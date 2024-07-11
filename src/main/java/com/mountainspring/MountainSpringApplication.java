package com.mountainspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties
@EnableJpaRepositories
public class MountainSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MountainSpringApplication.class, args);
        isAnagram("racecar","carrace");
    }

    public static boolean isAnagram(String s, String t) {
        String str = "Hello, Baeldung!";
        for (int i = 0; i < str.length(); i++) {
            char c = s.charAt(i);
            System.out.print(c);
        }
        return false;
    };

}
