package com.subabk.basics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Without @Configuration, DemoApplication will work fine, but {@link MyBean} is
 * not Singleton anymore
 * 
 * How to Check:- Remove @Configuration and try 
 * Result :- MyBean instance created - will be printed twice.
 * 
 * @author Suresh Babu
 *
 */
@Configuration
public class DemoConfiguration {

	@Bean
	public MyBean myBean() {
		return new MyBean();
	}

	@Bean
	public MyBeanConsumer myBeanConsumer() {
		return new MyBeanConsumer(myBean());
	}
}
