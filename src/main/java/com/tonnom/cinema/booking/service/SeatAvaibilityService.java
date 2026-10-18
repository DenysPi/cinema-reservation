package com.tonnom.cinema.booking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tonnom.cinema.booking.dto.SeatHoldResponse;
import com.tonnom.cinema.booking.event.SeatHeldEvent;
import com.tonnom.cinema.booking.event.SeatReleaseEvent;
import com.tonnom.cinema.booking.repository.BookingSeatRepository;
import com.tonnom.cinema.common.exception.SeatAlreadyBookedException;
import com.tonnom.cinema.common.exception.SeatAlreadyHeldException;
import com.tonnom.cinema.common.exception.SeatNotHeldException;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SeatAvaibilityService {


    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);
    private static final String BOOKED = "booked";


    private final StringRedisTemplate redis;
    private final BookingSeatRepository bookingSeatRepository;

    private final ApplicationEventPublisher eventPublisher;

    public SeatHoldResponse hold(Long screeningId, Long seatId, Long userId){

        ensureNotBooked(screeningId, seatId);
        takeSeat(screeningId, seatId, userId);
        trackUserHold(screeningId, seatId, userId);

        eventPublisher.publishEvent(new SeatHeldEvent(screeningId, seatId));
        return SeatHoldResponse.builder()
                                .screeningId(screeningId)
                                .seatId(seatId)
                                .expiresAt(LocalDateTime.now().plusMinutes(5))
                                .build();
    }

    public boolean areHeldBy(Long screeningId, List<Long> seatIds, Long userId){
        List<String> values = redis.opsForValue().multiGet(seatKeys(screeningId, seatIds));
        String expected = userId.toString();

        return values != null && values.stream().allMatch(expected::equals);
    
    }


    public void releaseSeat(Long screeningId, Long seatId, Long userId) {
        String owner = redis.opsForValue().get(seatKey(screeningId, seatId));

        if (owner == null) return;
        if(BOOKED.equals(owner)) throw new SeatAlreadyBookedException("Seat " + seatId + "is already booked by someone");
        if(!isOwnedBy(owner, userId)) throw new SeatNotHeldException("Seat" + seatId + "is not held by you");
        deleteOwnedHolds(screeningId, List.of(seatId), userId);
    }
    

    public void releaseAllHolds(Long screeningId, Long userId) {
        String userKey = userKey(screeningId, userId);
        Set<String> members = redis.opsForSet().members(userKey);

        if (members != null && !members.isEmpty()) {
            List<Long> seatIds = members.stream()
                    .map(Long::valueOf)
                    .toList();
            deleteOwnedHolds(screeningId, seatIds, userId);
        }

        redis.delete(userKey);
    }

    public void markBooked(Long screeningId, List<Long> seatIds, Long userId, LocalDateTime screeningStart){
        Duration ttl = Duration.between(LocalDateTime.now(), screeningStart.plusDays(1));

        for(String key: seatKeys(screeningId, seatIds)){
            redis.opsForValue().set(key, BOOKED, ttl);
        }

        releaseAllHolds(screeningId, userId);

    }

    public void markFree(Long screeningId, List<Long> seatIds){
        
        redis.delete(seatKeys(screeningId, seatIds));
    
    }


    // HELPERS ::

    private void ensureNotBooked(Long screeningId, Long seatId){
        if (bookingSeatRepository.existsByScreeningIdAndSeatId(screeningId, seatId)) {
            throw new SeatAlreadyHeldException("Seat " + seatId + " is already booked");
        }
    }

    private void takeSeat(Long screeningId, Long seatId, Long userId){
        Boolean created = redis.opsForValue().setIfAbsent(seatKey(screeningId, seatId), userId.toString(), HOLD_DURATION);
        
        if(!Boolean.TRUE.equals(created)){
            throw new SeatAlreadyHeldException("Seat " + seatId + " is already held");
        }
    }

    private void trackUserHold(Long screeningId, Long seatId, Long userId) {
        String userKey = userKey(screeningId, userId);
        redis.opsForSet().add(userKey, seatId.toString());
        redis.expire(userKey, HOLD_DURATION);
    }


    private boolean isOwnedBy(String owner, Long userId){
        return userId.toString().equals(owner);
    }

    private void deleteOwnedHolds(Long screeningId, List<Long> seatIds, Long userId){
        List<String> keys = seatKeys(screeningId, seatIds);
        List<String> values = redis.opsForValue().multiGet(keys);

        List<String> ownedKeys = new ArrayList<>();
        List<Long> freedSeatIds = new ArrayList<>();

        if (values != null){
            for (int i = 0; i < keys.size(); i++){
                if(isOwnedBy(values.get(i), userId)){
                    ownedKeys.add(keys.get(i));
                    freedSeatIds.add(seatIds.get(i));
                }
            }
        }

        if(!ownedKeys.isEmpty()){

            redis.delete(ownedKeys);
            eventPublisher.publishEvent(new SeatReleaseEvent(screeningId, freedSeatIds));
        }

        untrackUserHolds(screeningId, seatIds, userId);
        

    }

    private void untrackUserHolds(Long screeningId, List<Long> seatIds, Long userId){

        redis.opsForSet().remove(userKey(screeningId, userId), seatIds.stream().map(String::valueOf).toArray());
    }
    private List<String> seatKeys (Long screeningId, List<Long> seatIds){
        return seatIds.stream().map(seatId -> seatKey(screeningId, seatId)).toList();
    }
    private String seatKey(Long screeningId, Long seatId) {
        return "seat:" + screeningId + ":" + seatId;
    }

    
    private String userKey(Long screeningId, Long userId) {
        return "holds:" + screeningId + ":user:" + userId;
    }
}
