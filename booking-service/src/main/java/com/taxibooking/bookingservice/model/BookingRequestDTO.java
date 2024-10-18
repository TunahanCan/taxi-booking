package com.taxibooking.bookingservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookingRequestDTO {
    private String bookingId;
    private String userId;
    private String pickupLocation;
    private String dropoffLocation;
    private String bookingTime;
}
