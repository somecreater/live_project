package com.live.main.common.config;

import com.live.main.common.database.dto.AlertEvent;
import com.live.main.common.database.dto.ManagerMessageEvent;
import com.live.main.common.database.dto.VideoValidationEvent;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, Object> props = new HashMap<>(properties.buildConsumerProperties());
        props.remove("spring.json.trusted.packages");
        props.remove("spring.json.use.type.headers");
        props.remove("spring.json.value.default.type");
        props.remove("spring.json.key.default.type");

        JsonDeserializer<AlertEvent> valueDeserializer =
                new JsonDeserializer<>(AlertEvent.class);

        valueDeserializer.setUseTypeHeaders(false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
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
        Map<String, Object> props = new HashMap<>(properties.buildConsumerProperties());
        props.remove("spring.json.trusted.packages");
        props.remove("spring.json.use.type.headers");
        props.remove("spring.json.value.default.type");
        props.remove("spring.json.key.default.type");

        JsonDeserializer<ManagerMessageEvent> valueDeserializer =
                new JsonDeserializer<>(ManagerMessageEvent.class);

        valueDeserializer.setUseTypeHeaders(false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
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

    @Bean
    public ConsumerFactory<String, VideoValidationEvent> videoValidationConsumerFactory(
            KafkaProperties properties
    ) {
        Map<String, Object> props = new HashMap<>(properties.buildConsumerProperties());
        props.remove("spring.json.trusted.packages");
        props.remove("spring.json.use.type.headers");
        props.remove("spring.json.value.default.type");
        props.remove("spring.json.key.default.type");

        JsonDeserializer<VideoValidationEvent> valueDeserializer =
                new JsonDeserializer<>(VideoValidationEvent.class);

        valueDeserializer.setUseTypeHeaders(false);
        valueDeserializer.addTrustedPackages("*");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VideoValidationEvent>
    videoValidationKafkaListenerContainerFactory(
            ConsumerFactory<String, VideoValidationEvent> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, VideoValidationEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(CONSUMER_PARTITIONS); // 파티션 수 이하
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);

        return factory;
    }
}
