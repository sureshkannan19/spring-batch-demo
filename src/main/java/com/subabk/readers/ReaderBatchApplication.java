package com.subabk.readers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.subabk.readers",
								"com.subabk.common",
								"com.subabk.config",
								"com.subabk.readers.mapper"})
public class ReaderBatchApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(ReaderBatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
	}

}
