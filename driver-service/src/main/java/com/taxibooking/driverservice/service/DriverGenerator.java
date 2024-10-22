package com.taxibooking.driverservice.service;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class DriverGenerator {

    private final Random random = new Random();
    private final List<String> driverNames = Arrays.asList(
            "Ahmet Yılmaz", "Mehmet Kaya", "Fatma Demir", "Ayşe Çelik", "Ali Koç", "Deniz Özkan", "Emre Güneş", "Hülya Aydın"
    );

    private final List<String> carModels = Arrays.asList(
            "Renault Megane", "Toyota Corolla", "Volkswagen Passat", "Ford Focus", "Honda Civic", "BMW 3 Serisi", "Mercedes C Serisi"
    );

    public String getRandomDriverInfo() {
        return driverNames.get(random.nextInt(driverNames.size()));
    }
    public String getRandomCarModel() {
        return carModels.get(random.nextInt(carModels.size()));
    }
}
