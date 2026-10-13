package com.tonnom.cinema.booking.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tonnom.cinema.booking.service.SeatAvaibilityService;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class BookingEventListener {
    
    public final SeatAvaibilityService seatAvaibilityService;


    @TransactionalEventListener (phase = TransactionPhase.AFTER_COMMIT)
    public void onConfirmed(BookingConfirmedEvent event){
        seatAvaibilityService.markBooked(event.screeningId(), event.seatIds(), event.userId(), event.startTime());
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCancelled(BookingCancelledEvent event){
        seatAvaibilityService.markFree(event.screeningId(), event.seatIds());
    }
}
