package com.carrefour.driveanddeliver.controller;

import com.carrefour.driveanddeliver.model.Delivery;
import com.carrefour.driveanddeliver.model.DeliveryMethod;
import com.carrefour.driveanddeliver.model.TimeSlot;
import com.carrefour.driveanddeliver.service.DeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @GetMapping("/methods")
    public ResponseEntity<DeliveryMethod[]> getDeliveryMethods() {
        return ResponseEntity.ok(DeliveryMethod.values());
    }

    @GetMapping("/timeslots")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(@RequestParam DeliveryMethod method) {
        List<TimeSlot> timeSlots = deliveryService.getAvailableTimeSlots(method);
        return ResponseEntity.ok(timeSlots);
    }

    @PostMapping("/book")
    public ResponseEntity<Delivery> bookDelivery(@RequestParam DeliveryMethod method, @RequestParam Long timeSlotId) {
        Delivery delivery = deliveryService.bookDelivery(method, timeSlotId);
        return ResponseEntity.ok(delivery);
    }
}
