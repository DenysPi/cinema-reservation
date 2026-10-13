package com.tonnom.cinema.booking.event;

import java.time.LocalDateTime;
import java.util.List;

public record BookingConfirmedEvent(Long screeningId, List<Long> seatIds, Long userId, LocalDateTime startTime) {
}  
