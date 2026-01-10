package com.live.main.common.service;

import com.live.main.channel.service.Interface.ChannelServiceInterface;
import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.repository.OnlineRepository;
import com.live.main.common.service.Interface.AlertCustomServiceInterface;
import com.live.main.common.service.Interface.AlertServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AlertService implements AlertServiceInterface {

  private final SimpMessagingTemplate messagingTemplate;
  private final ChannelServiceInterface channelService;

  private final OnlineRepository onlineRepository;
  private final AlertCustomServiceInterface alertCustomService;

  private final KafkaTemplate<String, AlertEvent> kafkaTemplate;

  @Value("${app.kafka.topic.notification.name}")
  private String NOTIFICATION_TOPIC_NAME;

  @Async("IOTaskExecutor")
  @EventListener
  @Override
  public void producerKafka(AlertEvent alertEvent){
    kafkaTemplate.send(
      NOTIFICATION_TOPIC_NAME,
      alertEvent.getPublisher(),
      alertEvent
    );
    log.info("Kafka Alert Produced to Kafka - type: {},  subType: {}, publisher: {}, content: {}",
      alertEvent.getType().getType(),
      alertEvent.getType().getSubtype(),
      alertEvent.getPublisher(),
      alertEvent.getContent());
  }



  @KafkaListener(
          topics = "notification-topic",
          groupId = "notification_group",
          containerFactory = "kafkaListenerContainerFactory"
  )
  @Override
  public void consumerKafka(AlertEvent alertEvent, Acknowledgment ack){
    try {
      log.info("Kafka Alert Received by Kafka - type: {},  subType: {}, publisher: {}, content: {}",
              alertEvent.getType().getType(),
              alertEvent.getType().getSubtype(),
              alertEvent.getPublisher(),
              alertEvent.getContent());

      sendAlert(alertEvent);
      ack.acknowledge();
    } catch (Exception e) {
      log.error("Alert consume failed", e);
    }
  }

  @Override
  public void sendAlert(AlertEvent alertEvent) {

    List<String> targetList= searchSendAlert(alertEvent.getType().getType(),alertEvent.getPublisher());
    for(String target: targetList){
      Long id=alertCustomService.save(target, alertEvent);
      if (isOnline(target)) {
        log.info("WebSocket Alert Send [Online] - target: {}, type: {},  subType: {}, publisher: {}, content: {}",
                target,
                alertEvent.getType().getType(),
                alertEvent.getType().getSubtype(),
                alertEvent.getPublisher(),
                alertEvent.getContent());
        log.info("[SEND] URL: /user/{}/queue/alerts", target );


        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create();
        headerAccessor.setContentType(MimeTypeUtils.APPLICATION_JSON);
        headerAccessor.setNativeHeader("sender", alertEvent.getPublisher());
        headerAccessor.setNativeHeader("eventType",alertEvent.getType().getType());
        headerAccessor.setNativeHeader("eventSubType", alertEvent.getType().getSubtype());
        headerAccessor.setNativeHeader("priority", alertEvent.getType().getPriority());
        headerAccessor.setHeader("alertTime", alertEvent.getCreatedAt());
        headerAccessor.setHeader("alertId", id);
        MessageHeaders headers = headerAccessor.getMessageHeaders();
        messagingTemplate.convertAndSendToUser(
                target,
                "/queue/alerts",
                alertEvent.getContent(),
                headers);
      }else{
        log.info("WebSocket Alert Send [Offline] - target: {}, type: {},  subType: {}, publisher: {}, content: {}",
                target,
                alertEvent.getType().getType(),
                alertEvent.getType().getSubtype(),
                alertEvent.getPublisher(),
                alertEvent.getContent());
      }
    }
  }

  @Override
  public List<AlertEvent> sendAlertList(String target){
    List<AlertEvent> alertEvents= alertCustomService.get(target);
    if(alertEvents!=null){
      return alertEvents;
    }else{
      return null;
    }
  }

  //나중에 API 호출로 전환
  @Override
  public List<String> searchSendAlert(String type, String publisher){
    List<String> targetList=null;

    switch (type){
      case "CHANNEL":{
        targetList=channelService.getsubscriptionUserList(publisher);
        break;
      }
      case "USER":{
        targetList= new ArrayList<>();
        targetList.add(publisher);
        break;
      }
      case "REPLY":{
        //추후 로직 추가
        break;
      }
      default:{
        break;
      }
    }
    return targetList;
  }

  @Override
  public boolean isOnline(String userLoginId){
    return onlineRepository.get(userLoginId) != null;
  }
}