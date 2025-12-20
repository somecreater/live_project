package com.live.main.channel.database.entity;

import com.live.main.common.database.entity.timeEntity;
import com.live.main.user.database.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "channel")
@Getter
@Setter
public class ChannelEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true)
  private String name;

  @Column
  private String description;

  @Column
  private Long subscription_count;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = true, name = "user_id")
  private UsersEntity users;
}
