package com.tonnom.cinema.booking.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tonnom.cinema.booking.service.SeatAvaibilityService;
import com.tonnom.cinema.booking.dto.SeatHoldResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping("/screenings/{screeningId}/seats/{seatId}/hold")
@RequiredArgsConstructor 
@Slf4j 
public class SeatHoldController {
    
    private final SeatAvaibilityService seatAvaibilityService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SeatHoldResponse hold(@PathVariable Long screeningId,
                   @PathVariable Long seatId,
                   @RequestParam Long userId) {
        return seatAvaibilityService.hold(screeningId, seatId, userId);
    }

    @DeleteMapping 
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void calcelHold(@PathVariable Long screeningId,
                   @PathVariable Long seatId,
                   @RequestParam Long userId){
        seatAvaibilityService.release(screeningId, List.of(seatId), userId);
    }
}
