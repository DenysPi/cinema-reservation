package com.tonnom.cinema.booking.dto;

import java.util.List;

import com.tonnom.cinema.screening.dto.SeatStatus;




 
public record SeatUpdateMessage(Long screeningId, List<SeatUpdate> seats) {

   
    public record SeatUpdate(Long seatId, SeatStatus status) {
    }
    
    public static SeatUpdateMessage of(Long screeningId, List<Long> seatIds, SeatStatus status){

        List<SeatUpdate> seats = seatIds.stream()
            .map(seatId -> new SeatUpdate(seatId, status))
            .toList();
        return new SeatUpdateMessage(screeningId, seats);
    }


}
