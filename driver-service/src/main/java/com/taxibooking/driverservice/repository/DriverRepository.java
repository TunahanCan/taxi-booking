package com.taxibooking.driverservice.repository;

import com.taxibooking.driverservice.model.DriverStageEntity;
import org.springframework.data.repository.CrudRepository;

public interface DriverRepository extends CrudRepository<DriverStageEntity, Long> {
}
