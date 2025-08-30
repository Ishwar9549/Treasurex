package com.example.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
public class SpringJwtApplication {

    private static final Logger logger = LoggerFactory.getLogger(SpringJwtApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SpringJwtApplication.class, args);
        logger.info("Spring JWT 3 application started successfully.");
        System.err.println("Spring JWT 4 application started successfully.");
    }
}
//100% 