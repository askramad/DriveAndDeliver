package com.carrefour.driveanddeliver.service;

import com.carrefour.driveanddeliver.exception.ResourceNotFoundException;
import com.carrefour.driveanddeliver.model.Delivery;
import com.carrefour.driveanddeliver.model.DeliveryMethod;
import com.carrefour.driveanddeliver.model.TimeSlot;
import com.carrefour.driveanddeliver.repository.DeliveryRepository;
import com.carrefour.driveanddeliver.repository.TimeSlotRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeliveryServiceTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private DeliveryRepository deliveryRepository;

    @InjectMocks
    private DeliveryService deliveryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAvailableTimeSlots_ShouldReturnAvailableTimeSlots() {
        // Arrange
        DeliveryMethod method = DeliveryMethod.DRIVE;
        List<TimeSlot> expectedTimeSlots = Arrays.asList(new TimeSlot(), new TimeSlot());
        when(timeSlotRepository.findByDeliveryMethodAndIsBookedFalseAndStartTimeAfter(eq(method), any(LocalDateTime.class)))
                .thenReturn(expectedTimeSlots);

        // Act
        List<TimeSlot> actualTimeSlots = deliveryService.getAvailableTimeSlots(method);

        // Assert
        assertEquals(expectedTimeSlots, actualTimeSlots);
        verify(timeSlotRepository).findByDeliveryMethodAndIsBookedFalseAndStartTimeAfter(eq(method), any(LocalDateTime.class));
    }

    @Test
    void bookDelivery_ShouldBookDeliverySuccessfully() {
        // Arrange
        DeliveryMethod method = DeliveryMethod.DRIVE;
        Long timeSlotId = 1L;
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(timeSlotId);
        timeSlot.setDeliveryMethod(method);
        timeSlot.setBooked(false);

        when(timeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(timeSlot));
        when(deliveryRepository.save(any(Delivery.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Delivery bookedDelivery = deliveryService.bookDelivery(method, timeSlotId);

        // Assert
        assertNotNull(bookedDelivery);
        assertEquals(method, bookedDelivery.getMethod());
        assertEquals(timeSlot, bookedDelivery.getTimeSlot());
        assertTrue(timeSlot.isBooked());
        verify(timeSlotRepository).save(timeSlot);
        verify(deliveryRepository).save(any(Delivery.class));
    }

    @Test
    void bookDelivery_ShouldThrowException_WhenTimeSlotNotFound() {
        // Arrange
        DeliveryMethod method = DeliveryMethod.DRIVE;
        Long timeSlotId = 1L;
        when(timeSlotRepository.findById(timeSlotId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> deliveryService.bookDelivery(method, timeSlotId));
    }

    @Test
    void bookDelivery_ShouldThrowException_WhenTimeSlotAlreadyBooked() {
        // Arrange
        DeliveryMethod method = DeliveryMethod.DRIVE;
        Long timeSlotId = 1L;
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(timeSlotId);
        timeSlot.setDeliveryMethod(method);
        timeSlot.setBooked(true);

        when(timeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(timeSlot));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> deliveryService.bookDelivery(method, timeSlotId));
    }

    @Test
    void bookDelivery_ShouldThrowException_WhenDeliveryMethodMismatch() {
        // Arrange
        DeliveryMethod requestedMethod = DeliveryMethod.DRIVE;
        DeliveryMethod actualMethod = DeliveryMethod.DELIVERY;
        Long timeSlotId = 1L;
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(timeSlotId);
        timeSlot.setDeliveryMethod(actualMethod);
        timeSlot.setBooked(false);

        when(timeSlotRepository.findById(timeSlotId)).thenReturn(Optional.of(timeSlot));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> deliveryService.bookDelivery(requestedMethod, timeSlotId));
    }

    @Test
    void getAllDeliveries_ShouldReturnAllDeliveries() {
        // Arrange
        List<Delivery> expectedDeliveries = Arrays.asList(new Delivery(), new Delivery());
        when(deliveryRepository.findAll()).thenReturn(expectedDeliveries);

        // Act
        List<Delivery> actualDeliveries = deliveryService.getAllDeliveries();

        // Assert
        assertEquals(expectedDeliveries, actualDeliveries);
        verify(deliveryRepository).findAll();
    }

    @Test
    void getDeliveryById_ShouldReturnDelivery_WhenFound() {
        // Arrange
        Long deliveryId = 1L;
        Delivery expectedDelivery = new Delivery();
        expectedDelivery.setId(deliveryId);
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(expectedDelivery));

        // Act
        Delivery actualDelivery = deliveryService.getDeliveryById(deliveryId);

        // Assert
        assertEquals(expectedDelivery, actualDelivery);
        verify(deliveryRepository).findById(deliveryId);
    }

    @Test
    void getDeliveryById_ShouldThrowException_WhenNotFound() {
        // Arrange
        Long deliveryId = 1L;
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> deliveryService.getDeliveryById(deliveryId));
    }

    @Test
    void cancelDelivery_ShouldCancelDeliverySuccessfully() {
        // Arrange
        Long deliveryId = 1L;
        Delivery delivery = new Delivery();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setBooked(true);
        delivery.setTimeSlot(timeSlot);
        when(deliveryRepository.findById(deliveryId)).thenReturn(Optional.of(delivery));

        // Act
        deliveryService.cancelDelivery(deliveryId);

        // Assert
        assertFalse(timeSlot.isBooked());
        verify(timeSlotRepository).save(timeSlot);
        verify(deliveryRepository).delete(delivery);
    }

    @Test
    void createTimeSlot_ShouldCreateTimeSlotSuccessfully() {
        // Arrange
        TimeSlot timeSlot = new TimeSlot();
        when(timeSlotRepository.save(timeSlot)).thenReturn(timeSlot);

        // Act
        TimeSlot createdTimeSlot = deliveryService.createTimeSlot(timeSlot);

        // Assert
        assertEquals(timeSlot, createdTimeSlot);
        verify(timeSlotRepository).save(timeSlot);
    }

    @Test
    void getAllTimeSlots_ShouldReturnAllTimeSlots() {
        // Arrange
        List<TimeSlot> expectedTimeSlots = Arrays.asList(new TimeSlot(), new TimeSlot());
        when(timeSlotRepository.findAll()).thenReturn(expectedTimeSlots);

        // Act
        List<TimeSlot> actualTimeSlots = deliveryService.getAllTimeSlots();

        // Assert
        assertEquals(expectedTimeSlots, actualTimeSlots);
        verify(timeSlotRepository).findAll();
    }
}
