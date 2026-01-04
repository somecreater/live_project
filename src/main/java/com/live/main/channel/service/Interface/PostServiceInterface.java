package com.live.main.channel.service.Interface;

import com.live.main.channel.database.dto.PostDto;
import org.springframework.data.domain.Page;

/**채널 게시글 기능(2025-12-15)*/
public interface PostServiceInterface {

  /**게시글 업로드*/
  public PostDto writePost(PostDto postDto);
  /**게시글 조회*/
  public PostDto readPost(Long post_id);
  /**게시글 조회(스트리머)*/
  public PostDto readPostByOwner(Long post_id);
  /**게시글 목록 조회*/
  public Page<PostDto> readPostPage(
    int page, int size, String type, String keyword, String channel_name
  );
  /**게시글 목록 조회(스트리머)*/
  public Page<PostDto> readPostPageByOwner(
    int page, int size, String type, String keyword, String channel_name
  );
  /**게시글 수정*/
  public PostDto updatePost(PostDto postDto);
  /**게시글 삭제*/
  public boolean deletePost(Long post_id);
  /**게시글 삭제(채널 삭제)*/
  public boolean deletePostOnChannel(String channel_name);
}
