package com.tonnom.cinema.booking.event;

public record SeatHeldEvent(Long screeningId, Long seatId) {
    
}
