package com.example.ranchat.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.example.ranchat.message.entity.MessageDTO;
import com.example.ranchat.redis.RedisSubscriber;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {
	@Value("${spring.data.redis.host}")
	private String redisHost;
	@Value("${spring.data.redis.port}")
	private int redisPort;

	@Bean
	public MessageListenerAdapter messageListenerAdapter(RedisSubscriber redisSubscriber, ObjectMapper objectMapper) {
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		Jackson2JsonRedisSerializer<MessageDTO> serializer = new Jackson2JsonRedisSerializer<>(objectMapper,
			MessageDTO.class);

		MessageListenerAdapter adapter = new MessageListenerAdapter(redisSubscriber, "handleMessage");
		adapter.setSerializer(serializer);
		adapter.afterPropertiesSet();

		return adapter;
	}

	@Bean
	public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory,
		MessageListenerAdapter listenerAdapter) {

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);

		// 채널 패턴을 지정하여 구독
		container.addMessageListener(listenerAdapter, new PatternTopic("chatRoom:*"));

		return container;
	}

	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		return new LettuceConnectionFactory(redisHost, redisPort);
	}

	@Bean(name = "customStringRedisTemplate")
	@Primary
	public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());

		template.setHashKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new StringRedisSerializer());

		return template;
	}

	@Bean(name = "customBooleanRedisTemplate")
	public RedisTemplate<String, Boolean> booleanRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Boolean> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);

		StringRedisSerializer stringSerializer = new StringRedisSerializer();
		template.setKeySerializer(stringSerializer);
		template.setHashKeySerializer(stringSerializer);

		Jackson2JsonRedisSerializer<Boolean> booleanSerializer = new Jackson2JsonRedisSerializer<>(Boolean.class);

		template.setValueSerializer(booleanSerializer);
		template.setHashValueSerializer(booleanSerializer);

		return template;
	}

	@Bean(name = "customMessageRedisTemplate")
	public RedisTemplate<String, MessageDTO> messageRedisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, MessageDTO> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		template.setKeySerializer(new StringRedisSerializer());

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule());
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		Jackson2JsonRedisSerializer<MessageDTO> redisSerializer = new Jackson2JsonRedisSerializer<>(objectMapper,
			MessageDTO.class);

		template.setValueSerializer(redisSerializer);
		template.setHashValueSerializer(redisSerializer);

		return template;
	}

}