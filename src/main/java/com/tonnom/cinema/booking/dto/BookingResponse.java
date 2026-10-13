package com.tonnom.cinema.booking.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tonnom.cinema.booking.model.Booking;
import com.tonnom.cinema.booking.model.BookingSeat;
import com.tonnom.cinema.booking.model.BookingStatus;


import lombok.Builder;


@Builder 
public record BookingResponse(
        Long bookingId,
        Long screeningId,
        String reference,
        List<Long> seatIds,
        BookingStatus status,
        LocalDateTime createdAt
) {

        public static BookingResponse from(Booking booking, List<BookingSeat> bookingSeats){

                return BookingResponse.builder()
                                        .bookingId(booking.getId())
                                        .screeningId(booking.getScreening().getId())
                                        .reference(booking.getReference())
                                        .seatIds(bookingSeats.stream()
                                                .map(bs -> bs.getSeat().getId())
                                                .toList())
                                        .status(booking.getBookingStatus())
                                        .createdAt(booking.getCreatedAt())
                                        .build();

        }
}