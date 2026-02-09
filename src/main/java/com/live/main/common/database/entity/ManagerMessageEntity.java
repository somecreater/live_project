package com.live.main.common.database.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "manager_message")
@Getter
@Setter
public class ManagerMessageEntity extends timeEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String title;

  @Column(nullable = false, length = 500)
  private String content;

  @Column(nullable = false, length = 100)
  private String publisher;

  @Column(nullable = false, length = 100)
  private String targetId;

  @Column(name = "isRead")
  private boolean read;

}
