package com.subabk.cache;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.subabk.cache", "com.subabk.util", "com.subabk.common", "com.subabk.config" })
@EnableCaching
public class CacheBatchApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(CacheBatchApplication.class, args);
		System.exit(SpringApplication.exit(applicationContext));
	}
}
