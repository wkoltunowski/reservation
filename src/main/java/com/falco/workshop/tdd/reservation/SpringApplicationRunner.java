package com.falco.workshop.tdd.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * .
 */
@SpringBootApplication
@EnableCaching
public class SpringApplicationRunner {
    public static void main(String[] args) {
        SpringApplication.run(SpringApplicationRunner.class, args);
    }
}
