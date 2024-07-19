package com.carrefour.driveanddeliver.repository;

import com.carrefour.driveanddeliver.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
