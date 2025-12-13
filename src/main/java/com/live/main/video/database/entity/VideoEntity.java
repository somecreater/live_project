package com.live.main.video.database.entity;

import com.live.main.channel.database.entity.ChannelEntity;
import com.live.main.common.database.entity.timeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "video")
@Getter
@Setter
public class VideoEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String title;

  @Column
  private String description;

  @Column
  private boolean visibility;

  @Column
  private boolean allow_comments;

  @Column
  @Enumerated(EnumType.STRING)
  private Status status;

  @Column
  private int duration_seconds;

  @Column(name = "like_count")
  private int like;

  @Column(name = "unlike_count")
  private int unlike;

  @Column
  private int view_count;

  @ManyToOne
  @JoinColumn(nullable = true, name = "channel_id")
  private ChannelEntity channelEntity;

}
