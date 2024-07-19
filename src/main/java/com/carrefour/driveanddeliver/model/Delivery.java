package com.carrefour.driveanddeliver.model;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Data
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private DeliveryMethod method;

    @OneToOne
    private TimeSlot timeSlot;
}
