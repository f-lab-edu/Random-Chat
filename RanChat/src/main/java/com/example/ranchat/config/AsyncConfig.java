package com.example.ranchat.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@EnableAsync
@Configuration
public class AsyncConfig {
	@Bean(name = "chatMatchTaskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(15);
		executor.setMaxPoolSize(25);
		executor.setQueueCapacity(10);
		executor.setKeepAliveSeconds(30);
		executor.setThreadNamePrefix("async-ranChatMatching-");
		executor.initialize();
		return executor;
	}

}
