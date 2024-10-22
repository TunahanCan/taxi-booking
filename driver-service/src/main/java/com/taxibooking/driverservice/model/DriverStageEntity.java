package com.taxibooking.driverservice.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class DriverStageEntity {
    @Id
    @Column(name = "driver_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long driverId;

    @Column(name = "booking_id", nullable = false)
    private String bookingId;

    @Column(name = "driver_assigned", nullable = false)
    private boolean driverAssigned;

    @Column(name = "driver_name", nullable = false)
    private String driverName;

    @Column(name = "car_model")
    private String carModel;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "pickup_location")
    String pickupLocation;

    @Column(name = "dropoff_location")
    String dropoffLocation;

    @Column(name = "driver_date")
    private Date driverDate;
}
