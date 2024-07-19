package com.carrefour.driveanddeliver.repository;

import com.carrefour.driveanddeliver.model.DeliveryMethod;
import com.carrefour.driveanddeliver.model.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByDeliveryMethodAndIsBookedFalseAndStartTimeAfter(DeliveryMethod method, LocalDateTime now);

}
