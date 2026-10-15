package com.tonnom.cinema.booking.event;

import java.util.List;

public record SeatReleaseEvent(Long screeningId, List<Long> seatIds) {
    
}
