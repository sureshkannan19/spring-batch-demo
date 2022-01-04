package com.subabk.restart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.subabk.restart", "com.subabk.config", "com.subabk.common" })
public class StatefulBatchApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(StatefulBatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
	}

}
