package com.live.main.channel.database.mapper;

import com.live.main.channel.database.dto.PostDto;
import com.live.main.channel.database.entity.PostEntity;
import com.live.main.channel.database.repository.ChannelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostMapper {

  private final ChannelRepository channelRepository;

  public PostEntity toEntity(PostDto postDto){
    PostEntity postEntity= new PostEntity();
    postEntity.setId(postDto.getId());
    postEntity.setTitle(postDto.getTitle());
    postEntity.setContent(postDto.getContent());
    postEntity.setCategory(postDto.getCategory());
    postEntity.setLike(postDto.getLike());
    postEntity.setUnlike(postDto.getUnlike());
    postEntity.setVisibility(postDto.isVisibility());
    postEntity.setCommentable(postDto.isCommentable());
    if(postDto.getChannel_id() != null){
      postEntity.setChannelEntity(
        channelRepository.findById(postDto.getChannel_id()).orElse(null));
    }
    if(postDto.getChannel_name() != null){
      postEntity.setChannelEntity(
        channelRepository.findByName(postDto.getChannel_name()).orElse(null));
    }
    postEntity.setCreatedAt(postDto.getCreatedAt());
    postEntity.setUpdatedAt(postDto.getUpdatedAt());
    return postEntity;
  }

  public PostDto toDto(PostEntity postEntity){
    PostDto postDto= new PostDto();
    postDto.setId(postEntity.getId());
    postDto.setTitle(postEntity.getTitle());
    postDto.setContent(postEntity.getContent());
    postDto.setCategory(postEntity.getCategory());
    postDto.setLike(postEntity.getLike());
    postDto.setUnlike(postEntity.getUnlike());
    postDto.setVisibility(postEntity.isVisibility());
    postDto.setCommentable(postEntity.isCommentable());
    if(postEntity.getChannelEntity() != null){
      postDto.setChannel_id(postEntity.getChannelEntity().getId());
      postDto.setChannel_name(postEntity.getChannelEntity().getName());
    }
    postDto.setCreatedAt(postEntity.getCreatedAt());
    postDto.setUpdatedAt(postEntity.getUpdatedAt());
    return postDto;
  }
}
