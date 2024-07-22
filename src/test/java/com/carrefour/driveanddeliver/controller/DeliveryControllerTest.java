package com.carrefour.driveanddeliver.controller;

import com.carrefour.driveanddeliver.model.Delivery;
import com.carrefour.driveanddeliver.model.DeliveryMethod;
import com.carrefour.driveanddeliver.model.TimeSlot;
import com.carrefour.driveanddeliver.service.DeliveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeliveryController.class)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeliveryService deliveryService;

    @Autowired
    private ObjectMapper objectMapper;

    private TimeSlot timeSlot;
    private Delivery delivery;

    @BeforeEach
    void setUp() {
        timeSlot = new TimeSlot();
        timeSlot.setId(1L);
        timeSlot.setStartTime(LocalDateTime.now());
        timeSlot.setEndTime(LocalDateTime.now().plusHours(1));
        timeSlot.setDeliveryMethod(DeliveryMethod.DRIVE);
        timeSlot.setBooked(false);

        delivery = new Delivery();
        delivery.setId(1L);
        delivery.setMethod(DeliveryMethod.DRIVE);
        delivery.setTimeSlot(timeSlot);
    }

    @Test
    void getDeliveryMethods_ShouldReturnAllDeliveryMethods() throws Exception {
        mockMvc.perform(get("/api/deliveries/methods"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(DeliveryMethod.values())));
    }

    @Test
    void getAvailableTimeSlots_ShouldReturnAvailableTimeSlots() throws Exception {
        List<TimeSlot> timeSlots = Arrays.asList(timeSlot);
        when(deliveryService.getAvailableTimeSlots(any(DeliveryMethod.class))).thenReturn(timeSlots);

        mockMvc.perform(get("/api/deliveries/timeslots")
                .param("method", "DRIVE"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(timeSlots)));
    }

    @Test
    void bookDelivery_ShouldBookDeliverySuccessfully() throws Exception {
        when(deliveryService.bookDelivery(any(DeliveryMethod.class), anyLong())).thenReturn(delivery);

        mockMvc.perform(post("/api/deliveries/book")
                .param("method", "DRIVE")
                .param("timeSlotId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(delivery)));
    }

    @Test
    void getAllDeliveries_ShouldReturnAllDeliveries() throws Exception {
        List<Delivery> deliveries = Arrays.asList(delivery);
        when(deliveryService.getAllDeliveries()).thenReturn(deliveries);

        mockMvc.perform(get("/api/deliveries"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(deliveries)));
    }

    @Test
    void getDeliveryById_ShouldReturnDelivery() throws Exception {
        when(deliveryService.getDeliveryById(anyLong())).thenReturn(delivery);

        mockMvc.perform(get("/api/deliveries/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(delivery)));
    }

    @Test
    void cancelDelivery_ShouldCancelDeliverySuccessfully() throws Exception {
        doNothing().when(deliveryService).cancelDelivery(anyLong());

        mockMvc.perform(delete("/api/deliveries/1"))
                .andExpect(status().isOk());

        verify(deliveryService).cancelDelivery(1L);
    }

    @Test
    void createTimeSlot_ShouldCreateTimeSlotSuccessfully() throws Exception {
        when(deliveryService.createTimeSlot(any(TimeSlot.class))).thenReturn(timeSlot);

        mockMvc.perform(post("/api/deliveries/timeslots")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeSlot)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(timeSlot)));
    }

    @Test
    void getAllTimeSlots_ShouldReturnAllTimeSlots() throws Exception {
        List<TimeSlot> timeSlots = Arrays.asList(timeSlot);
        when(deliveryService.getAllTimeSlots()).thenReturn(timeSlots);

        mockMvc.perform(get("/api/deliveries/timeslots"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(timeSlots)));
    }
}
