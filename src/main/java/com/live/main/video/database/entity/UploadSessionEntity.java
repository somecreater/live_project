package com.live.main.video.database.entity;

import com.live.main.common.database.entity.timeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "upload_session")
@Getter
@Setter
public class UploadSessionEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "upload_id", length = 512)
  private String uploadId;

  @Column(name = "video_id")
  private Long videoId;

  @Column(name = "object_key", length = 500)
  private String objectKey;

  @Enumerated(EnumType.STRING)
  @Column
  private UploadSessionStatus status;

  @Column(name = "total_part_count")
  private Integer totalPartCount;

  @Column(name = "completed_part_count")
  private Integer completedPartCount;

  @Column(name = "part_size")
  private Long partSize;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "aborted_at")
  private LocalDateTime abortedAt;
}
