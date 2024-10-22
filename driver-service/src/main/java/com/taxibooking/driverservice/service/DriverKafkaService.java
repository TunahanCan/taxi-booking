package com.taxibooking.driverservice.service;


import com.taxibooking.driverservice.model.DriverStageDTO;
import com.taxibooking.driverservice.model.DriverStageEntity;
import com.taxibooking.driverservice.model.DriverTriggerDTO;
import com.taxibooking.driverservice.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
@RequiredArgsConstructor
@Slf4j
public class DriverKafkaService {

    private final DriverRepository driverRepository;
    private final DriverKafkaProducerService driverKafkaProducerService;
    private final DriverGenerator driverInfoGenerator;

    public DriverStageDTO convertToDto(DriverStageEntity driverStageEntity) {
        return new DriverStageDTO(
                driverStageEntity.getBookingId(),
                driverStageEntity.isDriverAssigned(),
                driverStageEntity.getDriverName(),
                driverStageEntity.getCarModel(),
                driverStageEntity.getCustomerName(),
                driverStageEntity.getPickupLocation(),
                driverStageEntity.getDropoffLocation(),
                driverStageEntity.getDriverDate()
        );
    }
    @KafkaListener(topics = "${driver.trigger.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "driverTriggerKafkaListenerContainerFactory")
    public void handleDriverTriggerEvent(DriverTriggerDTO driverTriggerDTO) {
        log.info("Received Driver Trigger Event: {}", driverTriggerDTO);
        DriverStageEntity driverStageEntity = new DriverStageEntity();
        driverStageEntity.setDriverName(driverInfoGenerator.getRandomDriverInfo());
        driverStageEntity.setCarModel(driverInfoGenerator.getRandomCarModel());
        driverStageEntity.setDriverAssigned(true);
        driverStageEntity.setDriverDate( new Date());
        driverStageEntity.setBookingId(driverTriggerDTO.bookingId());
        driverStageEntity.setDropoffLocation(driverTriggerDTO.dropoffLocation());
        driverStageEntity.setPickupLocation(driverTriggerDTO.pickupLocation());
        driverStageEntity.setCustomerName(driverTriggerDTO.customerName());
        driverRepository.save(driverStageEntity);
        driverKafkaProducerService.sendDriverStage(convertToDto(driverStageEntity),"driver-stage-completed");
    }
}
