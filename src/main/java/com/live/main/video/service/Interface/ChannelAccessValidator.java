package com.live.main.video.service.Interface;

import com.live.main.video.database.entity.VideoEntity;

/**
 * 1. channelName 문자열만 비교하지 말고 로그인 사용자가 채널 소유자인지 검사합니다.
 * 2. 재생 시 PUBLIC은 허용하고 PRIVATE/UNLISTED는 소유자 또는 권한 보유자만 허용합니다.
 */
public interface ChannelAccessValidator {
    void validateChannelOwner(String channelName, String userLoginId);

    void validateVideoOwner(VideoEntity video, String userLoginId);

    void validatePlaybackPermission(VideoEntity video, String userLoginId);

}
