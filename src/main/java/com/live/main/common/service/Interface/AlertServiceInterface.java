package com.live.main.common.service.Interface;

import com.live.main.common.database.dto.AlertEvent;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

/**알림 전송 서비스 (2025-12-27)*/
public interface AlertServiceInterface {
  /**알림 객체 생산 후 Kafka에 넣는 기능*/
  public void producerKafka(AlertEvent alertEvent);

  /*
  아래 메소드들은 나중에 다른 서버로 분리
  */
  /**Kafka Consumer의 알림 메시지 소비 기능*/
  public void consumerKafka(AlertEvent alertEvent, Acknowledgment ack);
  /**알림 전송 기능*/
  public void sendAlert(AlertEvent alertEvent);
  /**Redis 내 저장된 알림 전송 기능*/
  public List<AlertEvent> sendAlertList(String target);
  /**알림 전송 대상 파악 기능*/
  public List<String> searchSendAlert(String type, String publisher);
  /**접속 여부 검증 기능*/
  public boolean isOnline(String userLoginId);
}
