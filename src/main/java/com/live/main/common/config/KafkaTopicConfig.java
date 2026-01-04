package com.live.main.common.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
@EnableKafka
public class KafkaTopicConfig {

    @Value("${app.kafka.topic.notification.name}")
    private String NOTIFICATION_TOPIC_NAME;
    @Value("${app.kafka.topic.notification.partitions}")
    private Integer NOTIFICATIOPN_PARTITIONS;
    @Value("${app.kafka.topic.notification.replicas}")
    private Integer NOTIFICATION_REPLICAS;

    @Bean
    public NewTopic notificationTopic(){
      return TopicBuilder.name(NOTIFICATION_TOPIC_NAME)
              .partitions(NOTIFICATIOPN_PARTITIONS)
              .replicas(NOTIFICATION_REPLICAS)
              .build();
    }
}
