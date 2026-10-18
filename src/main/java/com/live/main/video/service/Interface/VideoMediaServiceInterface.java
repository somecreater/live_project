package com.live.main.video.service.Interface;

import com.live.main.common.database.dto.VideoEncodingEvent;
import com.live.main.video.database.dto.VideoPlaybackResponse;
import com.live.main.video.database.entity.ProcessingStatus;

/**
 * 동영상 검증, 동영상 트랜스코딩 완료 처리, 동영상 재생 기능
 * 2026-10-19 구현
 */
public interface VideoMediaServiceInterface {

    /**R2 원본 객체 검증 후 트랜스코딩 작업 이벤트를 준비*/
    /**
     * 이 부분은 추후 Kafka 이벤트 생산을 없애고,
     * AWS Batch Job Queue를 활용해서
     * 미디어 트랜스코딩 서버를 상시 가동이 아닌
     * 호출 방식으로 바꿀 예정입니다.
     **/
    public void validateUploadedVideo(Long videoId, String channelName, String userLoginId);

    /**Kafka Consumer가 트랜스코딩 완료 메시지를 받았을 때 호출*/
    public void completeEncoding(VideoEncodingEvent event);

    /**Spring이 m3u8을 읽고 재작성하지 않고 Cloudflare CDN URL만 반환*/
    public VideoPlaybackResponse getPlaybackUrl(Long videoId, String userLoginId);

    /**파일 앞부분의 ftyp 박스와 major brand를 확인해 MP4/MOV 계열 파일인지 빠르게 판별*/
    public boolean isValidMp4OrMov(byte[] bytes);

    /**영상 검증 API 호출에 필요한 videoId, 채널명, 사용자 식별값의 기본 유효성을 확인*/
    public void validateValidationRequest(Long videoId, String channelName, String userLoginId);

    /**Kafka로 전달된 트랜스코딩 완료 이벤트에 필수 식별자와 결과 경로가 포함됐는지 검사한다.*/
    public void validateEncodingEvent(VideoEncodingEvent event);

    /**R2 실제 파일 크기가 허용 범위 내이며 업로드 요청 당시 크기와 일치하는지 확인*/
    public void validateObjectSize(Long actualSize, long expectedSize);

    /**파일 형식 검증 실패 시 잘못된 원본 객체를 삭제하고 영상 상태를 FAILED로 변경*/
    public void failValidationAndDelete(ValidationContext context);

    /**영상 처리 과정에서 복구할 수 없는 오류가 발생했을 때 처리 상태를 FAILED로 기록*/
    public void markVideoFailed(Long videoId);

    /**R2 Object Prefix의 앞쪽 슬래시를 제거하고 끝에 슬래시가 하나 존재하도록 정규화*/
    public String normalizePrefix(String value);

    public record ValidationContext(
            Long videoId,
            String objectKey,
            long expectedFileSize,
            boolean alreadyProcessed
    ) {
    }

    public record EncodingContext(
            Long videoId,
            String originalObjectKey,
            String hlsObjectKey,
            ProcessingStatus processingStatus
    ) {
    }
}
