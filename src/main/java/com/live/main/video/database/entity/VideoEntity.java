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
  private String file_type;

  @Column
  private Long size;

  @Column
  private boolean allow_comments;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Visibility visibility = Visibility.PRIVATE;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ProcessingStatus processingStatus =
          ProcessingStatus.CREATED;
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

  @Column
  private String thumbnail_url;

  @Column(length = 1024, unique = true)
  private String originalObjectKey;

  @Column(length = 2048)
  private String hlsObjectKey;

  @Column(nullable = false)
  private Long expectedFileSize;

  @ManyToOne
  @JoinColumn(nullable = true, name = "channel_id")
  private ChannelEntity channelEntity;

}
