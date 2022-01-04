package com.subabk.restart;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.subabk.config.BatchConfiguration;
import com.subabk.listener.StepListener;

import lombok.extern.slf4j.Slf4j;


/**
 *  MapJobRepositoryFactoryBean will clear the data from JobRepository for every run,
 *  So inorder to run this StatefulBatchApplication job, {@link EnableBatchProcessing} has to be annotated in this file 
 *  and {@link BatchConfiguration} should be commented.
 * @author Suresh Babu
 */
@Configuration
@Slf4j
public class StatefulBatchConfiguration {

	public static final String JOB_NAME = "StatefulJob";
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("testDataSource")
	private DataSource dataSource;
	
	@Bean
	public Job statefulJob() {
		
		return this.jobBuilderFactory.get(JOB_NAME)
				.start(statefulReadStep())
				.build();
				
	}
	
	@Bean
	public Step statefulReadStep() {
		return this.stepBuilderFactory.get("statefulStep")
				.<Long, Long>chunk(10)
				.reader(statefulItemReader())
				.writer(items-> {
					for (Long item : items) {
						log.info("Item Id :{} ",item);		
					}
				})
				.stream(statefulItemReader())
				.listener(stepExecutionListener())
				.build();
	}
	
	@Bean
	@StepScope
	public StatefulItemReader statefulItemReader() {
		return new StatefulItemReader(getDummyValues());
	}
	
	@Bean
	public StepExecutionListener stepExecutionListener() {
		return new StepListener();
	}

	private List<Long> getDummyValues() {
		return LongStream.rangeClosed(1, 30).boxed().collect(Collectors.toList());
	}
}
