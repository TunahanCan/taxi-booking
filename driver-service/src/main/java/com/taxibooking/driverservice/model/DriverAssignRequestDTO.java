package com.taxibooking.driverservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DriverAssignRequestDTO {
    private String bookingId;
    private String pickupLocation;
    private String dropoffLocation;
}
