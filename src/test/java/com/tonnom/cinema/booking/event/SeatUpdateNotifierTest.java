package com.tonnom.cinema.booking.event;

import static org.assertj.core.api.Assertions.assertThat;


import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;

import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import tools.jackson.databind.ObjectMapper;

import com.tonnom.cinema.IntegrationTest;
import com.tonnom.cinema.booking.dto.BookingResponse;
import com.tonnom.cinema.booking.dto.CreateBookingRequest;
import com.tonnom.cinema.booking.dto.SeatUpdateMessage;
import com.tonnom.cinema.booking.dto.SeatUpdateMessage.SeatUpdate;
import com.tonnom.cinema.booking.model.Booking;
import com.tonnom.cinema.booking.repository.BookingRepository;
import com.tonnom.cinema.booking.service.BookingService;
import com.tonnom.cinema.booking.service.SeatAvaibilityService;
import com.tonnom.cinema.screening.dto.SeatStatus;


public class SeatUpdateNotifierTest extends IntegrationTest {
    
    @LocalServerPort int port;
    @Autowired SeatAvaibilityService seatAvaibilityService;
    @Autowired BookingService bookingService;
    @Autowired ObjectMapper objectMapper;

    private StompSession session;
    private final BlockingQueue<SeatUpdateMessage> received = new LinkedBlockingDeque<>();

    @BeforeEach 
    void connectAndSubscribe() throws Exception{
        WebSocketStompClient client = new WebSocketStompClient(new StandardWebSocketClient());
        client.setMessageConverter(new JacksonJsonMessageConverter());

        session = client.connectAsync("ws://localhost:"+port+"/ws",new StompSessionHandlerAdapter() {
            @Override
            public void handleException(StompSession s, StompCommand command, StompHeaders headers,
                                byte[] payload, Throwable exception) {
            exception.printStackTrace();
            }

            @Override
            public void handleTransportError(StompSession s, Throwable exception) {
            exception.printStackTrace();
            }
        }).get(5, TimeUnit.SECONDS);
        
        subscribe("/topic/screenings/" + SCREENING+"/seats", received);

        Thread.sleep(300);
    }


    @AfterEach 
    public void disconnect(){
        if(session != null) session.disconnect();
    }

    private void subscribe(String topic, BlockingQueue<SeatUpdateMessage> queue){
        session.subscribe(topic, new StompFrameHandler() {

            @Override 
            public Type getPayloadType(StompHeaders header){
                return SeatUpdateMessage.class;
            }

            @Override 
            public void handleFrame(StompHeaders headers, Object payload){
                queue.offer((SeatUpdateMessage)payload);
            }
        });
    }

    @Test 
    public void holdingSeatBroadcastsHeld() throws Exception{

        seatAvaibilityService.hold(SCREENING, 1L, USER1);

        SeatUpdateMessage update = received.poll(5, TimeUnit.SECONDS);


        assertThat(update).isNotNull();
        assertThat(update.screeningId()).isEqualTo(SCREENING);
        assertThat(update.seats()).containsExactly(new SeatUpdateMessage.SeatUpdate(1L, SeatStatus.HELD));
    }



    @Test 
    public void releasingSeatBroadcastsFree() throws Exception{

        seatAvaibilityService.hold(SCREENING, 1L, USER1);
  
        assertThat(received.poll(5, TimeUnit.SECONDS)).isNotNull();
        
        seatAvaibilityService.releaseSeat(SCREENING, 1L, USER1);

        SeatUpdateMessage update = received.poll(5, TimeUnit.SECONDS);
        assertThat(update).isNotNull();
        assertThat(update.screeningId()).isEqualTo(SCREENING);
        assertThat(update.seats()).containsExactly(new SeatUpdateMessage.SeatUpdate(1L, SeatStatus.FREE));

    }


    @Test
    public void bookingSeatBroadcastsBookedForEverySeat() throws Exception{
    
        for (long seatId = 1L; seatId <= 3L; seatId++) {
            seatAvaibilityService.hold(SCREENING, seatId, USER1);
            assertThat(received.poll(5, TimeUnit.SECONDS)).isNotNull();
        }

        bookingService.create(new CreateBookingRequest(USER1, SCREENING,List.of(1L,2L,3L)));

        SeatUpdateMessage update = received.poll(5, TimeUnit.SECONDS);

        assertThat(update).isNotNull();
        assertThat(update.screeningId()).isEqualTo(SCREENING);
        assertThat(update.seats()).containsExactlyInAnyOrder(
            new SeatUpdate(1L, SeatStatus.BOOKED),
            new SeatUpdate(2L, SeatStatus.BOOKED),
            new SeatUpdate(3L, SeatStatus.BOOKED));
    
    }

    @Test 
    public void cancellingSeatBroadcastsFreeForEverySeat() throws Exception{
        for (long seatId = 1L; seatId <= 3L; seatId++) {
            seatAvaibilityService.hold(SCREENING, seatId, USER1);
            assertThat(received.poll(5, TimeUnit.SECONDS)).isNotNull();
        }

        BookingResponse booking = bookingService.create(new CreateBookingRequest(USER1, SCREENING,List.of(1L,2L,3L)));

        assertThat(received.poll(5, TimeUnit.SECONDS)).isNotNull();

        bookingService.cancel(booking.reference());

        SeatUpdateMessage update = received.poll(5, TimeUnit.SECONDS);

        assertThat(update).isNotNull();
        assertThat(update.screeningId()).isEqualTo(SCREENING);
        assertThat(update.seats()).containsExactlyInAnyOrder(
            new SeatUpdate(1L, SeatStatus.FREE),
            new SeatUpdate(2L, SeatStatus.FREE),
            new SeatUpdate(3L, SeatStatus.FREE));
        
    }

    @Test 
    public void subscriberOfAnotherScreeningReceivesNothing() throws Exception{
        BlockingQueue<SeatUpdateMessage> anotherScreening = new LinkedBlockingQueue<>();

        subscribe("/topic/screenings/" + 2L + "/seats", anotherScreening);

        seatAvaibilityService.hold(SCREENING, 1L, USER1);

        assertThat(received.poll(5, TimeUnit.SECONDS)).isNotNull();
        assertThat(anotherScreening.poll(5, TimeUnit.SECONDS)).isNull();
    }
}
