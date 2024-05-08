package ru.boris.examle.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import ru.boris.examle.service.LinesWriter;

import java.util.Map;
import java.util.Properties;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final String demoTopic;

    public BatchConfig(@Value("${application.kafka.topic}") String demoTopic) {
        this.demoTopic = demoTopic;
    }

    @Bean
    public Job job(Step firstStep, JobRepository jobRepository) {
        return new JobBuilder("mainJob")
                .start(firstStep)
                .repository(jobRepository)
                .build();
    }

    @Bean("firstStep")
    public Step firstStep(KafkaItemReader<String, String> kafkaItemReader,
                          LinesWriter linesWriter,
                          JobRepository jobRepository,
                          PlatformTransactionManager transactionManager) {
        return new StepBuilder("firstStep")
                .<String, String>chunk(10)
                .reader(kafkaItemReader)
                .writer(linesWriter)
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public KafkaItemReader<String, String> kafkaItemReader(KafkaProperties kafkaProperties) {
        Map<String, Object> consumerProperties = kafkaProperties.buildConsumerProperties();

        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 3);
        consumerProperties.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 3_000);

        Properties properties = new Properties();
        properties.putAll(consumerProperties);

        return new KafkaItemReaderBuilder<String, String>()
                .partitions(0)
                .name("kafkaItemReader")
                .consumerProperties(properties)
                .saveState(true)
                .topic(demoTopic)
                .build();
    }
}
