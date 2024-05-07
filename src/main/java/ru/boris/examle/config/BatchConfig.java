package ru.boris.examle.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public Job job(Step firstStep, JobRepository jobRepository) {
        return new JobBuilder("mainJob")
                .start(firstStep)
                .repository(jobRepository)
                .build();
    }

    @Bean("firstStep")
    public Step firstStep(Tasklet tasklet,
                          JobRepository jobRepository,
                          PlatformTransactionManager transactionManager) {
        return new StepBuilder("firstStep")
                .tasklet(tasklet)
                .repository(jobRepository)
                .transactionManager(transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
