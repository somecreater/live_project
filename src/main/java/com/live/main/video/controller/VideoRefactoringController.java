package com.live.main.video.controller;

import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.CustomUserDetails;
import com.live.main.video.database.dto.*;
import com.live.main.video.database.dto.Upload.MultipartUploadResponse;
import com.live.main.video.database.dto.Upload.SingleUploadResponse;
import com.live.main.video.service.VideoMediaService;
import com.live.main.video.service.VideoUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/video")
@RequiredArgsConstructor
@Slf4j
public class VideoRefactoringController {

    private final VideoUploadService videoUploadService;
    private final VideoMediaService videoMediaService;

    @PostMapping("/single")
    /**
     * 단일 PUT 방식 영상 업로드를 시작하고 Presigned URL을 반환한다.
     */
    public ResponseEntity<SingleUploadResponse> createSingleUpload(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam String channelName,
            @RequestParam String userLoginId,
            @Valid @RequestBody VideoDto request
    ) {
        if(principal.getUserid().compareTo(userLoginId) != 0){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                videoUploadService.createSingleUploadUrl(
                        channelName,
                        userLoginId,
                        request
                )
        );
    }

    @PostMapping("/multipart")
    /**
     * 대용량 영상용 Multipart Upload 세션을 생성한다.
     */
    public ResponseEntity<MultipartUploadResponse> createMultipart(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam String channelName,
            @RequestParam String userLoginId,
            @Valid @RequestBody VideoDto request
    ) {
        if(principal.getUserid().compareTo(userLoginId) != 0){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                videoUploadService.createMultipartUpload(
                        channelName,
                        userLoginId,
                        request
                )
        );
    }


    @PostMapping("/multipart/parts")
    /**
     * 요청한 Multipart Part 번호별 업로드 URL을 발급한다.
     */
    public ResponseEntity<List<PartPresignedUrlResponse>> createPartUrls(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam String userLoginId,
            @Valid @RequestBody PresignPartsRequest request
    ) {
        if(principal.getUserid().compareTo(userLoginId) != 0){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        return ResponseEntity.ok(
                videoUploadService.createPartUrls(
                        request,
                        userLoginId
                )
        );
    }

    @PostMapping("/multipart/complete")
    /**
     * 모든 Part 업로드가 끝난 Multipart Upload를 최종 완료 처리한다.
     */
    public ResponseEntity<Void> completeMultipart(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam String userLoginId,
            @Valid @RequestBody CompleteUploadRequest request
    ) {
        if(principal.getUserid().compareTo(userLoginId) != 0){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        videoUploadService.completeMultipartUpload(
                request,
                userLoginId
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/multipart/abort")
    /**
     * 진행 중인 Multipart Upload를 사용자가 명시적으로 취소한다.
     */
    public ResponseEntity<Void> abortMultipart(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam String userLoginId,
            @Valid @RequestBody AbortUploadRequest request
    ) {
        if(principal.getUserid().compareTo(userLoginId) != 0){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        videoUploadService.abortMultipartUpload(
                request,
                userLoginId
        );

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{videoId}/validate")
    /**
     * 업로드된 원본 영상의 기본 무결성을 검사하고 트랜스코딩 단계로 넘긴다.
     */
    public ResponseEntity<Void> validateUpload(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long videoId,
            @RequestParam String channelName,
            @RequestParam String userLoginId
    ) {
        if(principal.getUserid().compareTo(userLoginId) != 0){
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        videoMediaService.validateUploadedVideo(
                videoId,
                channelName,
                userLoginId
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{videoId}/playback")
    /**
     * 재생 가능한 영상의 Cloudflare CDN HLS 주소를 반환한다.
     */
    public ResponseEntity<VideoPlaybackResponse> getPlayback(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long videoId,
            @RequestParam(required = false) String userLoginId
    ) {
        return ResponseEntity.ok(
                videoMediaService.getPlaybackUrl(
                        videoId,
                        userLoginId
                )
        );
    }
}

