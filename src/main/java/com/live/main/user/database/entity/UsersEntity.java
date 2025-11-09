package com.live.main.user.database.entity;

import com.live.main.common.database.entity.timeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class UsersEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "login_id", unique = true, nullable = false, updatable = false)
  private String loginId;

  @Column
  private String password;

  @Column(unique = true)
  private String phone;

  @Column
  private String nickname;

  @Enumerated(EnumType.STRING)
  @Column(name = "login_type", nullable = false)
  private LoginType loginType;

  @Enumerated(EnumType.STRING)
  @Column(name = "user_type", nullable = false)
  private UserType userType;

}
