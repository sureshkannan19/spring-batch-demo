package com.subabk.parent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.subabk.parent",
		"com.subabk.readers",
		 "com.subabk.config",
		 "com.subabk.common"})
@Slf4j
public class ParentBatchApplication {

	public static void main(String[] args) {
		ApplicationContext applicationContext = SpringApplication.run(ParentBatchApplication.class, args);
		log.info("*******Loaded Beans*****");
//		for (String bean : applicationContext.getBeanDefinitionNames()) {
//			log.info(bean);
//		}
		System.exit(SpringApplication.exit(applicationContext));
	}

}
