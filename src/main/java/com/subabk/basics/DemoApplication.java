package com.subabk.basics;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class DemoApplication {

	public static void main(String[] args) {
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(DemoConfiguration.class);
		ctx.refresh();

		/**
		 * mb1 and mb2 not calling the MyBean constructor method,
		 * mean's in Spring everything is Singleton unless we specify it with Scope annotation
		 * 
		 *  Eg:- 
		 *  1. It can be ensured by calling the method or
		 *  2. by printing its hashcode(will be same for mb1 and mb2) 
		 */
//		MyBean mb1 = ctx.getBean(MyBean.class);
//		System.out.println(mb1);
//		MyBean mb2 = ctx.getBean(MyBean.class);
//		System.out.println(mb2);
		ctx.close();
	}
}
