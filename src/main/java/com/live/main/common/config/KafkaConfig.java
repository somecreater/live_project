package com.live.main.common.config;

import com.live.main.common.database.dto.AlertEvent;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

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
}
