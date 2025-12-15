package com.live.main.channel.service;

import com.live.main.channel.database.dto.PostDto;
import com.live.main.channel.database.mapper.PostMapper;
import com.live.main.channel.database.repository.PostRepository;
import com.live.main.channel.service.Interface.PostServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService implements PostServiceInterface {

  private final PostRepository postRepository;
  private final PostMapper postMapper;

  @Override
  public PostDto writePost(PostDto postDto, String user_login_id) {
    return null;
  }

  @Override
  public PostDto readPost(Long post_id) {
    return null;
  }

  @Override
  public Page<PostDto> readPostPage(int page, int size, String type, String keyword, String channel_name) {
    return null;
  }

  @Override
  public PostDto updatePost(PostDto postDto, String user_login_id) {
    return null;
  }

  @Override
  public boolean deletePost(Long post_id, String user_login_id) {
    return false;
  }

  @Override
  public boolean deletePostOnChannel(Long channel_id) {
    return false;
  }
}
