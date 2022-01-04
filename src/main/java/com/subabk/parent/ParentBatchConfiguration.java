package com.subabk.parent;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.JobStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import com.subabk.readers.ReaderBatchConfiguration;

@Configuration
public class ParentBatchConfiguration {

	public static final String JOB_NAME = "ParentJob";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private Job job;
	
	@Autowired
	private JobLauncher jobLauncher;

	@Bean
	public Job parentJob(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
		
		Step childJobStep= new JobStepBuilder(new StepBuilder(ReaderBatchConfiguration.JOB_NAME))
				.job(job)
				.launcher(jobLauncher)
				.repository(jobRepository)
				.transactionManager(transactionManager)
				.build();
		
		return this.jobBuilderFactory.get(JOB_NAME)
				.start(parentStep1())
				.on(ExitStatus.FAILED.getExitCode()).fail()
				.from(parentStep1()).on("*").to(childJobStep)
				.from(childJobStep).on("*").end()
				.end()
				.build();
	}

	@Bean
	public Step parentStep1() {
		return this.stepBuilderFactory.get("ParentStep1")
				.tasklet((contribution,chunkContext) -> {
					System.out.println("Inside Parent Step1.");
					return RepeatStatus.FINISHED;
				})
				.build();
	}
}