package com.expenses;

import org.springframework.boot.SpringApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.core.env.Environment;

@SpringBootApplication
//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class ExpensesBeApplication {
//	private static final Logger logger = LoggerFactory.getLogger(ExpensesBeApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ExpensesBeApplication.class, args);
		
//		SpringApplication app = new SpringApplication(ExpensesBeApplication.class);
//        Environment env = app.run(args).getEnvironment();
//        logger.info("Application running on port: " + env.getProperty("server.port"));
//        logger.info("LiveReload enabled: " + env.getProperty("spring.devtools.livereload.enabled"));
	   
	}

}
