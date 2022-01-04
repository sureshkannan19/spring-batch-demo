package com.subabk.jobdecider;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.subabk.listener.JobListener;
import com.subabk.listener.StepListener;
import com.subabk.listener.ChunkListener;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class JobDeciderBatchConfiguration {

	public static final String JOB_NAME = "DeciderJob";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	private int count = 0;
	
	@Bean
	public Job deciderJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		
		return this.jobBuilderFactory.get(JOB_NAME)
				.start(deciderStep())
				.next(decider())
				.from(decider()).on("Odd").to(childOddStep())
				.from(childOddStep()).on("*").to(decider())
				.from(decider()).on("Even").to(childEvenStep())
				.end()
				.build();
	}

	@Bean
	public Step deciderStep() {
		return this.stepBuilderFactory.get("deciderStep")
				.tasklet((contribution,chunkContext) -> {
					System.out.println("Inside Decider Step.");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
	
	@Bean
	public Step childOddStep() {
		return stepBuilderFactory.get("ChildOddStep")
				.tasklet((contribution,chunkContext) -> {
					log.info("Inside Odd Step.");
					return RepeatStatus.FINISHED;
				})
				.listener(childStepExecutionListener())
				.build();
	}

	@Bean
	public Step childEvenStep() {
		return stepBuilderFactory.get("ChildEvenStep")
				.listener(new ChunkListener())
				.tasklet((contribution,chunkContext) -> {
					log.info("Inside Even Step.");
					return RepeatStatus.FINISHED;
				})
				.listener(childStepExecutionListener())
				.build();
	}
	
	@Bean
	public JobExecutionDecider decider() {
		return ((JobExecution jobexecution, StepExecution stepExecution) -> {
			count++;
			return count % 2 == 0 ? new FlowExecutionStatus("Even") : new FlowExecutionStatus("Odd");
		});
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