package com.live.main.common.config;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.dto.ManagerMessageEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${app.kafka.consumer.partitions}")
    private Integer CONSUMER_PARTITIONS;

    // Alert Consumer Configuration
    @Bean
    public ConsumerFactory<String, AlertEvent> consumerFactory(
            KafkaProperties properties
    ) {
        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties()
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AlertEvent>
    kafkaListenerContainerFactory(
            ConsumerFactory<String, AlertEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, AlertEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(CONSUMER_PARTITIONS); // 파티션 수 이하
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

    // Manager Message Consumer Configuration
    @Bean
    public ConsumerFactory<String, ManagerMessageEvent> managerMessageConsumerFactory(
            KafkaProperties properties
    ) {
        return new DefaultKafkaConsumerFactory<>(
                properties.buildConsumerProperties()
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ManagerMessageEvent>
    managerMessageKafkaListenerContainerFactory(
            ConsumerFactory<String, ManagerMessageEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, ManagerMessageEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(CONSUMER_PARTITIONS); // 파티션 수 이하
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }

}
