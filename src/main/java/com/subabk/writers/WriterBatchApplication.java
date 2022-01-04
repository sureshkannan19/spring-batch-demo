package com.subabk.writers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.subabk.writers", "com.subabk.common", "com.subabk.config" })
public class WriterBatchApplication {
	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(WriterBatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
	}
}
