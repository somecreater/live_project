package com.live.main.video.service.Interface;

import com.live.main.video.database.dto.*;
import com.live.main.video.database.dto.Upload.*;
import com.live.main.video.database.entity.UploadSessionEntity;
import com.live.main.video.database.entity.UploadSessionStatus;

import java.util.List;

/**동영상 파일 업로드 기능(2026-07-22)*/
public interface VideoUploadServiceInterface {

    /**소용량 영상을 단일 PUT 방식으로 업로드할 수 있도록 R2 Presigned URL을 생성*/
    public SingleUploadResponse createSingleUploadUrl(String channelName, String userLoginId, VideoDto request);

    //대용량 영상을 위한 메소드 목록
    /**대용량 영상 업로드를 위한 Multipart Upload 세션을 생성*/
    public MultipartUploadResponse createMultipartUpload(String channelName, String userLoginId, VideoDto request);

    /**Multipart Upload에서 지정된 Part 번호에 대한 Presigned URL을 발급*/
    public List<PartPresignedUrlResponse> createPartUrls(PresignPartsRequest request, String userLoginId);

    /**모든 Part 업로드가 끝난 Multipart Upload를 R2에서 완료 처리*/
    public void completeMultipartUpload(CompleteUploadRequest request, String userLoginId);

    /**진행 중인 Multipart Upload를 취소하고 업로드 세션을 ABORTED 상태로 변경*/
    public void abortMultipartUpload(AbortUploadRequest request, String userLoginId);

    //전처리, 후처리 메소드
    /**만료된 Multipart Upload 세션을 조회해 R2에서 정리하고 DB 상태를 EXPIRED로 변경*/
    public void cleanupExpiredUploads();

    /**업로드 시작 전 VideoEntity를 생성하고 변경되지 않는 원본 R2 Object Key를 할당*/
    public PendingVideo createPendingVideo(String channelName, String userLoginId, VideoDto request, ValidatedVideoInput input);

    /**업로드 세션과 영상을 조회하고 현재 사용자의 영상 소유 권한을 확인한 뒤 필요한 값만 반환*/
    public UploadContext loadUploadContext(Long videoId, String uploadId, String userLoginId);

    /**R2에서는 완료됐지만 DB 상태 갱신이 실패한 Multipart Upload를 재시도 시 복구*/
    public boolean recoverCompletedUpload(UploadContext context, int completedPartCount);

    /**Multipart 세션을 COMPLETED로, 영상 처리 상태를 UPLOADED로 변경*/
    public void markUploadCompleted(Long videoId, String uploadId, int completedPartCount);


    //실패 처리
    /**업로드 처리 중 오류가 발생했을 때 세션과 영상 상태를 FAILED로 기록*/
    public void markUploadFailed(Long videoId, String uploadId);

    /**업로드 세션이 없는 단일 업로드 등의 실패 상황에서 영상 상태만 FAILED로 변경*/
    public void markVideoFailed(Long videoId);


    //검증 절차
    /**R2 HEAD 요청으로 실제 객체 크기를 조회해 클라이언트가 선언한 파일 크기와 일치하는지 확인*/
    public void verifyUploadedObject(String objectKey, long expectedFileSize);

    /**완료·취소·만료된 세션에 추가 업로드 작업이 수행되지 않도록 현재 세션 상태를 검사*/
    public void validateUploadOpen(UploadSessionStatus status);

    /**Presigned URL 발급 대상 Part 번호가 범위 내에 있고 중복되지 않았는지 확인*/
    public void validatePartNumbers(List<Integer> partNumbers, int totalPartCount);

    /**Multipart 완료 요청에 1번부터 마지막 Part까지 모든 Part와 ETag가 포함됐는지 검증*/
    public void validateCompleteParts(List<CompletePartRequest> parts, int totalPartCount);

    /**영상 제목, 파일 크기, 파일 형식 등 업로드 요청의 기본 입력값을 검증하고 MIME 타입을 결정*/
    public ValidatedVideoInput validateVideoInput(String channelName, String userLoginId, VideoDto request);


    //기타
    /**전체 파일 크기와 설정된 Part 크기를 기준으로 필요한 Multipart Part 개수를 계산*/
    public int calculateTotalPartCount(long fileSize);

    /**세션 생성 도중 오류가 발생했을 때 보상 작업으로 R2 Multipart Upload를 안전하게 취소*/
    public void safeAbort(String objectKey, String uploadId);

    /**JPA 엔티티를 외부 네트워크 작업에 필요한 불변 업로드 컨텍스트로 변환*/
    public UploadContext toContext(UploadSessionEntity session);
}
