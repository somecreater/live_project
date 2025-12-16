package com.live.main.channel.service;

import com.live.main.channel.database.dto.PostDto;
import com.live.main.channel.database.entity.PostEntity;
import com.live.main.channel.database.mapper.PostMapper;
import com.live.main.channel.database.repository.PostRepository;
import com.live.main.channel.service.Interface.PostServiceInterface;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService implements PostServiceInterface {

  private final PostRepository postRepository;
  private final PostMapper postMapper;

  @Override
  public PostDto writePost(PostDto postDto) {
    if(postDto == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }
    if(postDto.getTitle().isBlank()
    || postDto.getContent().isBlank()
    || postDto.getCategory().isBlank()
    || postDto.getChannel_name().isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    PostEntity postEntity= postMapper.toEntity(postDto);
    postEntity.setLike(0L);
    postEntity.setUnlike(0L);
    postEntity.setUpdatedAt(LocalDateTime.now());
    postEntity.setCreatedAt(LocalDateTime.now());

    PostEntity newPost= postRepository.save(postEntity);

    if(newPost.getId() == null){
      return null;
    }
    return postMapper.toDto(newPost);
  }

  @Override
  public PostDto readPost(Long post_id) {
    PostEntity entity= postRepository.findById(post_id).orElse(null);
    PostDto postDto= new PostDto();
    if(entity == null){
      return null;
    }
    if(!entity.isVisibility()){
      return postDto;
    }

    postDto=postMapper.toDto(entity);
    return postDto;
  }

  @Override
  public Page<PostDto> readPostPage(int page, int size, String type, String keyword, String channel_name) {
    return null;
  }

  @Override
  public PostDto updatePost(PostDto postDto) {
    return null;
  }

  @Override
  public boolean deletePost(Long post_id) {
    return false;
  }

  @Override
  public boolean deletePostOnChannel(Long channel_id) {
    return false;
  }
}
