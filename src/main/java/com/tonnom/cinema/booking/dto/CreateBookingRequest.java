package com.tonnom.cinema.booking.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record CreateBookingRequest(
        @NotNull Long userId,
        @NotNull Long screeningId,
        @NotEmpty List<Long> seatIds
) {}