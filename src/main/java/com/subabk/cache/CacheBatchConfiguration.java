package com.subabk.cache;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.subabk.bo.Citizen;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class CacheBatchConfiguration {

	public static final String JOB_NAME = "CacheJob";
	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job cacheJob() {
		return this.jobBuilderFactory.get(JOB_NAME)
				.start(findCitizenDetails(null))
				.on("*").to(dislayCitizens())
				.on("*").to(clearCache())
				.end()
				.build();
	}
	
	@Bean
	public Step findCitizenDetails(FindCitizensTasklet tasklet) {
		return this.stepBuilderFactory.get("FindCitizens")
				.tasklet(tasklet)
				.build();
	}
	
	@Bean
	public Step dislayCitizens() {
		return this.stepBuilderFactory.get("DisplayCitizens")
				.tasklet(displacyCitizensTasklet(null))
				.build();
	}

	@Bean
	@StepScope
	public Tasklet displacyCitizensTasklet(@Value("#{jobParameters['stateId']}")Long stateId) {
		return new Tasklet() {
			
			@Autowired
			private CacheManager cacheManager;
			
			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				List<Citizen> citizens= (List<Citizen>) cacheManager.getCache("CitizenDetails").get(stateId).get();
				for (Citizen citizen : citizens) {
					log.info("{}", citizen);
				}
				return RepeatStatus.FINISHED;
			}
		};
	}
	
	@Bean
	public Step clearCache() {
		return  this.stepBuilderFactory.get("ClearCache")
				.tasklet(clearCacheTasklet())
				.build();
	}
	
	@Bean
	public CacheManager cacheManager() {
		ConcurrentMapCacheManager cacheManger= new ConcurrentMapCacheManager();
		cacheManger.setCacheNames(Arrays.asList("CitizenDetails"));
		return cacheManger;
	}
	
	@Bean
	public Tasklet clearCacheTasklet() {
		return new Tasklet() {

			@Autowired
			private CacheManager cacheManager;

			@Override
			public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
				cacheManager.getCache("CitizenDetails").clear();
				log.info("After Clearing Cache {}", cacheManager.getCache("CitizenDetails"));
				return RepeatStatus.FINISHED;
			}
		};
	}

}
