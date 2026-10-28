package com.live.main.video.service;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.video.config.VideoConfigProperties;
import com.live.main.video.database.dto.*;
import com.live.main.video.database.dto.Upload.*;
import com.live.main.video.database.entity.UploadSessionEntity;
import com.live.main.video.database.entity.UploadSessionStatus;
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

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    }

    @Override
    public void abortMultipartUpload(AbortUploadRequest request, String userLoginId) {

    }

    @Override
    public void cleanupExpiredUploads() {

    }

    @Override
    public PendingVideo createPendingVideo(String channelName, String userLoginId, VideoDto request, ValidatedVideoInput input) {
        return null;
    }

    @Override
    public UploadContext loadUploadContext(Long videoId, String uploadId, String userLoginId) {
        return null;
    }

    @Override
    public boolean recoverCompletedUpload(UploadContext context, int completedPartCount) {
        return false;
    }

    @Override
    public void markUploadCompleted(Long videoId, String uploadId, int completedPartCount) {

    }

    @Override
    public void markUploadFailed(Long videoId, String uploadId) {

    }

    @Override
    public void markVideoFailed(Long videoId) {

    }

    @Override
    public void verifyUploadedObject(String objectKey, long expectedFileSize) {

    }

    @Override
    public void validateUploadOpen(UploadSessionStatus status) {

    }

    @Override
    public void validatePartNumbers(List<Integer> partNumbers, int totalPartCount) {

    }

    @Override
    public void validateCompleteParts(List<CompletePartRequest> parts, int totalPartCount) {

    }

    @Override
    public ValidatedVideoInput validateVideoInput(String channelName, String userLoginId, VideoDto request) {
        return null;
    }

    @Override
    public int calculateTotalPartCount(long fileSize) {
        return 0;
    }

    @Override
    public void safeAbort(String objectKey, String uploadId) {

    }

    @Override
    public UploadContext toContext(UploadSessionEntity session) {
        return null;
    }
}
