package com.example.ranchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class RanChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(RanChatApplication.class, args);
    }

}
