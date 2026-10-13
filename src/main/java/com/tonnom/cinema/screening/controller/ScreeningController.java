package com.tonnom.cinema.screening.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tonnom.cinema.screening.dto.ScreeningResponse;
import com.tonnom.cinema.screening.service.ScreeningService;
import com.tonnom.cinema.screening.dto.SeatsMapResponse;
import lombok.RequiredArgsConstructor;

@RestController 
@RequestMapping ("/screenings")
@RequiredArgsConstructor  
public class ScreeningController {
    
    private final ScreeningService screeningService;

    @GetMapping 
    public List<ScreeningResponse> getAll(
        @RequestParam(required = false) Long movieId,
        @RequestParam (required = false) LocalDate date
    ){
        return screeningService.getUpcoming(movieId, date);
    }

    @GetMapping("/{id}")
    public ScreeningResponse getOne(@PathVariable Long id){
        return screeningService.getById(id);
    }

    @GetMapping ("/{id}/seats")
    public SeatsMapResponse getSeats(@PathVariable Long id){
        return screeningService.getSeatsMap(id);
    }
}
