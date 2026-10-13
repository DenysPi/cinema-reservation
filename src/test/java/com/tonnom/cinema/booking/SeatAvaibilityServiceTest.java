package com.tonnom.cinema.booking;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.tonnom.cinema.IntegrationTest;
import com.tonnom.cinema.booking.dto.BookingResponse;
import com.tonnom.cinema.booking.dto.CreateBookingRequest;

import com.tonnom.cinema.booking.service.BookingService;
import com.tonnom.cinema.booking.service.SeatAvaibilityService;
import com.tonnom.cinema.common.exception.SeatAlreadyHeldException;
import com.tonnom.cinema.common.exception.SeatNotHeldException;

public class SeatAvaibilityServiceTest extends IntegrationTest{
    
    private static final Long SCREENING = 1L;
    private static final Long USER1 = 1L;
    private static final Long USER2 = 2L;

    @Autowired 
    private SeatAvaibilityService seatAvaibilityService;

    @Autowired  
    private  BookingService bookingService;

    

    private String key (Long seatId){
        return "seat:"+SCREENING+":"+seatId;
    }

    private String userKey(Long userId){
        return "holds:"+SCREENING+":user:"+userId;
    }

    @Test 
    void holdingFreeSeatStoresTheUserWithAnExpiry(){
        seatAvaibilityService.hold(SCREENING, 5L, USER1);
        
        assertThat(redis.opsForValue().get(key(5L))).isEqualTo(USER1.toString());
        assertThat(redis.getExpire(key(5L))).isBetween(1L, 300L);
        assertThat(redis.opsForSet().members(userKey(USER1))).containsExactlyInAnyOrder("5");


    }

    @Test 
    void anotherUserCannotHoldAlreadyTakenSeat(){
        seatAvaibilityService.hold(SCREENING, 1L, USER1);

        assertThatThrownBy(() -> seatAvaibilityService.hold(SCREENING, 1L, USER2)).isInstanceOf(SeatAlreadyHeldException.class);
        assertThat(redis.opsForValue().get(key(1L))).isEqualTo(USER1.toString());
    }

    @Test 
    void userCannotBookWithoutHoldingSeat(){
        CreateBookingRequest request = new CreateBookingRequest(USER1, SCREENING, List.of(1L));
        assertThatThrownBy(() -> bookingService.create(request)).isInstanceOf(SeatNotHeldException.class);

        assertThat(bookingService.getByUser(USER1)).isEmpty();
        
    }

    @Test
    void bookingTwoOfThreeHeldSeatsReleasesTheThird(){

        seatAvaibilityService.hold(SCREENING, 1L, USER1);
        seatAvaibilityService.hold(SCREENING, 2L, USER1);
        seatAvaibilityService.hold(SCREENING, 3L, USER1);

        CreateBookingRequest request = new CreateBookingRequest(USER1, SCREENING, List.of(1L,2L));
        bookingService.create(request);

        List<BookingResponse> bookings = bookingService.getByUser(USER1);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).seatIds()).containsExactlyInAnyOrder(1L, 2L);

        assertThat(redis.opsForValue().get(key(1L))).isEqualTo("booked");
        assertThat(redis.opsForValue().get(key(2L))).isEqualTo("booked");
        assertThat(redis.opsForValue().get(key(3L))).isNull();
        assertThat(redis.opsForSet().members(userKey(USER1))).isEmpty();

        seatAvaibilityService.hold(SCREENING, 3L, USER2);

        assertThat(redis.opsForValue().get(key(3L))).isEqualTo(USER2.toString());

       
    }

    @Test 
    public void holdABookedSeat(){
        seatAvaibilityService.hold(SCREENING, 1L, USER1);
        

        CreateBookingRequest request = new CreateBookingRequest(USER1, SCREENING, List.of(1L));
        bookingService.create(request);

        assertThatThrownBy(() ->seatAvaibilityService.hold(SCREENING, 1L, USER1)).isInstanceOf(SeatAlreadyHeldException.class);

    }

    public void cancelBooking(){
        seatAvaibilityService.hold(SCREENING, 1L, USER1);

        CreateBookingRequest request = new CreateBookingRequest(USER1, SCREENING, List.of(1L));
        BookingResponse response = bookingService.create(request);

        bookingService.cancel(response.reference());

        assertThat(bookingService.getByUser(USER1)).isEmpty();
        assertThat(redis.opsForValue().get(key(1L))).isNull();

        seatAvaibilityService.hold(SCREENING, 1L, USER2);

    }

    @Test 
    void OnlyOneUserCanHoldTheSameSeat() throws Exception{
        
        int users = 30;
        ExecutorService pool = Executors.newFixedThreadPool(users);
        CountDownLatch start = new CountDownLatch(1);

        AtomicInteger succeses = new AtomicInteger(0);
        List<Future<?>> futures = new ArrayList<>();

        for(long userId = 1; userId<=users; userId++){
            long id =userId;
            futures.add(pool.submit(()->{
                start.await();
                try{
                    seatAvaibilityService.hold(SCREENING, 2L, id);
                    succeses.incrementAndGet();
                }catch(SeatAlreadyHeldException ignored){}
                return null;
            }));
            
        }

        start.countDown();
        for(Future<?> f: futures) f.get();
        pool.shutdown();

        assertThat(succeses.get()).isEqualTo(1);



    }
}
