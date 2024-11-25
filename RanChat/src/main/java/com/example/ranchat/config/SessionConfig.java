package com.example.ranchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class SessionConfig {
    // Spring Session을 활성화하여 Redis를 세션 저장소로 사용하도록 설정
}
