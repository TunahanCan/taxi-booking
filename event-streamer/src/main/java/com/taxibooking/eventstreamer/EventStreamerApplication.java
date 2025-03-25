package com.taxibooking.eventstreamer;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class EventStreamerApplication  implements CommandLineRunner {

    public static void main(String... args) {
        SpringApplication.run(EventStreamerApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Event Streamer started");
    }
}