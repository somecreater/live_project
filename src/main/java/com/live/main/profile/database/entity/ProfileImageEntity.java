package com.live.main.profile.database.entity;

import com.live.main.common.database.entity.timeEntity;
import com.live.main.user.database.entity.UsersEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profile_image")
@Getter
@Setter
public class ProfileImageEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="image_name", nullable = false)
  private String imageName;

  @Column(name="image_url" ,nullable = false, unique = true)
  private String imageUrl;

  @Column(nullable = false)
  private Long size;

  @Column(name = "file_type")
  private String fileType;

  @Column(name = "is_user")
  private boolean isUser;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(nullable = true, name = "user_id")
  private UsersEntity users;

}
