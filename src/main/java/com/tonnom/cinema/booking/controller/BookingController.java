package com.tonnom.cinema.booking.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.tonnom.cinema.booking.service.BookingService;
import com.tonnom.cinema.booking.dto.BookingResponse;
import com.tonnom.cinema.booking.dto.CreateBookingRequest;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor 
@Slf4j 
public class BookingController {
    private final BookingService bookingService;
    

    @PostMapping 
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(@Valid @RequestBody CreateBookingRequest request){
        return bookingService.create(request);
    }


    
    @GetMapping
    public List<BookingResponse> getByUser(@RequestParam Long userId){
        return bookingService.getByUser(userId);
    }

    @GetMapping("/{reference}")
    public BookingResponse getByReference(@PathVariable String reference){
        return bookingService.getByReference(reference);
    }

    @DeleteMapping("/{reference}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable String reference){
        bookingService.cancel(reference);
    }
}
