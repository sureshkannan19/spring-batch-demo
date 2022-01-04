package com.subabk.readers;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.sql.DataSource;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.PassThroughItemProcessor;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.subabk.bo.Citizen;
import com.subabk.listener.ChunkListener;
import com.subabk.listener.JobListener;
import com.subabk.listener.StepListener;
import com.subabk.readers.mapper.CitizenFieldSetMapper;
import com.subabk.readers.mapper.CitizenRowMapper;
import com.subabk.readers.mapper.SimpleProcessor;
import com.subabk.readers.mapper.SimpleReader;

import lombok.extern.slf4j.Slf4j;

/**
 * Types of reader implemented in this job : 
 * {@link JdbcPagingItemReader}, {@link JdbcCursorItemReader} , {@link FlatFileItemReader} , {@link StaxEventItemReader}
 * @author Suresh Babu
 */
@Configuration
@Slf4j
public class ReaderBatchConfiguration {

	private static final String DELIMITER = ";";

	public static final String JOB_NAME = "ReadersJob";
	
	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	@Qualifier("testDataSource")
	private DataSource dataSource;
	
	@Autowired
	@Qualifier("testJdbcTemplate")
	private JdbcTemplate testJdbcTemplate;
	
	@Value("/citizen*.csv") // or @Value("classpath*:/citizen*.csv")
	private Resource[] resources;
	
	@Bean
	public Job readersJob() {
		return this.jobBuilderFactory.get(JOB_NAME)
				.listener(jobExecutionListener())
				.start(readersStep())
//				.on("*").to(simpleChunkStep())
//				.on("*").to(jdbcCursorChunkStep())
				.on("*").to(jdbcPageItemReaderStep())
//				.on("*").to(xmlFileItemReaderStep())
//				.on("*").to(multiResourceItemReaderStep())
				.end()
				.build();
	}

	@Bean
	public Step readersStep() {
		return this.stepBuilderFactory.get("readersStep")
				.tasklet(jobParamTasklet(null))
				.listener(stepExecutionListener())
				.build();
	}

