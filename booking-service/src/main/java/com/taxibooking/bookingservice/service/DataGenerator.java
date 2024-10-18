package com.taxibooking.bookingservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.taxibooking.bookingservice.model.BookingRequestDTO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Order(100000)
@RequiredArgsConstructor
public class DataGenerator {

}
