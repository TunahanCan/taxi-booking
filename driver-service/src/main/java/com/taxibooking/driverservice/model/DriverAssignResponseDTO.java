package com.taxibooking.driverservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DriverAssignResponseDTO {
    private String bookingId;
    private String driverId;
    private String status;
}