	/**
	 *Unlike other Singleton class, @StepScope Instantitate's new bean only when the method(jobParamTasklet) is called
	*/ 
	@Bean
	@StepScope
	public Tasklet jobParamTasklet(@Value("#{jobParameters['message']}")String message) {
		return (contribution,chunkContext) -> {
			log.info("Inside JobParamTasklet.");
			log.info("JobParam : {} ", message);
			String sql ="INSERT INTO `test`.`citizen`"
					+ "(`aadharNumber`,"
					+ "`name`,"
					+ "`age`,"
					+ "`address`,"
					+ "`gender`,"
					+ "`state_id`)"
					+ "VALUES"
					+ "(?,?,?,?,?,?);"
					+ "";
			
			List<Long> ids = LongStream.rangeClosed(1, 1000).boxed().collect(Collectors.toList());
			testJdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
						ps.setLong(1, ids.get(i));
						ps.setString(2,RandomStringUtils.randomAlphabetic(6));
						ps.setInt(3, 23);
						ps.setString(4,"Chennai");
						ps.setString(5, "Male");
						ps.setInt(6, 1);
					
				}
				
				@Override
				public int getBatchSize() {
					return ids.size();
				}
			});

			return RepeatStatus.FINISHED;
		};
	}

	@Bean
	public Step simpleChunkStep() {
		return stepBuilderFactory.get("simpleChunkStep")
				.<Long, Long>chunk(1)
				.listener(new ChunkListener())
				.reader(new SimpleReader(getDummyValues().iterator()))
				.processor(new SimpleProcessor())// processor is optional
				.writer(items-> {
					for (Long item : items) {
						log.info("Item Id :{} ",item);		
					}
				})
				.listener(stepExecutionListener())
				.build();
	}
	
	@Bean
	public Step jdbcCursorChunkStep() {
		return stepBuilderFactory.get("JdbcCursorChunkStep")
				.<Citizen, Citizen>chunk(2)
				.reader(jdbcCursorItemReader())
				.writer(printCitizens())
				.listener(stepExecutionListener())
				.build();
	}
	
	@Bean
	public Step jdbcPageItemReaderStep() {
		return stepBuilderFactory.get("JdbcPageItemReaderStep")
				.<Citizen, Citizen>chunk(500)
				.reader(jdbcPageItemReader())
				.writer(printCitizens())
				.listener(stepExecutionListener())
				.build();
	}
	
	@Bean
	public Step flatFileItemReaderStep() {
		return stepBuilderFactory.get("FlatFileItemReaderStep")
				.<Citizen, Citizen>chunk(2)
				.reader(flatFileItemReader())
				.writer(printCitizens())
				.listener(stepExecutionListener())
				.build();
	}
	
	@Bean
	public Step xmlFileItemReaderStep() {
		return stepBuilderFactory.get("XmlFileItemReaderStep")
				.<Citizen, Citizen>chunk(2)
				.reader(xmlFileItemReader())
				.processor(new PassThroughItemProcessor<>())// processor is optional
				.writer(printCitizens())
				.listener(stepExecutionListener())
				.build();
	}
	
	@Bean
	public Step multiResourceItemReaderStep() {
		return stepBuilderFactory.get("MultiResourceItemReaderStep")
				.<Citizen, Citizen>chunk(10)
				.reader(multiResourceItemReader())
				.processor(new PassThroughItemProcessor<>())// processor is optional
				.writer(printCitizens())
				.listener(stepExecutionListener())
				.build();
	}

	@Bean
	public ItemWriter<Citizen> printCitizens() {
		return citizens-> {
			log.info("Total citizens per chunk {} ",citizens.size());
			for (Citizen citizen : citizens) {
				log.info("Citizen Details {} ",citizen);		
			}
		};
	}
	
	@Bean
	public StepExecutionListener stepExecutionListener() {
		return new StepListener();
	}
	
	@Bean
	public JobExecutionListener jobExecutionListener() {
		return new JobListener();
	}

	private List<Long> getDummyValues() {
		return LongStream.rangeClosed(1, 10).boxed().collect(Collectors.toList());
	}
	

	/**
	 * JdbcCursorItemReader is not thread Safe (multiple threads would be reading from the same ResultSet). 
	 */
	@Bean 
	public JdbcCursorItemReader<Citizen> jdbcCursorItemReader() {
		JdbcCursorItemReader<Citizen> jdbcCursorItemReader= new JdbcCursorItemReader<>();
		// OrderBy used for to start picking next set of records, where it left by no of rows it have processed, for subsequent Iterations
	    // Note: If order by is not given, then it will sort by primarykey present for that table. 
		jdbcCursorItemReader.setSql("Select name, age, gender, aadharNumber, address FROM citizen order by name");
		jdbcCursorItemReader.setFetchSize(2);
		jdbcCursorItemReader.setDataSource(this.dataSource);
		jdbcCursorItemReader.setRowMapper(new CitizenRowMapper());
		return jdbcCursorItemReader;
	}

	/**
	 * JdbcPagingItemReader is thread Safe. 
	 */
	@Bean 
	public JdbcPagingItemReader<Citizen> jdbcPageItemReader() {
		JdbcPagingItemReader<Citizen> jdbcPagingItemReader= new JdbcPagingItemReader<>();
		jdbcPagingItemReader.setFetchSize(500);
		jdbcPagingItemReader.setDataSource(this.dataSource);
		jdbcPagingItemReader.setRowMapper(new CitizenRowMapper());
		
		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("name, age, gender, aadharNumber, address, state_id");
		queryProvider.setFromClause("from citizen");
		
        // SortKeys Must be specified, inOrder to pick next set of records for subsequent iterations.
		Map<String, Order> sortKeys = new HashMap<>();
		sortKeys.put("aadharNumber", Order.ASCENDING);
		queryProvider.setSortKeys(sortKeys);

		jdbcPagingItemReader.setQueryProvider(queryProvider);
		return jdbcPagingItemReader;
	}
	
	@Bean 
	public FlatFileItemReader<Citizen> flatFileItemReader() {
		FlatFileItemReader<Citizen> reader = new FlatFileItemReader<>();
		reader.setLinesToSkip(1); // Columns Name's Skipped
		reader.setResource(new ClassPathResource("/citizen.csv"));
		
		DefaultLineMapper<Citizen> mapper = new DefaultLineMapper<>();
		
		DelimitedLineTokenizer tokenizer= new DelimitedLineTokenizer(DELIMITER);
		tokenizer.setNames(new String[] { "name", "age", "gender", "aadharNumber", "address" });
		
		mapper.setLineTokenizer(tokenizer);
		mapper.setFieldSetMapper(new CitizenFieldSetMapper());
		mapper.afterPropertiesSet();//
		
		reader.setLineMapper(mapper);
		return reader;
	}
	
	@Bean 
	public StaxEventItemReader<Citizen> xmlFileItemReader() {
		XStreamMarshaller unmarshaller = new XStreamMarshaller();
		
		Map<String, Class<Citizen>> aliases = new HashMap<>();
		aliases.put("citizen", Citizen.class);
		unmarshaller.setAliases(aliases);
		
		StaxEventItemReader<Citizen> reader= new StaxEventItemReader<>();
		reader.setResource(new ClassPathResource("/citizen.xml"));	
		reader.setFragmentRootElementName("citizen");
		reader.setUnmarshaller(unmarshaller);
		
		return reader;
	}
	
	
	@Bean 
	public MultiResourceItemReader<Citizen> multiResourceItemReader() {
		MultiResourceItemReader<Citizen> reader= new MultiResourceItemReader<>();
		reader.setResources(resources);
		reader.setDelegate(flatFileItemReader());
		return reader;
	}
}