package com.taxibooking.bookingservice.configuration;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class ConsumerKafkaConfig {
    @PostConstruct
    public void init() {
      log.warn("ConsumerKafkaConfig init");
    }

    
}
