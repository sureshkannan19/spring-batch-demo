package com.subabk.config;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing // adds many critical beans that support jobs
@EnableAutoConfiguration
public class BatchConfiguration extends DefaultBatchConfigurer {

	// clear() inside MapJobRepositoryFactoryBean is used to clear the data stored execution context(Job Repository)
	@Override
	protected JobRepository createJobRepository() throws Exception {
		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean();
		factory.setTransactionManager(getTransactionManager());
		return factory.getObject();
	}
}