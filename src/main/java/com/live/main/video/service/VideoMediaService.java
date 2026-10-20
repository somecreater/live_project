package com.live.main.video.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.database.dto.VideoEncodingEvent;
import com.live.main.common.exception.CustomException;
import com.live.main.video.config.VideoConfigProperties;
import com.live.main.video.database.dto.VideoPlaybackResponse;
import com.live.main.video.database.entity.ProcessingStatus;
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

import java.nio.charset.StandardCharsets;
import java.util.Set;

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

    }

    /**
     * 트랜스코딩 완료 Kafka 이벤트를 검증하고 생성된 master.m3u8의 존재 여부를 확인한다.
     * 정상 결과라면 HLS Object Key를 저장하고 영상 상태를 READY로 변경한다.
     */
    @Override
    public void completeEncoding(VideoEncodingEvent event) {

    }

    /**
     * 재생 권한과 READY 상태를 확인한 뒤 Cloudflare CDN의 master.m3u8 URL을 반환한다.
     * Spring 서버가 Playlist나 세그먼트를 직접 중계하지 않는다.
     */
    @Override
    public VideoPlaybackResponse getPlaybackUrl(Long videoId, String userLoginId) {
        return null;
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
}
