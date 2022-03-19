package com.mountainspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties

public class MountainSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MountainSpringApplication.class, args);
    }

}
