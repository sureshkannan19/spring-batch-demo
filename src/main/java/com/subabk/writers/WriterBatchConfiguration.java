package com.subabk.writers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.xstream.XStreamMarshaller;

import com.subabk.bo.Citizen;
import com.subabk.listener.JobListener;
import com.subabk.listener.StepListener;
import com.subabk.readers.mapper.CitizenRowMapper;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class WriterBatchConfiguration {

	public static final String JOB_NAME = "WriterJob";

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	@Qualifier("testDataSource")
	private DataSource dataSource;
	
	@Bean
	public Job writerJob() {
		
		return this.jobBuilderFactory.get(JOB_NAME)
				.start(writerStep())
				.listener(jobExecutionListener())
				.build();
				
	}
	
	// Since flat file and xml file writer are non-transactional, 
	// requesting spring by adding .stream to make it as transactional
	@Bean
	public Step writerStep() {
		return this.stepBuilderFactory.get("WriterStep")
				.<Citizen, Citizen>chunk(10)
				.reader(jdbcPageItemReader())
				.writer(classifiedWriter())
				.stream(xmlFileItemWriter())
				.stream(flatFileItemWriter()) 
				.listener(stepExecutionListener())
				.build();
	}
	
	
	@Bean 
	public JdbcPagingItemReader<Citizen> jdbcPageItemReader() {
		JdbcPagingItemReader<Citizen> jdbcPagingItemReader= new JdbcPagingItemReader<>();
		jdbcPagingItemReader.setFetchSize(20);
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
	public CompositeItemWriter<Citizen> compositeWriter() {
		CompositeItemWriter<Citizen> writer = new CompositeItemWriter<>();
		List<ItemWriter< ? super Citizen>> citizenWriters = new ArrayList<>();
		citizenWriters.add(xmlFileItemWriter());
		citizenWriters.add(flatFileItemWriter());
		writer.setDelegates(citizenWriters);
		return writer;
	}
	
	@Bean
	public ClassifierCompositeItemWriter<Citizen> classifiedWriter() {
		ClassifierCompositeItemWriter<Citizen> writer = new ClassifierCompositeItemWriter<>();
		List<ItemWriter< ? super Citizen>> citizenWriters = new ArrayList<>();
		citizenWriters.add(xmlFileItemWriter());
		citizenWriters.add(flatFileItemWriter());
		writer.setClassifier(new ClassifierItemWriter(xmlFileItemWriter(), flatFileItemWriter()));
		return writer;
	}
	
	@Bean
	public FlatFileItemWriter<Citizen> flatFileItemWriter() {
		FlatFileItemWriter<Citizen> writer = new FlatFileItemWriter<>();
		writer.setLineAggregator(new PassThroughLineAggregator<>()); // use toString() to convert
		try {
			String outPutFilePath = File.createTempFile("citizen", ".out",new File("D:\\CodeBase\\")).getAbsolutePath();
			writer.setResource(new FileSystemResource(outPutFilePath));
			writer.afterPropertiesSet();
		} catch (Exception e) {
			log.info("Exception Occured: ", e);
		}
		return writer;
	}
	
	@Bean
	public StaxEventItemWriter<Citizen> xmlFileItemWriter() {
		XStreamMarshaller marshaller = new XStreamMarshaller();

		StaxEventItemWriter<Citizen> writer = new StaxEventItemWriter<>();
		writer.setRootTagName("citizens");

		Map<String, Class<Citizen>> aliases = new HashMap<>();
		aliases.put("citizen", Citizen.class);
		marshaller.setAliases(aliases);

		try {
			String outPutFilePath = File.createTempFile("citizen", ".xml", new File("D:\\CodeBase\\"))
					.getAbsolutePath();
			writer.setResource(new FileSystemResource(outPutFilePath));
			writer.setMarshaller(marshaller);
			writer.afterPropertiesSet();
		} catch (Exception e) {
			log.info("Exception Occured: ", e);
		}
		return writer;
	}

	@Bean
	public StepExecutionListener stepExecutionListener() {
		return new StepListener();
	}
	
	@Bean
	public JobExecutionListener jobExecutionListener() {
		return new JobListener();
	}
}