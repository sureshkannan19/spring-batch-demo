package com.subabk.partitioner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.subabk.bo.Citizen;
import com.subabk.listener.JobListener;
import com.subabk.listener.StepListener;
import com.subabk.readers.mapper.CitizenRowMapper;
import com.subabk.util.JobExecutionContextHelper;

@Configuration
public class PartitionerBatchConfiguration {

	public static final String JOB_NAME = "PartitionerJob";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	@Qualifier("testDataSource")
	private DataSource dataSource;
	
	@Value("${app.gridsize}")
	private int gridSize;
	
	@Value("${app.partitionJob.chunkSize}")
	private int chunkSize;
	
	@Value("${app.partitionJob.fetchSize}")
	private int fetchSize;
	
	@Value("${app.maxPoolSize}")
	private int maxPoolSize;
	
	@Value("${app.corePoolSize}")
	private int corePoolSize;
	
	@Value("${app.queueCapacity}")
	private int queueCapacity;
	
	@Autowired
	private JobExecutionContextHelper helper;

	@Bean
	public Job partitionJob() {
		
		return this.jobBuilderFactory.get(JOB_NAME)
				.listener(jobExecutionListener())
				.start(createPartitionGrid(null))
					.on("*")
				.to(isPartitionGridPresent())
					.on(ExitStatus.FAILED.getExitCode()).fail()
				.from(isPartitionGridPresent())
					.on("NO_GRID").end()
				.from(isPartitionGridPresent())
					.on("CONTINUE").to(pollPartitionGrid(null))
				.from(pollPartitionGrid(null))
					.on(ExitStatus.FAILED.getExitCode()).fail()
				.from(pollPartitionGrid(null))
					.on("*").to(masterStep())
				.from(masterStep())
					.on(ExitStatus.FAILED.getExitCode()).fail()
				.from(masterStep())
					.on("*").to(isPartitionGridPresent())
				.end()
				.build();
	}

	@Bean
	public Step createPartitionGrid(PartitionGridTasklet tasklet) {
		return this.stepBuilderFactory.get("CreatePartitionGrid")
				.tasklet(tasklet)
				.build();
	}

	// Master Process each grid one by one
	@Bean
	public Step masterStep() {
		return this.stepBuilderFactory.get("PartitionStep")
				.partitioner(slaveStep(null).getName(), partitioner(null))
				.step(slaveStep(null)) // Slave Process values present inside grid one by one
				.taskExecutor(taskExecutor())
				.listener(stepExecutionListener())
				.build();
	}
	
	@Bean
	@StepScope
	public Partitioner partitioner(@Value("#{jobExecutionContext[PARTITION_GRID]}") Map<Long, List<Long>> statesPerPartition) {
		CitizenPartitioner citizenPartitioner = new CitizenPartitioner(statesPerPartition);
		return citizenPartitioner;
	}
	
	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setMaxPoolSize(maxPoolSize);
		taskExecutor.setCorePoolSize(corePoolSize);
		taskExecutor.setQueueCapacity(queueCapacity);
		return taskExecutor;
	}

	@Bean
	public Step slaveStep(PersonWriter writer) {
		return this.stepBuilderFactory.get("SlaveStep")
				.<Citizen, Citizen>chunk(chunkSize)
				.reader(jdbcPageItemReader(null))
				.writer(writer)
				.listener(stepExecutionListener())
				.build();
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<Citizen> jdbcPageItemReader(@Value("#{stepExecutionContext[partition]}")Long partition) {
		
		JdbcPagingItemReader<Citizen> jdbcPagingItemReader= new JdbcPagingItemReader<>();
		jdbcPagingItemReader.setFetchSize(fetchSize);
		jdbcPagingItemReader.setDataSource(this.dataSource);
		jdbcPagingItemReader.setRowMapper(new CitizenRowMapper());
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("stateId", partition);
		jdbcPagingItemReader.setParameterValues(parameters);
		
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("name, age, gender, aadharNumber, address, state_id");
		queryProvider.setFromClause("from citizen");
		queryProvider.setWhereClause("where state_id = :stateId ");
		
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
	
	@Bean
	public JobExecutionListener jobExecutionListener() {
		return new JobListener();
	}

	@Bean
	public JobExecutionDecider isPartitionGridPresent() {
		return ((JobExecution jobexecution, StepExecution stepExecution) -> {
			Queue<Map<Long, List<Long>>> statesPerPartition = helper.getPartitionGrids(jobexecution);
			if (CollectionUtils.isNotEmpty(statesPerPartition)) {
				return new FlowExecutionStatus("CONTINUE");
			} else {
				return new FlowExecutionStatus("NO_GRID");
			}
		});
	}

	@Bean
	public Step pollPartitionGrid(PollGridTasklet tasklet) {
		return this.stepBuilderFactory.get("PollGridTasklet")
				.tasklet(tasklet)
				.build();
	}
	
}