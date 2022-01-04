package com.subabk.integration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.integration.launch.JobLaunchingMessageHandler;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.transaction.PlatformTransactionManager;

import com.subabk.listener.JobListener;
import com.subabk.listener.StepListener;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class IntegrationBatchConfiguration {

	public static final String JOB_NAME = "IntegrationJob";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	public JobLauncher jobLauncher;
	
	@Bean
	@ServiceActivator(inputChannel = "requests", outputChannel = "replies")
	public JobLaunchingMessageHandler jobLaunchingMessageHandler() {
		return new JobLaunchingMessageHandler(this.jobLauncher);
	}

	@Bean
	public DirectChannel replies() {
		return new DirectChannel();
	}
	
	@Bean
	public DirectChannel requests() {
		return new DirectChannel();
	}
	
	@Bean
	public Job intergationJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		
		return this.jobBuilderFactory.get(JOB_NAME)
				.start(childStep())
				.build();
	}

	@Bean
	public Step childStep() {
		return stepBuilderFactory.get("ChildStep")
				.tasklet((contribution,chunkContext) -> {
					log.info("Inside Child Step.");
					return RepeatStatus.FINISHED;
				})
				.listener(childStepExecutionListener())
				.build();
	}

	@Bean
	public StepExecutionListener childStepExecutionListener() {
		return new StepListener();
	}
	
	@Bean
	public JobExecutionListener childJobExecutionListener() {
		return new JobListener();
	}

}