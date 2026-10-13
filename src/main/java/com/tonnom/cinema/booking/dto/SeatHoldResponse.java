package com.tonnom.cinema.booking.dto;

import java.time.LocalDateTime;

import lombok.Builder;


@Builder 
public record SeatHoldResponse (Long screeningId, Long seatId, LocalDateTime expiresAt){
    
}
