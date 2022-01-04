package com.subabk.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class DataSourceConfiguration {

	public String profile = "dev";

	@Bean(name = "testDataSource")
	public DataSource mysqlDataSource() {

		// TODO: Change applicationprofile
		if (profile.equals("dev")) {
			DriverManagerDataSource dataSource = new DriverManagerDataSource();
			dataSource.setDriverClassName("com.mysql.jdbc.Driver");
			dataSource.setUrl("jdbc:mysql://localhost:3306/test?useSSL=false");
			dataSource.setUsername("student1");
			dataSource.setPassword("#student1");
			return dataSource;
		} else {
			return new EmbeddedDatabaseBuilder().setType(EmbeddedDatabaseType.H2).addScript("classpath:jdbc/schema.sql")
					.build();
		}
	}

	@Bean(name = "testJdbcTemplate")
	public JdbcTemplate getJdbcTemplate(@Qualifier("testDataSource") DataSource ds) {
		return new JdbcTemplate(ds);
	}

	public JavaMailSender getMailSender() {
		return new JavaMailSenderImpl();
	}

}