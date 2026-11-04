package com.live.main.video.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.video.config.VideoConfigProperties;
import com.live.main.video.database.dto.*;
import com.live.main.video.database.dto.Upload.*;
import com.live.main.video.database.entity.*;
import com.live.main.video.database.mapper.VideoMapper;
import com.live.main.video.database.repository.UploadSessionRepository;
import com.live.main.video.database.repository.VideoRepository;
import com.live.main.video.service.Interface.ChannelAccessValidator;
import com.live.main.video.service.Interface.VideoUploadServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VideoUploadService implements VideoUploadServiceInterface {

    @Value("${app.file.multi_part_urls_length}")
    private int MAX_PART_URLS_PER_REQUEST;

    private final R2Service r2Service;
    private final VideoRepository videoRepository;
    private final UploadSessionRepository uploadSessionRepository;
    private final VideoMapper videoMapper;
    private final ChannelAccessValidator channelAccessValidator;
    private final VideoConfigProperties properties;
    private final TransactionTemplate transactionTemplate;


    @Override
    public SingleUploadResponse createSingleUploadUrl(String channelName, String userLoginId, VideoDto request) {
        ValidatedVideoInput input = validateVideoInput(
                channelName,
                userLoginId,
                request
        );

        PendingVideo pendingVideo = createPendingVideo(
                channelName,
                userLoginId,
                request,
                input
        );

        try{
            URL uploadUrl = r2Service.createPutPresignedUrl(
                    pendingVideo.objectKey(),
                    pendingVideo.contentType()
            );

            return new SingleUploadResponse(
                    pendingVideo.videoId(),
                    uploadUrl.toString(),
                    LocalDateTime.now().plusMinutes(
                            properties.videoUploadUrlDuration()
                    )
            );

        } catch (Exception exception) {
            markVideoFailed(pendingVideo.videoId());

            log.error(
                    "단일 업로드 URL 생성 실패. videoId={}, key={}",
                    pendingVideo.videoId(),
                    pendingVideo.objectKey(),
                    exception
            );

            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public MultipartUploadResponse createMultipartUpload(String channelName, String userLoginId, VideoDto request) {
        ValidatedVideoInput input = validateVideoInput(
                channelName,
                userLoginId,
                request
        );

        PendingVideo pendingVideo = createPendingVideo(
                channelName,
                userLoginId,
                request,
                input
        );

        String uploadId = null;

        try{
            uploadId = r2Service.createMultipartUpload(
                    pendingVideo.objectKey(),
                    pendingVideo.contentType()
            );

            int totalPartCount = calculateTotalPartCount(
                    pendingVideo.expectedFileSize()
            );

            String finalUploadId = uploadId;
            transactionTemplate.executeWithoutResult(status->{
                UploadSessionEntity session = new UploadSessionEntity();
                session.setUploadId(finalUploadId);
                session.setVideoId(pendingVideo.videoId());
                session.setObjectKey(pendingVideo.objectKey());
                session.setStatus(UploadSessionStatus.INITIATED);
                session.setTotalPartCount(totalPartCount);
                session.setCompletedPartCount(0);
                session.setPartSize(properties.multiPartSize());
                session.setExpectedFileSize(pendingVideo.expectedFileSize());
                session.setExpiresAt(LocalDateTime.now().plusHours(24));
                uploadSessionRepository.save(session);
            });

            return new MultipartUploadResponse(
                    pendingVideo.videoId(),
                    uploadId,
                    properties.multiPartSize(),
                    totalPartCount
            );

        }catch (Exception exception){
            if (uploadId != null) {
                safeAbort(pendingVideo.objectKey(), uploadId);
            }

            markVideoFailed(pendingVideo.videoId());

            log.error(
                    "Multipart 생성 실패. videoId={}, uploadId={}",
                    pendingVideo.videoId(),
                    uploadId,
                    exception
            );

            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public List<PartPresignedUrlResponse> createPartUrls(PresignPartsRequest request, String userLoginId) {
        UploadContext context = loadUploadContext(
                request.getVideoId(),
                request.getUploadId(),
                userLoginId
        );

        validateUploadOpen(context.status());
        validatePartNumbers(
                request.getPartNumbers(),
                context.totalPartCount()
        );

        try{
            List<PartPresignedUrlResponse> result =
                    new ArrayList<>(request.getPartNumbers().size());

            for (Integer partNumber : request.getPartNumbers()) {
                URL uploadUrl = r2Service.createPartPresignedUrl(
                        context.objectKey(),
                        context.uploadId(),
                        partNumber
                );

                result.add(new PartPresignedUrlResponse(
                        partNumber,
                        uploadUrl.toString()
                ));
            }

            transactionTemplate.executeWithoutResult(status ->
                    uploadSessionRepository
                            .findByUploadIdAndVideoId(
                                    context.uploadId(),
                                    context.videoId()
                            )
                            .ifPresent(session -> {
                                if (session.getStatus()
                                        == UploadSessionStatus.INITIATED) {
                                    session.setStatus(
                                            UploadSessionStatus.UPLOADING
                                    );
                                }
                            })
            );

            return result;


        } catch (Exception e) {
            /*
             * Part URL 발급 실패는 재시도 가능한 오류이므로
             * 이미 업로드된 Part를 전부 Abort하지 않습니다.
             */
            log.error(
                    "Part URL 생성 실패. videoId={}, uploadId={}",
                    context.videoId(),
                    context.uploadId(),
                    e
            );

            throw new CustomException(ErrorCode.SERVER_ERROR);
        }
    }

    @Override
    public void completeMultipartUpload(CompleteUploadRequest request, String userLoginId) {
        UploadContext current = loadUploadContext(
                request.getVideoId(),
                request.getUploadId(),
                userLoginId
        );

        if (current.status() == UploadSessionStatus.COMPLETED) {
            return;
        }

        validateCompleteParts(
                request.getParts(),
                current.totalPartCount()
        );

        if ((current.status() == UploadSessionStatus.COMPLETING
                || current.status() == UploadSessionStatus.FAILED)
                && recoverCompletedUpload(
                current,
                request.getParts().size()
        )) {
            return;
        }

        UploadContext completing = transactionTemplate.execute(status -> {
            UploadSessionEntity session = uploadSessionRepository
                    .findByUploadIdAndVideoIdForUpdate(
                            request.getUploadId(),
                            request.getVideoId()
                    )
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            VideoEntity video = videoRepository
                    .findById(request.getVideoId())
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            channelAccessValidator.validateVideoOwner(
                    video,
                    userLoginId
            );

            validateUploadOpen(session.getStatus());
            session.setStatus(UploadSessionStatus.COMPLETING);

            return toContext(session);
        });

        if (completing == null) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        try{
            r2Service.completeMultipartUpload(
                    completing.objectKey(),
                    completing.uploadId(),
                    request.getParts()
            );

            verifyUploadedObject(
                    completing.objectKey(),
                    completing.expectedFileSize()
            );
        } catch (Exception exception) {
            markUploadFailed(
                    completing.videoId(),
                    completing.uploadId()
            );

            log.error(
                    "Multipart 완료 실패. videoId={}, uploadId={}",
                    completing.videoId(),
                    completing.uploadId(),
                    exception
            );

            throw new CustomException(ErrorCode.SERVER_ERROR);

        }

        markUploadCompleted(
                completing.videoId(),
                completing.uploadId(),
                request.getParts().size()
        );

    }

    @Override
    public void abortMultipartUpload(AbortUploadRequest request, String userLoginId) {
        UploadContext context = loadUploadContext(
                request.getVideoId(),
                request.getUploadId(),
                userLoginId
        );

        if (context.status() == UploadSessionStatus.ABORTED) {
            return;
        }

        if (context.status() == UploadSessionStatus.COMPLETED) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }


        try {
            r2Service.abortMultipartUpload(
                    context.objectKey(),
                    context.uploadId()
            );
        } catch (S3Exception exception) {
            if (exception.statusCode() != 404) {
                throw exception;
            }
        }
        transactionTemplate.executeWithoutResult(status->{
            UploadSessionEntity session = uploadSessionRepository
                    .findByUploadIdAndVideoIdForUpdate(
                            context.uploadId(),
                            context.videoId()
                    )
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            if (session.getStatus() != UploadSessionStatus.COMPLETED) {
                session.setStatus(UploadSessionStatus.ABORTED);
                session.setAbortedAt(LocalDateTime.now());
            }

            videoRepository.findById(context.videoId())
                    .ifPresent(video ->
                            video.setProcessingStatus(
                                    ProcessingStatus.FAILED
                            )
                    );
        });
    }

    @Override
    public void cleanupExpiredUploads() {
        List<UploadContext> expired = transactionTemplate.execute(status ->
                uploadSessionRepository
                        .findTop100ByStatusInAndExpiresAtBeforeOrderByExpiresAtAsc(
                                List.of(
                                        UploadSessionStatus.INITIATED,
                                        UploadSessionStatus.UPLOADING,
                                        UploadSessionStatus.FAILED
                                ),
                                LocalDateTime.now()
                        )
                        .stream()
                        .map(this::toContext)
                        .toList()
        );

        if (expired == null || expired.isEmpty()) {
            return;
        }

        for (UploadContext context : expired) {
            try {
                r2Service.abortMultipartUpload(
                        context.objectKey(),
                        context.uploadId()
                );
            } catch (Exception exception) {
                log.warn(
                        "만료 Multipart R2 정리 실패. videoId={}, uploadId={}",
                        context.videoId(),
                        context.uploadId(),
                        exception
                );
            }


            transactionTemplate.executeWithoutResult(status -> {
                uploadSessionRepository
                        .findByUploadIdAndVideoId(
                                context.uploadId(),
                                context.videoId()
                        )
                        .ifPresent(session -> {
                            if (session.getStatus()
                                    != UploadSessionStatus.COMPLETED) {
                                session.setStatus(
                                        UploadSessionStatus.EXPIRED
                                );
                            }
                        });

                videoRepository.findById(context.videoId())
                        .ifPresent(video -> {
                            if (video.getProcessingStatus()
                                    != ProcessingStatus.READY) {
                                video.setProcessingStatus(
                                        ProcessingStatus.FAILED
                                );
                            }
                        });
            });
        }
    }

    @Override
    public PendingVideo createPendingVideo(String channelName, String userLoginId, VideoDto request, ValidatedVideoInput input) {
        PendingVideo result = transactionTemplate.execute(status -> {
            channelAccessValidator.validateChannelOwner(
                    channelName,
                    userLoginId
            );

            request.setChannel_name(channelName);

            VideoEntity video = videoMapper.toEntity(request);
            video.setVisibility(Visibility.PRIVATE);
            video.setProcessingStatus(ProcessingStatus.CREATED);
            video.setExpectedFileSize(request.getSize());

            VideoEntity saved = videoRepository.save(video);

            String objectKey =
                    properties.normalizedOriginalPrefix()
                            + "videos/"
                            + saved.getId()
                            + "/"
                            + UUID.randomUUID()
                            + "."
                            + input.extension();

            saved.setOriginalObjectKey(objectKey);
            saved.setProcessingStatus(ProcessingStatus.UPLOADING);

            return new PendingVideo(
                    saved.getId(),
                    objectKey,
                    request.getSize(),
                    input.contentType()
            );

        });

        if (result == null) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        return result;
    }

    @Override
    public UploadContext loadUploadContext(Long videoId, String uploadId, String userLoginId) {
        UploadContext result = transactionTemplate.execute(status -> {
            UploadSessionEntity session = uploadSessionRepository
                    .findByUploadIdAndVideoId(uploadId, videoId)
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            VideoEntity video = videoRepository
                    .findById(videoId)
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            channelAccessValidator.validateVideoOwner(
                    video,
                    userLoginId
            );

            return toContext(session);
        });

        if (result == null) {
            throw new CustomException(ErrorCode.SERVER_ERROR);
        }

        return result;
    }

    @Override
    public boolean recoverCompletedUpload(UploadContext context, int completedPartCount) {
        if (!r2Service.objectExists(context.objectKey())) {
            return false;
        }

        verifyUploadedObject(
                context.objectKey(),
                context.expectedFileSize()
        );

        markUploadCompleted(
                context.videoId(),
                context.uploadId(),
                completedPartCount
        );

        return true;
    }

    @Override
    public void markUploadCompleted(Long videoId, String uploadId, int completedPartCount) {
        transactionTemplate.executeWithoutResult(status -> {
            UploadSessionEntity session = uploadSessionRepository
                    .findByUploadIdAndVideoIdForUpdate(
                            uploadId,
                            videoId
                    )
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            VideoEntity video = videoRepository
                    .findById(videoId)
                    .orElseThrow(() ->
                            new CustomException(ErrorCode.BAD_REQUEST)
                    );

            session.setStatus(UploadSessionStatus.COMPLETED);
            session.setCompletedPartCount(completedPartCount);
            session.setCompletedAt(LocalDateTime.now());
            video.setProcessingStatus(ProcessingStatus.UPLOADED);
        });

    }

    @Override
    public void markUploadFailed(Long videoId, String uploadId) {
        transactionTemplate.executeWithoutResult(status -> {
            if (uploadId != null && !uploadId.isBlank()) {
                uploadSessionRepository
                        .findByUploadIdAndVideoId(uploadId, videoId)
                        .ifPresent(session -> {
                            if (session.getStatus()
                                    != UploadSessionStatus.COMPLETED) {
                                session.setStatus(
                                        UploadSessionStatus.FAILED
                                );
                            }
                        });
            }

            videoRepository.findById(videoId)
                    .ifPresent(video ->
                            video.setProcessingStatus(
                                    ProcessingStatus.FAILED
                            )
                    );
        });

    }

    @Override
    public void markVideoFailed(Long videoId) {
        markUploadFailed(videoId, null);
    }

    @Override
    public void verifyUploadedObject(String objectKey, long expectedFileSize) {
        HeadObjectResponse head = r2Service.headObject(objectKey);
        Long actualFileSize = head.contentLength();

        if (actualFileSize == null
                || actualFileSize <= 0
                || actualFileSize != expectedFileSize) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

    }

    @Override
    public void validateUploadOpen(UploadSessionStatus status) {
        if (status == UploadSessionStatus.COMPLETED
                || status == UploadSessionStatus.ABORTED
                || status == UploadSessionStatus.EXPIRED) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public void validatePartNumbers(List<Integer> partNumbers, int totalPartCount) {
        if (partNumbers == null
                || partNumbers.isEmpty()
                || partNumbers.size() > MAX_PART_URLS_PER_REQUEST) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        Set<Integer> unique = new HashSet<>();

        for (Integer partNumber : partNumbers) {
            if (partNumber == null
                    || partNumber < 1
                    || partNumber > totalPartCount
                    || !unique.add(partNumber)) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
        }
    }

    @Override
    public void validateCompleteParts(List<CompletePartRequest> parts, int totalPartCount) {
        if (parts == null || parts.size() != totalPartCount) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        List<CompletePartRequest> sorted = parts.stream()
                .sorted(Comparator.comparingInt(
                        CompletePartRequest::getPartNumber
                ))
                .toList();

        for (int index = 0; index < sorted.size(); index++) {
            CompletePartRequest part = sorted.get(index);
            int expectedPartNumber = index + 1;

            if (part.getPartNumber() == null
                    || part.getPartNumber() != expectedPartNumber
                    || part.getEtag() == null
                    || part.getEtag().isBlank()) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }
        }

    }

    @Override
    public ValidatedVideoInput validateVideoInput(String channelName, String userLoginId, VideoDto request) {
        if (channelName == null
                || channelName.isBlank()
                || userLoginId == null
                || userLoginId.isBlank()
                || request == null
                || request.getTitle() == null
                || request.getTitle().isBlank()
                || request.getSize() == null
                || request.getSize() <= 0
                || request.getSize() > properties.videoLimitSize()
                || request.getFile_type() == null
                || request.getFile_type().isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return switch (request.getFile_type().toLowerCase()) {
            case "mp4", "video/mp4" -> new ValidatedVideoInput("mp4", "video/mp4");

            case "mov", "video/quicktime" -> new ValidatedVideoInput("mov", "video/quicktime");

            default -> throw new CustomException(ErrorCode.BAD_REQUEST);
        };
    }

    @Override
    public int calculateTotalPartCount(long fileSize) {
        long partSize = properties.multiPartSize();
        long partCount = (fileSize + partSize - 1) / partSize;

        if (partCount <= 0 || partCount > 10_000) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        return Math.toIntExact(partCount);
    }

    @Override
    public void safeAbort(String objectKey, String uploadId) {
        try {
            r2Service.abortMultipartUpload(objectKey, uploadId);
        } catch (Exception exception) {
            log.error(
                    "Multipart 보상 취소 실패. uploadId={}",
                    uploadId,
                    exception
            );
        }
    }

    @Override
    public UploadContext toContext(UploadSessionEntity session) {
        return new UploadContext(
                session.getVideoId(),
                session.getUploadId(),
                session.getObjectKey(),
                session.getTotalPartCount(),
                session.getExpectedFileSize(),
                session.getStatus()
        );
    }
}
