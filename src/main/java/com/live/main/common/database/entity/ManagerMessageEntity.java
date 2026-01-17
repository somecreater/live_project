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

  @Column
  private String title;

  @Column
  private String content;

  @Column
  private String publisher;

  @Column
  private String targetId;

  @Column
  private boolean read;

}
