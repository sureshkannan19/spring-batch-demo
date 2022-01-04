package com.subabk.jobdecider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.subabk.jobdecider",
		"com.subabk.common",
		"com.subabk.config"})
public class JobDeciderBatchApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(JobDeciderBatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
	}

}
