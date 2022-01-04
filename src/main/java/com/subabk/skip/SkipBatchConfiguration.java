package com.subabk.skip;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.subabk.bo.Citizen;
import com.subabk.listener.StepListener;
import com.subabk.readers.mapper.CitizenRowMapper;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class SkipBatchConfiguration {

	public static final String JOB_NAME = "SkipJob";
	
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
				.start(skipReadStep())
				.build();
				
	}
	
	@Bean
	public Step skipReadStep() {
		return this.stepBuilderFactory.get("SkipStep")
				.<Citizen, Citizen>chunk(10)
				.reader(jdbcPageItemReader())
				.processor(new SkipProcessor())
				.writer(items-> {
					for (Citizen item : items) {
						log.info("Citizen Id :{} ", item.getName());		
					}
				})
				.faultTolerant()
				.skip(RuntimeException.class)
				.skipLimit(1)
				.listener(stepExecutionListener())
				.build();
	}
	
	/**
	 * JdbcPagingItemReader is thread Safe. 
	 */
	@Bean 
	public JdbcPagingItemReader<Citizen> jdbcPageItemReader() {
		JdbcPagingItemReader<Citizen> jdbcPagingItemReader= new JdbcPagingItemReader<>();
		jdbcPagingItemReader.setFetchSize(2);
		jdbcPagingItemReader.setDataSource(this.dataSource);
		jdbcPagingItemReader.setRowMapper(new CitizenRowMapper());
		
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("name, age, gender, aadharNumber, address");
		queryProvider.setFromClause("from citizen");
		
        // SortKeys Must be specified, inOrder to pick next set of records for subsequent iterations.
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("aadharNumber", Order.ASCENDING);
		queryProvider.setSortKeys(sortKeys);

		jdbcPagingItemReader.setQueryProvider(queryProvider);
		return jdbcPagingItemReader;
	}
	
	@Bean
	public StepExecutionListener stepExecutionListener() {
		return new StepListener();
	}

}
