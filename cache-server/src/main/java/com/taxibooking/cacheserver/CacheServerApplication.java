package com.taxibooking.cacheserver;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CacheServerApplication implements CommandLineRunner {

    public static void main(String... args) {
        SpringApplication.run(CacheServerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Cache Server started");
    }
}