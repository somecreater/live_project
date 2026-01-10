package com.live.main.common.database.entity;

import com.live.main.common.database.dto.AlertType;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "alerts")
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AlertEventEntity extends timeEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  private AlertType alertType;

  @Column(nullable = false, length = 100)
  private String publisher;

  @Column(nullable = false, length = 500)
  private String content;

  @Column(nullable = false, length = 100)
  private String targetUser;

  @Column(name="isRead", nullable = false)
  private boolean read;
}
