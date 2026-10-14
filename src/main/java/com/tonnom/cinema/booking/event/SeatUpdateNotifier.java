package com.tonnom.cinema.booking.event;



import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tonnom.cinema.booking.dto.SeatUpdateMessage;
import com.tonnom.cinema.booking.dto.SeatUpdateMessage.SeatUpdate;
import com.tonnom.cinema.screening.dto.SeatStatus;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class SeatUpdateNotifier {
    
    private final SimpMessagingTemplate messaging;

    private void send(SeatUpdateMessage updateMessage){
        messaging.convertAndSend("/topic/screenings/"+updateMessage.screeningId()+"/seats",
            updateMessage
        );
    }

    @TransactionalEventListener (phase=TransactionPhase.AFTER_COMMIT)
    public void onBookingConfirmed(BookingConfirmedEvent event){
 
        send(SeatUpdateMessage.of(event.screeningId(), event.seatIds(), SeatStatus.BOOKED));

       
    }

    @EventListener 
    public void onSeatHeld(SeatHeldEvent event){
        send(SeatUpdateMessage.of(event.screeningId(), List.of(event.seatId()), SeatStatus.HELD));
    }

    
}
