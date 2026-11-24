package com.live.main.video.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.database.dto.VideoEncodingEvent;
import com.live.main.common.database.dto.VideoValidationEvent;
import com.live.main.common.exception.CustomException;
import com.live.main.video.config.VideoConfigProperties;
import com.live.main.video.database.dto.VideoPlaybackResponse;
import com.live.main.video.database.entity.ProcessingStatus;
import com.live.main.video.database.entity.VideoEntity;
import com.live.main.video.database.repository.VideoRepository;
import com.live.main.video.service.Interface.ChannelAccessValidator;
import com.live.main.video.service.Interface.R2ServiceInterface;
import com.live.main.video.service.Interface.VideoMediaServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.util.UriUtils;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoMediaService implements VideoMediaServiceInterface {

    @Value("${app.file.video_minimum_size}")
    private Long MINIMUM_FILE_SIZE;
    @Value("${app.file.video_header_end_offset}")
    private Long HEADER_END_OFFSET;

    private static final Set<String> SUPPORTED_BRANDS = Set.of(
            "isom",
            "iso2",
            "mp41",
            "mp42",
            "avc1",
            "M4V ",
            "MSNV",
            "qt  "
    );

    private final R2ServiceInterface r2service;
    private final VideoRepository videoRepository;
    private final ChannelAccessValidator accessValidator;
    private final VideoConfigProperties configProperties;
    private final ApplicationEventPublisher eventPublisher;
    private final TransactionTemplate transactionTemplate;


    /**
     * R2에 업로드된 원본 영상의 존재 여부, 크기, MP4/MOV 기본 헤더를 검증한다.
     * 검증 성공 시 상태를 TRANSCODING으로 변경하고 트랜스코딩 요청 이벤트를 발행한다.
     */
    @Override
    public void validateUploadedVideo(Long videoId, String channelName, String userLoginId) {
        validateValidationRequest(
                videoId,
                channelName,
                userLoginId
        );

        ValidationContext context= transactionTemplate.execute(status -> {
            VideoEntity video = videoRepository
                    .findById(videoId)
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            accessValidator.validateVideoOwner(
                    video,
                    userLoginId
            );


            if (video.getChannelEntity() == null
                    || !Objects.equals(
                    video.getChannelEntity().getName(),
                    channelName
            )) {
                /* 요청 불일치는 데이터 삭제 사유가 아닙니다. */
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            ProcessingStatus currentStatus = video.getProcessingStatus();

            if (currentStatus == ProcessingStatus.DELETED) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            boolean alreadyProcessed =
                    currentStatus == ProcessingStatus.VALIDATING
                            || currentStatus == ProcessingStatus.TRANSCODING
                            || currentStatus == ProcessingStatus.READY;

            if (alreadyProcessed) {
                return new ValidationContext(
                        video.getId(),
                        video.getOriginalObjectKey(),
                        video.getExpectedFileSize(),
                        true
                );
            }

            // 검증 시작이 가능한 상태만 허용
            if (currentStatus != ProcessingStatus.UPLOADING
                    && currentStatus != ProcessingStatus.UPLOADED) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            // DB에서 원자적으로 VALIDATING 상태 선점
            int updated = videoRepository.updateProcessingStatusIfAllowed(
                    video.getId(),
                    video.getOriginalObjectKey(),
                    List.of(
                            ProcessingStatus.UPLOADING,
                            ProcessingStatus.UPLOADED
                    ),
                    ProcessingStatus.VALIDATING
            );

            log.info(
                    "Validation claim. videoId={}, previousStatus={}, updated={}",
                    video.getId(),
                    currentStatus,
                    updated
            );

            return new ValidationContext(
                    video.getId(),
                    video.getOriginalObjectKey(),
                    video.getExpectedFileSize(),
                    updated != 1
            );
        });

        if (context == null) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        if (context.alreadyProcessed()) {
            return;
        }

        try {
            HeadObjectResponse head = r2service.headObject(
                    context.objectKey()
            );

            validateObjectSize(
                    head.contentLength(),
                    context.expectedFileSize()
            );

            byte[] header = r2service.readRange(
                    context.objectKey(),
                    0,
                    HEADER_END_OFFSET
            );

            if (!isValidMp4OrMov(header)) {
                failValidationAndDelete(context);
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

        } catch (CustomException exception) {

            markValidationFailed(
                    context.videoId(),
                    context.objectKey()
            );

            throw exception;

        } catch (S3Exception exception) {
            markVideoFailed(context.videoId());

            log.error(
                    "R2 파일 검증 실패. videoId={}, key={}, status={}",
                    context.videoId(),
                    context.objectKey(),
                    exception.statusCode(),
                    exception
            );

            if (exception.statusCode() == 404) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            markValidationFailed(
                    context.videoId(),
                    context.objectKey()
            );

            throw new CustomException(ErrorCode.SERVER_ERROR);

        } catch (Exception exception) {
            markValidationFailed(
                    context.videoId(),
                    context.objectKey()
            );
            markVideoFailed(context.videoId());

            log.error(
                    "동영상 검증 실패. videoId={}, key={}",
                    context.videoId(),
                    context.objectKey(),
                    exception
            );

            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        /*
         * 상태 변경과 애플리케이션 이벤트 발행은 동일 DB 트랜잭션에서 수행합니다.
         * VideoKafkaAdapter가 AFTER_COMMIT 시점에 Kafka로 전송합니다.
         */
        transactionTemplate.executeWithoutResult(status -> {

            // VALIDATING 상태인 경우에만 TRANSCODING으로 전환
            int updated = videoRepository.updateProcessingStatusIfAllowed(
                    context.videoId(),
                    context.objectKey(),
                    List.of(ProcessingStatus.VALIDATING),
                    ProcessingStatus.TRANSCODING
            );

            // 상태 변경에 실패하면 이벤트 발행하지 않음
            if (updated != 1) {
                log.warn(
                        "Transcoding event skipped. videoId={}, updated={}",
                        context.videoId(),
                        updated
                );
                return;
            }

            log.info(
                    "Transcoding event scheduled. videoId={}, objectKey={}",
                    context.videoId(),
                    context.objectKey()
            );

            /*
            * 이 아래 부분은 추후 AWS Batch Job Queue로 변경할 예정 입니다.
            * */
            eventPublisher.publishEvent(
                    new VideoValidationEvent(
                            context.videoId(),
                            context.objectKey()
                    )
            );
        });
    }

    /**
     * 트랜스코딩 완료 Kafka 이벤트를 검증하고 생성된 master.m3u8의 존재 여부를 확인한다.
     * 정상 결과라면 HLS Object Key를 저장하고 영상 상태를 READY로 변경한다.
     */
    @Override
    public void completeEncoding(VideoEncodingEvent event) {
        validateEncodingEvent(event);

        EncodingContext context = transactionTemplate.execute(status->{
            VideoEntity video = videoRepository
                    .findById(event.getVideoId())
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );


            return new EncodingContext(
                    video.getId(),
                    video.getOriginalObjectKey(),
                    video.getHlsObjectKey(),
                    video.getProcessingStatus()
            );
        });

        if (context == null) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        if (!Objects.equals(
                context.originalObjectKey(),
                event.getObjectKey()
        )) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String channelName = extractChannelNameFromObjectKey(
                context.originalObjectKey(),
                event.getVideoId()
        );
        String resultPrefix = normalizePrefix(
                event.getResultKey()
        );
        String expectedPrefix =
                configProperties.normalizedTranscodingPrefix()
                        + "channels/"
                        + channelName
                        + "/videos/"
                        + event.getVideoId()
                        + "/";

        if (!resultPrefix.startsWith(expectedPrefix)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String masterObjectKey =
                resultPrefix + "master.m3u8";

        if (context.processingStatus()
                == ProcessingStatus.READY) {
            if (Objects.equals(
                    context.hlsObjectKey(),
                    masterObjectKey
            )) {
                log.info(
                        "Encoding completion already processed. videoId={}",
                        event.getVideoId()
                );
                return;
            }

            log.warn(
                    "Duplicate encoding result ignored. videoId={}, existing={}, incoming={}",
                    event.getVideoId(),
                    context.hlsObjectKey(),
                    masterObjectKey
            );
        }

        HeadObjectResponse head = r2service.headObject(
                masterObjectKey
        );

        if (head.contentLength() == null
                || head.contentLength() <= 0) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        transactionTemplate.executeWithoutResult(status -> {
            VideoEntity video = videoRepository
                    .findById(event.getVideoId())
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            if (!Objects.equals(
                    video.getOriginalObjectKey(),
                    event.getObjectKey()
            )) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            if (video.getProcessingStatus()
                    == ProcessingStatus.READY
                    && Objects.equals(
                    video.getHlsObjectKey(),
                    masterObjectKey
            )) {
                return;
            }

            if (video.getProcessingStatus()
                    != ProcessingStatus.TRANSCODING) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
            int updated = videoRepository.completeEncodingIfTranscoding(
                    event.getVideoId(),
                    event.getObjectKey(),
                    masterObjectKey,
                    ProcessingStatus.TRANSCODING,
                    ProcessingStatus.READY);

            if (updated == 1) {
                log.info(
                        "Video encoding completed. videoId={}, hlsObjectKey={}",
                        event.getVideoId(),
                        masterObjectKey
                );
                return;
            }

            // 상태 변경 실패 시 현재 DB 상태 재조회
            VideoEntity video_validate = videoRepository.findById(event.getVideoId())
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            // 동일한 완료 이벤트가 다시 전달된 경우
            if (video_validate.getProcessingStatus() == ProcessingStatus.READY
                    && Objects.equals(video_validate.getHlsObjectKey(), masterObjectKey)) {
                log.info(
                        "Encoding already completed. videoId={}",
                        event.getVideoId()
                );
                return;
            }

            // 다른 HLS 결과가 이미 등록됐거나 상태가 유효하지 않은 경우
            log.warn(
                    "Encoding completion rejected. videoId={}, status={}, incomingKey={}",
                    event.getVideoId(),
                    video_validate.getProcessingStatus(),
                    masterObjectKey
            );

            throw new CustomException(ErrorCode.BAD_REQUEST);


        });
    }

    /**
     * 재생 권한과 READY 상태를 확인한 뒤 Cloudflare CDN의 master.m3u8 URL을 반환한다.
     * Spring 서버가 Playlist나 세그먼트를 직접 중계하지 않는다.
     */
    @Override
    public VideoPlaybackResponse getPlaybackUrl(Long videoId, String userLoginId) {
        VideoPlaybackResponse response = transactionTemplate.execute(status ->{
            VideoEntity video = videoRepository
                    .findById(videoId)
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            if (video.getProcessingStatus()
                    != ProcessingStatus.READY) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }


            accessValidator.validatePlaybackPermission(
                    video,
                    userLoginId
            );
            String hlsObjectKey = video.getHlsObjectKey();

            String encodedObjectKey = Arrays.stream(
                            hlsObjectKey.split("/")
                    )
                    .map(segment ->
                            UriUtils.encodePathSegment(
                                    segment,
                                    StandardCharsets.UTF_8
                            )
                    )
                    .collect(Collectors.joining("/"));

            if (hlsObjectKey == null || hlsObjectKey.isBlank()) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            return new VideoPlaybackResponse(
                    video.getId(),
                    configProperties.normalizedCdnBaseUrl()
                            + "/"
                            + encodedObjectKey
            );
        });

        if (response == null) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        return response;
    }

    @Override
    public boolean isValidMp4OrMov(byte[] bytes) {
        if (bytes == null || bytes.length < 12) {
            return false;
        }

        int max = Math.min(bytes.length - 8, 256);

        for (int index = 0; index < max; index++) {
            boolean ftyp =
                    bytes[index] == 'f'
                            && bytes[index + 1] == 't'
                            && bytes[index + 2] == 'y'
                            && bytes[index + 3] == 'p';

            if (!ftyp) {
                continue;
            }

            if (index + 8 > bytes.length) {
                return false;
            }

            String brand = new String(
                    bytes,
                    index + 4,
                    4,
                    StandardCharsets.US_ASCII
            );

            return SUPPORTED_BRANDS.contains(brand);
        }

        return false;
    }

    @Override
    public void validateValidationRequest(Long videoId, String channelName, String userLoginId) {
        if (videoId == null
                || channelName == null
                || channelName.isBlank()
                || userLoginId == null
                || userLoginId.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

    }

    @Override
    public void validateEncodingEvent(VideoEncodingEvent event) {
        if (event == null
                || event.getVideoId() == null
                || event.getObjectKey() == null
                || event.getObjectKey().isBlank()
                || event.getResultKey() == null
                || event.getResultKey().isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public void validateObjectSize(Long actualSize, long expectedSize) {
        if (actualSize == null
                || actualSize < MINIMUM_FILE_SIZE
                || actualSize > configProperties.videoLimitSize()
                || actualSize != expectedSize) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public void failValidationAndDelete(ValidationContext context) {
        try {
            r2service.deleteObject(context.objectKey());
        } catch (Exception exception) {
            log.error(
                    "검증 실패 객체 삭제 실패. videoId={}, key={}",
                    context.videoId(),
                    context.objectKey(),
                    exception
            );
        }

        markVideoFailed(context.videoId());

    }

    @Override
    public void markVideoFailed(Long videoId) {
        transactionTemplate.executeWithoutResult(status ->
                videoRepository.findById(videoId)
                        .ifPresent(video ->
                                video.setProcessingStatus(
                                        ProcessingStatus.FAILED
                                )
                        )
        );
    }

    @Override
    public String normalizePrefix(String value) {
        String normalized = value.startsWith("/")
                ? value.substring(1)
                : value;

        return normalized.endsWith("/")
                ? normalized
                : normalized + "/";
    }

    @Override
    public String extractChannelNameFromObjectKey(String objectKey, Long videoId){
        String expectedPrefix =
                configProperties.normalizedOriginalPrefix()
                        + "channels/";

        if (objectKey == null
                || !objectKey.startsWith(expectedPrefix)) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        String relativePath =
                objectKey.substring(expectedPrefix.length());

        String[] parts = relativePath.split("/", -1);

        // {channelName}/videos/{videoId}/{fileName}
        if (parts.length != 4
                || parts[0].isBlank()
                || !parts[1].equals("videos")
                || !parts[2].equals(String.valueOf(videoId))
                || parts[3].isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return parts[0];
    }

    @Override
    public void markValidationFailed(Long videoId, String objectKey) {

        transactionTemplate.executeWithoutResult(status -> {
            videoRepository.updateProcessingStatusIfAllowed(
                    videoId,
                    objectKey,
                    List.of(ProcessingStatus.VALIDATING),
                    ProcessingStatus.FAILED
            );
        });
    }
}
