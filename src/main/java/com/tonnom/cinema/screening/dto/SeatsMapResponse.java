package com.tonnom.cinema.screening.dto;

import java.util.List;

import lombok.Builder;



public record SeatsMapResponse(Long screeningId, List<SeatInfo> seats){

    @Builder 
    public record SeatInfo(Long seatId, String rowLabel, Integer seatNumber, SeatStatus status){}
}
