package com.taxibooking.bookingservice.model;

import java.util.Date;

public record DriverStageDTO(
        String bookingId,
        boolean driverAssigned,
        String driverName,
        String carModel,
        String customerName,
        String pickupLocation,
        String dropoffLocation,
        Date driverDate
) {
}
