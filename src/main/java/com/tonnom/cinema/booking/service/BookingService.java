package com.tonnom.cinema.booking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import com.tonnom.cinema.booking.dto.BookingResponse;
import com.tonnom.cinema.booking.dto.CreateBookingRequest;
import com.tonnom.cinema.booking.event.BookingCancelledEvent;
import com.tonnom.cinema.booking.event.BookingConfirmedEvent;
import com.tonnom.cinema.booking.model.Booking;
import com.tonnom.cinema.booking.model.BookingSeat;
import com.tonnom.cinema.booking.model.BookingStatus;
import com.tonnom.cinema.booking.repository.BookingRepository;
import com.tonnom.cinema.booking.repository.BookingSeatRepository;
import com.tonnom.cinema.hall.model.Seat;
import com.tonnom.cinema.hall.repository.SeatRepository;
import com.tonnom.cinema.screening.model.Screening;
import com.tonnom.cinema.screening.repository.ScreeningRepository;
import com.tonnom.cinema.user.repository.UserRepository;

import com.tonnom.cinema.common.exception.NotFoundException;

import com.tonnom.cinema.common.exception.SeatNotHeldException;

import org.springframework.transaction.annotation.Transactional;

import com.tonnom.cinema.user.model.User;
@Service 
@RequiredArgsConstructor 
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    
    private final SeatAvaibilityService seatAvaibilityService;

    private final ApplicationEventPublisher eventPublisher;
    
    @Transactional 
    public BookingResponse create(CreateBookingRequest request){
        List<Long> seatsIds = request.seatIds();

        if(!seatAvaibilityService.areHeldBy(request.screeningId(), seatsIds, request.userId())){
            throw new SeatNotHeldException("Your hold is missing or has expired");
        }
        User user = userRepository.findById(request.userId()).orElseThrow(() -> new NotFoundException("User not found with id: " + request.userId()));

        Screening screening = screeningRepository.findById(request.screeningId()).orElseThrow(() -> new NotFoundException("Screening not found with id: " + request.screeningId()));
        
        List<Seat> seats = seatRepository.findAllById(seatsIds);

        if(seats.size() != seatsIds.size()){
            throw new NotFoundException("One or more seats do not exist");
        }

        LocalDateTime now = LocalDateTime.now();
        Booking booking = bookingRepository.save(Booking.builder()
            .user(user)
            .screening(screening)
            .bookingStatus(BookingStatus.CONFIRMED)
            .reference(UUID.randomUUID().toString())
            .createdAt(now)
            .build());
        
        List<BookingSeat> bookingSeats = new ArrayList<>();
        
        for(Seat seat: seats){
            bookingSeats.add(BookingSeat.builder()
                                            .booking(booking)
                                            .seat(seat)
                                            .screening(screening)
                                            .build());
        }
        

        bookingSeatRepository.saveAllAndFlush(bookingSeats);
        

        eventPublisher.publishEvent(new BookingConfirmedEvent(request.screeningId(), seatsIds, request.screeningId(), screening.getStartTime()));
        
        return BookingResponse.builder()
            .bookingId(booking.getId())
            .reference(booking.getReference())
            .screeningId(screening.getId())
            .seatIds(request.seatIds())
            .status(booking.getBookingStatus())
            .createdAt(booking.getCreatedAt())
            .build();
    }

    public List<BookingResponse> getByUser(Long userId){
        List<BookingResponse> result = new ArrayList<>();
        for(Booking booking :bookingRepository.findByUserId(userId)){
            List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());
            result.add(BookingResponse.from(booking, bookingSeats));
        }

        return result;
    }
    
    public BookingResponse getByReference(String reference){
        Booking booking = bookingRepository.findByReference(reference).orElseThrow(() -> new NotFoundException("Booking not found: "+ reference));
        List<BookingSeat> bookingSeats = bookingSeatRepository.findByBookingId(booking.getId());
        return BookingResponse.from(booking, bookingSeats);
        
    }

    @Transactional 
    public void cancel(String reference){
        Booking booking = bookingRepository.findByReference(reference).orElseThrow(() -> new NotFoundException("Booking not found: "+ reference));
        
        List<Long> seatIds =bookingSeatRepository.findByBookingId(booking.getId()).stream()
            .map(bookingSeat -> bookingSeat.getSeat().getId())
            .toList();

        Long screeningId = booking.getScreening().getId();

        bookingSeatRepository.deleteByBooking(booking);
        bookingRepository.delete(booking);


        eventPublisher.publishEvent(new BookingCancelledEvent(screeningId, seatIds));
    }
}
