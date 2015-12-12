package com.anyi.gp.message.jms.util;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUtils {
	private static ApplicationContext context = null;

	static {
		context = new ClassPathXmlApplicationContext("applicationContext_jms.xml");
	}
	
	public static Object getBean(String name) {
		Object bean = context.getBean(name);
		return bean;
	}
}
