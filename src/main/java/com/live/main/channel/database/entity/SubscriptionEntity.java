package com.live.main.channel.database.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
  name = "subscription",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "channel_id"})
  },
  indexes = {
    @Index(name = "idx_user_channel", columnList = "user_login_id, channel_name"),
    @Index(name = "idx_channel", columnList = "channel_name")
  }
)
@Getter
@Setter
public class SubscriptionEntity{

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String user_login_id;

  @Column
  private String channel_name;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column
  private boolean notification;
}
