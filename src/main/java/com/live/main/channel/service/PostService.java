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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
  public PostDto readPostByOwner(Long post_id){
    PostEntity entity= postRepository.findById(post_id).orElse(null);
    if(entity == null){
      return null;
    }
    return postMapper.toDto(entity);
  }

  @Override
  public Page<PostDto> readPostPage(
    int page, int size, String type, String keyword, String channel_name
  ) {
    Pageable pageable = PageRequest.of(page, size);
    Page<PostEntity> postEntities = null;
    if(type.isBlank()){
      postEntities= postRepository.findByVisibilityTrueAndChannelEntity_Name(channel_name,pageable);
    }else if(type.equals("title")){
      postEntities= postRepository.findByVisibilityTrueAndChannelEntity_NameAndTitleLike(
        channel_name, keyword, pageable
      );
    }else if(type.equals("content")){
      postEntities= postRepository.findByVisibilityTrueAndChannelEntity_NameAndContentLike(
        channel_name, keyword, pageable
      );
    }else if(type.equals("category")){
      postEntities= postRepository.findByVisibilityTrueAndChannelEntity_NameAndCategoryLike(
        channel_name, keyword, pageable
      );
    }
    if(postEntities == null){
      return null;
    }
    return postEntities.map(postMapper::toDto);
  }

  @Override
  public Page<PostDto> readPostPageByOwner(
    int page, int size, String type, String keyword, String channel_name
  ){
    Pageable pageable = PageRequest.of(page, size);
    Page<PostEntity> postEntities = null;
    if(type.isBlank()){
      postEntities= postRepository.findByChannelEntity_Name(channel_name,pageable);
    }else if(type.equals("title")){
      postEntities= postRepository.findByChannelEntity_NameAndTitleLike(
        channel_name, keyword, pageable
      );
    }else if(type.equals("content")){
      postEntities= postRepository.findByChannelEntity_NameAndContentLike(
        channel_name, keyword, pageable
      );
    }else if(type.equals("category")){
      postEntities= postRepository.findByChannelEntity_NameAndCategoryLike(
        channel_name, keyword, pageable
      );
    }

    if(postEntities == null){
      return null;
    }
    return postEntities.map(postMapper::toDto);
  }

  @Override
  public PostDto updatePost(PostDto postDto) {
    if(postDto == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    PostEntity postEntity= postRepository.findById(postDto.getId()).orElse(null);
    if(postEntity == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    if(postDto.getTitle().isBlank()
      || postDto.getContent().isBlank()
      || postDto.getCategory().isBlank()
      || postDto.getChannel_name().isBlank()){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    postEntity.setTitle(postDto.getTitle());
    postEntity.setContent(postDto.getContent());
    postEntity.setCategory(postDto.getCategory());
    PostEntity update= postRepository.save(postEntity);

    return postMapper.toDto(update);
  }

  @Override
  public boolean deletePost(Long post_id) {
    try{
      PostEntity postEntity= postRepository.findById(post_id).orElse(null);
      if(postEntity == null){
        throw new CustomException(ErrorCode.NOT_FOUND);
      }
      postRepository.deleteById(post_id);
      return true;
    }catch(Exception e){
      e.printStackTrace();
      return false;
    }
  }

  @Override
  public boolean deletePostOnChannel(Long channel_id) {
    try {
      postRepository.deleteByChannelEntity_Id(channel_id);
      return true;
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
  }
}
