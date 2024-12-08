package com.example.ranchat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class RanChatApplication {

    public static void main(String[] args) {
        SpringApplication.run(RanChatApplication.class, args);
    }

}
