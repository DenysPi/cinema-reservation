package com.tonnom.cinema.booking.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tonnom.cinema.booking.model.BookingSeat;
import com.tonnom.cinema.booking.model.Booking;
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long>{

    @Query("SELECT bs.seat.id FROM BookingSeat AS bs WHERE bs.screening.id = :screeningId")
    Set<Long> findTakenSeats(@Param("screeningId") Long screeningId);

    

    List<BookingSeat> findByBookingId(Long bookingId);
    Boolean existsByScreeningIdAndSeatId(Long screeningId, Long SeatId);
    void deleteByBooking(Booking booking);
}
