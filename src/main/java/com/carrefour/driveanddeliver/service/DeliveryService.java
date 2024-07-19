// src/main/java/com/carrefour/driveanddeliver/service/DeliveryService.java
package com.carrefour.driveanddeliver.service;

import com.carrefour.driveanddeliver.exception.ResourceNotFoundException;
import com.carrefour.driveanddeliver.model.Delivery;
import com.carrefour.driveanddeliver.model.DeliveryMethod;
import com.carrefour.driveanddeliver.model.TimeSlot;
import com.carrefour.driveanddeliver.repository.DeliveryRepository;
import com.carrefour.driveanddeliver.repository.TimeSlotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeliveryService {

    private final TimeSlotRepository timeSlotRepository;
    private final DeliveryRepository deliveryRepository;

    @Autowired
    public DeliveryService(TimeSlotRepository timeSlotRepository, DeliveryRepository deliveryRepository) {
        this.timeSlotRepository = timeSlotRepository;
        this.deliveryRepository = deliveryRepository;
    }

    public List<TimeSlot> getAvailableTimeSlots(DeliveryMethod method) {
        return timeSlotRepository.findByDeliveryMethodAndIsBookedFalseAndStartTimeAfter(method, LocalDateTime.now());
    }

    @Transactional
    public Delivery bookDelivery(DeliveryMethod method, Long timeSlotId) {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("TimeSlot not found with id: " + timeSlotId));

        if (timeSlot.isBooked()) {
            throw new IllegalStateException("TimeSlot is already booked");
        }

        if (timeSlot.getDeliveryMethod() != method) {
            throw new IllegalArgumentException("TimeSlot is not available for the selected delivery method");
        }

        timeSlot.setBooked(true);
        timeSlotRepository.save(timeSlot);

        Delivery delivery = new Delivery();
        delivery.setMethod(method);
        delivery.setTimeSlot(timeSlot);

        return deliveryRepository.save(delivery);
    }

    public List<Delivery> getAllDeliveries() {
        return deliveryRepository.findAll();
    }

    public Delivery getDeliveryById(Long id) {
        return deliveryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Delivery not found with id: " + id));
    }

    @Transactional
    public void cancelDelivery(Long id) {
        Delivery delivery = getDeliveryById(id);
        TimeSlot timeSlot = delivery.getTimeSlot();
        timeSlot.setBooked(false);
        timeSlotRepository.save(timeSlot);
        deliveryRepository.delete(delivery);
    }

    @Transactional
    public TimeSlot createTimeSlot(TimeSlot timeSlot) {
        return timeSlotRepository.save(timeSlot);
    }

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }
}