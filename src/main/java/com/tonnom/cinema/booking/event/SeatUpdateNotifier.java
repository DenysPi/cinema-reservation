package com.tonnom.cinema.booking.event;



import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tonnom.cinema.booking.dto.SeatUpdateMessage;

import com.tonnom.cinema.screening.dto.SeatStatus;

import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class SeatUpdateNotifier {
    
    private final SimpMessagingTemplate messaging;



    @TransactionalEventListener (phase=TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedEvent event){

        


        messaging.convertAndSend(
            "/topic/screenings/"+ event.screeningId()+"/seats",
            SeatUpdateMessage.of(event.screeningId(), event.seatIds(), SeatStatus.BOOKED)

        );
    }
}
