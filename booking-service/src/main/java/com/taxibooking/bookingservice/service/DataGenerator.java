package com.taxibooking.bookingservice.service;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;


@Component
@Order(100000)
@RequiredArgsConstructor
public class DataGenerator {

    @PostConstruct
    public void init() {
    System.out.println("DataGenerator");
    }   


    

}
