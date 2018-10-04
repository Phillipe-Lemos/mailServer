package de.mail.configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MailConfiguration {

	private final int POOL_SIZE = 5;
	
	@Bean
	public ScheduledExecutorService createScheduledThreadPoolExecutor() {
		return Executors.newScheduledThreadPool(POOL_SIZE);
	}
	
	
}
