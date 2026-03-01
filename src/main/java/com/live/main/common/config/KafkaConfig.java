package com.live.main.common.config;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.dto.ManagerMessageEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@EnableKafka
@Configuration
public class KafkaConfig {
    @Bean
    public ProducerFactory<String, AlertEvent> producerFactory(
            KafkaProperties properties
    ) {
        return new DefaultKafkaProducerFactory<>(
                properties.buildProducerProperties()
        );
    }

    @Bean
    public KafkaTemplate<String, AlertEvent> kafkaTemplate(
            ProducerFactory<String, AlertEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

    @Bean
    public ProducerFactory<String, ManagerMessageEvent> managerMessageProducerFactory(
            KafkaProperties properties
    ) {
        return new DefaultKafkaProducerFactory<>(
                properties.buildProducerProperties()
        );
    }

    @Bean
    public KafkaTemplate<String, ManagerMessageEvent> managerMessageKafkaTemplate(
            ProducerFactory<String, ManagerMessageEvent> producerFactory
    ) {
        return new KafkaTemplate<>(producerFactory);
    }

}
