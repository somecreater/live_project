package com.live.main.video.service;

import com.live.main.video.database.dto.*;
import com.live.main.video.database.dto.Upload.*;
import com.live.main.video.database.entity.UploadSessionEntity;
import com.live.main.video.database.entity.UploadSessionStatus;
import com.live.main.video.service.Interface.VideoUploadServiceInterface;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoUploadService implements VideoUploadServiceInterface {
    @Override
    public SingleUploadResponse createSingleUploadUrl(String channelName, String userLoginId, VideoDto request) {
        return null;
    }

    @Override
    public MultipartUploadResponse createMultipartUpload(String channelName, String userLoginId, VideoDto request) {
        return null;
    }

    @Override
    public List<PartPresignedUrlResponse> createPartUrls(PresignPartsRequest request, String userLoginId) {
        return List.of();
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
