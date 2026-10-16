package com.live.main.video.service;

import com.live.main.common.database.dto.VideoEncodingEvent;
import com.live.main.common.database.dto.VideoValidationEvent;
import com.live.main.video.service.Interface.VideoMediaServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaAdapter {


    private final KafkaTemplate<String, VideoValidationEvent> kafkaTemplate;
    private final VideoMediaServiceInterface videoMediaService;


    @Value("${app.kafka.topic.video-complete.name}")
    private String validationCompletedTopic;

    @Async("IOTaskExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void publishValidationCompleted(VideoValidationEvent event){
        kafkaTemplate.send(
                validationCompletedTopic,
                event.getObject_key(),
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error(
                        "Kafka 검증 이벤트 발행 실패. videoId={}, key={}",
                        event.getVideo_id(),
                        event.getObject_key(),
                        exception
                );
                return;
            }

            log.info(
                    "Kafka 검증 이벤트 발행 성공. videoId={}, partition={}, offset={}",
                    event.getVideo_id(),
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset()
            );
        });
    }

    @KafkaListener(
            topics = "video-encoding-topic",
            groupId = "video-encoding_group",
            containerFactory = "videoEncodingKafkaListenerContainerFactory"
    )
    /**
     * 트랜스코딩 완료 이벤트를 수신해 영상 상태 반영을 위임하고, 정상 처리 후 Kafka ACK를 수행한다.
     */
    public void consumeEncodingCompleted(
            VideoEncodingEvent event,
            Acknowledgment acknowledgment
    ) {
        //videoMediaService.completeEncoding(event);
        acknowledgment.acknowledge();
    }
}
