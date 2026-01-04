package com.live.main.channel.controller;

import com.live.main.channel.database.dto.ChannelDto;
import com.live.main.channel.database.dto.PostDeleteRequest;
import com.live.main.channel.database.dto.PostDto;
import com.live.main.channel.database.dto.PostSearchRequest;
import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.channel.service.Interface.PostServiceInterface;
import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.dto.ErrorCode;
import com.live.main.common.exception.CustomException;
import com.live.main.user.database.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
  private final PostServiceInterface postService;
  private final ChannelServiceInterface channelService;

  private final ApplicationEventPublisher publisher;

  @GetMapping("/read/{post_id}")
  public ResponseEntity<?> readPost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @PathVariable Long post_id
  ){
    log.info("[GET] /api/post/read/{}", post_id);
    Map<String, Object> result= new HashMap<>();
    ChannelDto channelDto= channelService.getChannelInfoUser(principal.getUserid());
    PostDto postDto=postService.readPostByOwner(post_id);
    if(postDto == null){
      result.put("result", false);
    }
    else if(channelDto !=null && Objects.equals(postDto.getChannel_name(), channelDto.getName())){
      result.put("result", true);
      result.put("post", postDto);
    }else{
      if(!postDto.isVisibility()){
        result.put("result", false);
      }else{
        result.put("result", true);
        result.put("post", postDto);
      }
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/list")
  public ResponseEntity<?> searchPost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostSearchRequest postSearchRequest
  ){
    log.info("[POST] /api/post/list - page:{} size:{} type:{} keyword:{}",
      postSearchRequest.getPage(), postSearchRequest.getSize(),
      postSearchRequest.getType(), postSearchRequest.getKeyword());

    Map<String, Object> result= new HashMap<>();
    Page<PostDto> postDtoPage=null;
    if(postSearchRequest == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    ChannelDto channelDto= channelService.getChannelInfoUser(principal.getUserid());
    if(channelDto!=null && Objects.equals(channelDto.getName(), postSearchRequest.getChannel_name())){
      postDtoPage=postService.readPostPageByOwner(
        postSearchRequest.getPage(),
        postSearchRequest.getSize(),
        postSearchRequest.getType(),
        postSearchRequest.getKeyword(),
        postSearchRequest.getChannel_name()
      );
    }else{
      postDtoPage=postService.readPostPage(
        postSearchRequest.getPage(),
        postSearchRequest.getSize(),
        postSearchRequest.getType(),
        postSearchRequest.getKeyword(),
        postSearchRequest.getChannel_name()
      );
    }

    if(postDtoPage == null){
      result.put("result", false);
    }else{
      result.put("result", true);
      result.put("post_page", postDtoPage);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/write")
  public ResponseEntity<?> writePost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostDto postDto
  ){
    log.info("[POST] /api/post/write - {}", principal.getUserid());
    Map<String, Object> result= new HashMap<>();
    if(postDto == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    ChannelDto channelDto= channelService.getChannelInfoUser(principal.getUserid());
    if(channelDto!=null && Objects.equals(postDto.getChannel_name(), channelDto.getName())){
      PostDto newPost= postService.writePost(postDto);
      result.put("result", true);
      result.put("new_post", newPost);
      publisher.publishEvent(new AlertEvent(
        "POST_UPLOAD", channelDto.getName(),
        channelDto.getName()+" 채널이 새 게시글을 업로드 했습니다. \n제목: '" + newPost.getTitle()+"'"));
    }else{
      result.put("result", false);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/update")
  public ResponseEntity<?> updatePost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostDto postDto
  ){
    log.info("[POST] /api/post/update - {}", principal.getUserid());
    Map<String, Object> result= new HashMap<>();
    if(postDto == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    ChannelDto channelDto= channelService.getChannelInfoUser(principal.getUserid());
    if(channelDto!=null && Objects.equals(postDto.getChannel_name(), channelDto.getName())){
      PostDto updateDto= postService.updatePost(postDto);
      result.put("result", true);
      result.put("update_post", updateDto);
      publisher.publishEvent(new AlertEvent(
        "POST_UPDATE", channelDto.getName(),
        channelDto.getName()+" 채널이 게시물을 수정 했습니다. \n제목: '" + updateDto.getTitle()+"'"));
    }else{
      result.put("result", false);
    }
    return ResponseEntity.ok(result);
  }

  @PostMapping("/delete")
  public ResponseEntity<?> deletePost(
    @AuthenticationPrincipal CustomUserDetails principal,
    @RequestBody PostDeleteRequest postDeleteRequest
  ){
    log.info("[POST] /api/post/delete - {}", principal.getUserid());
    Map<String, Object> result= new HashMap<>();
    if(postDeleteRequest == null){
      throw new CustomException(ErrorCode.BAD_REQUEST);
    }

    ChannelDto channelDto= channelService.getChannelInfoUser(principal.getUserid());
    if(channelDto != null && Objects.equals(postDeleteRequest.getChannel_name(), channelDto.getName())){
      boolean delete= postService.deletePost(postDeleteRequest.getPost_id());
      if(delete){
        result.put("result", true);
        publisher.publishEvent(new AlertEvent(
          "POST_DELETE", channelDto.getName(),
          channelDto.getName()+" 채널이 게시물을 삭제 했습니다."));
      }else{
        result.put("result",false);
      }
    }else{
      result.put("result",false);
    }
    return ResponseEntity.ok(result);
  }

}
