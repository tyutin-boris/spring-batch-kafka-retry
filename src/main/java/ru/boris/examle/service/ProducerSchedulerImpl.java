package ru.boris.examle.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProducerSchedulerImpl implements ProducerScheduler {

    @Value("${application.kafka.topic}")
    private String topicName;

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Override
    @Scheduled(cron = "*/5 * * * * *")
    public void send() {
        log.info("Scheduler start.");

        String helloKafka = "Hello kafka";
        String howAreYou = "How are you ?";
        String whatAreYouDoing = "What are you doing ?";
        String bye = "Bye";

        Stream.of(helloKafka, howAreYou, whatAreYouDoing, bye)
                .forEach(message -> kafkaTemplate.send(topicName, message)
//                        .addCallback(
//                                result -> log.info("Scheduler send message: " + result),
//                                ex -> log.error("kafka return exception.", ex)
//                        )
                );
    }
}
