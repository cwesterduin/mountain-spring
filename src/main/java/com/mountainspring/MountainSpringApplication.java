package com.mountainspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories
public class MountainSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MountainSpringApplication.class, args);
    }

}
