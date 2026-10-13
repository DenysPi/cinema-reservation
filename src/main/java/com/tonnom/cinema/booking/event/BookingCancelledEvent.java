package com.tonnom.cinema.booking.event;

import java.util.List;

public record BookingCancelledEvent(Long screeningId, List<Long> seatIds) {
    
}
