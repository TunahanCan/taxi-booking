package com.taxibooking.driverservice.controller;

import com.taxibooking.driverservice.model.DriverAssignRequestDTO;
import com.taxibooking.driverservice.service.DriverKafkaService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class DriverController {

    private final DriverKafkaService driverKafkaService;

    @PostMapping("/assignDriver")
    public String assignDriver(@RequestBody DriverAssignRequestDTO assignRequest) {
        try {
            driverKafkaService.assignDriver(assignRequest);
            return "Driver assigned for bookingId: " + assignRequest.getBookingId();
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            return "Error in assigning driver.";
        }
    }
}