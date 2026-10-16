package com.live.main.video.service;

import com.live.main.channel.database.entity.ChannelEntity;
import com.live.main.channel.database.repository.ChannelRepository;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.common.service.Interface.CommonServiceInterface;
import com.live.main.video.database.entity.VideoEntity;
import com.live.main.video.database.entity.Visibility;
import com.live.main.video.service.Interface.ChannelAccessValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChannelAccessValidate implements ChannelAccessValidator {

    private final CommonServiceInterface commonService;
    private final ChannelRepository channelRepository;
    @Override
    public void validateChannelOwner(String channelName, String userLoginId) {
        commonService.validateData(userLoginId);

        if (channelName == null || channelName.isBlank()) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        boolean isOwner =
                channelRepository
                        .existsByNameAndUsers_LoginId(
                                channelName,
                                userLoginId
                        );

        if (!isOwner) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Override
    public void validateVideoOwner(VideoEntity video, String userLoginId) {
        commonService.validateData(userLoginId);

        if (video == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        ChannelEntity channel = video.getChannelEntity();

        if (channel == null || channel.getId() == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        boolean isOwner =
                channelRepository
                        .existsByIdAndUsers_LoginId(
                                channel.getId(),
                                userLoginId
                        );

        if (!isOwner) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

    }

    @Override
    public void validatePlaybackPermission(VideoEntity video, String userLoginId) {
        if (video == null || video.getVisibility() == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }

        // 공개 영상은 누구나 재생 가능
        if (video.getVisibility() == Visibility.PUBLIC) {
            return;
        }

        // 비공개 영상은 로그인한 채널 소유자만 재생 가능
        if (video.getVisibility() == Visibility.PRIVATE) {
            validateVideoOwner(video, userLoginId);
            return;
        }

        // 정의되지 않은 공개 범위는 기본적으로 접근 거부
        throw new CustomException(ErrorCode.BAD_REQUEST);

    }
}
