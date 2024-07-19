package com.carrefour.driveanddeliver.repository;

import com.carrefour.driveanddeliver.model.TimeSlot;

import java.util.List;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long>{
    List<TimeSlot> findByDeliveryMethodAndIsBookedFalseAndStartTimeAfter(DeliveryMethod method, LocalDateTime now);

}
