package com.example.privatedining;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication(exclude = {MongoAutoConfiguration.class})
public class PrivateDiningApplication {
    public static void main(String[] args) {
        SpringApplication.run(PrivateDiningApplication.class, args);
    }
}
