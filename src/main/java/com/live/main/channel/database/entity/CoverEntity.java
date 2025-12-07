package com.live.main.channel.database.entity;

import com.live.main.common.database.entity.timeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "cover")
@Getter
@Setter
public class CoverEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private String image_name;

  @Column
  private String image_url;

  @Column
  private Long size;

  @Column
  private String file_type;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = true, name = "channel_id")
  private ChannelEntity channel;
}
