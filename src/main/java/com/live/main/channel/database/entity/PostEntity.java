package com.live.main.channel.database.entity;

import com.live.main.common.database.entity.timeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "post")
@Getter
@Setter
public class PostEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String title;

  @Column(length = 65535)
  private String content;

  @Column
  private String category;

  @Column(name = "like_count")
  private Long like;

  @Column(name = "unlike_count")
  private Long unlike;

  @Column
  private boolean visibility;

  @Column
  private boolean commentable;

  @ManyToOne
  @JoinColumn(nullable = true, name = "channel_id")
  private ChannelEntity channelEntity;
}
