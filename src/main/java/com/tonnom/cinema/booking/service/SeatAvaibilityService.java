package com.tonnom.cinema.booking.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tonnom.cinema.booking.dto.SeatHoldResponse;
import com.tonnom.cinema.booking.repository.BookingSeatRepository;
import com.tonnom.cinema.common.exception.SeatAlreadyHeldException;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class SeatAvaibilityService {


    private static final Duration HOLD_DURATION = Duration.ofMinutes(5);
    private static final String BOOKED = "booked";


    private final StringRedisTemplate redis;
    private final BookingSeatRepository bookingSeatRepository;



    public SeatHoldResponse hold(Long screeningId, Long seatId, Long userId){

        ensureNotBooked(screeningId, seatId);
        takeSeat(screeningId, seatId, userId);
        trackUserHold(screeningId, seatId, userId);

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

    public void release(Long screeningId, List<Long> seatIds, Long userId) {
        if (seatIds == null || seatIds.isEmpty()) {
            return;
        }

        List<String> keys = seatKeys(screeningId, seatIds);
        
        List<String> values = redis.opsForValue().multiGet(keys);
        String expected = userId.toString();

        
        if (values != null) {
            List<String> owned = new ArrayList<>();
            for (int i = 0; i < keys.size(); i++) {
                if (expected.equals(values.get(i))) {
                    owned.add(keys.get(i));
                }
            }
            if (!owned.isEmpty()) {
                redis.delete(owned);
            }
        }

        
        redis.opsForSet().remove(
                userKey(screeningId, userId),
                seatIds.stream().map(String::valueOf).toArray());
    }

    public void releaseAll(Long screeningId, Long userId) {
        String userKey = userKey(screeningId, userId);
        Set<String> members = redis.opsForSet().members(userKey);

        if (members != null && !members.isEmpty()) {
            List<Long> seatIds = members.stream()
                    .map(Long::valueOf)
                    .toList();
            release(screeningId, seatIds, userId);
        }

        redis.delete(userKey);
    }

    public void markBooked(Long screeningId, List<Long> seatIds, Long userId, LocalDateTime screeningStart){
        Duration ttl = Duration.between(LocalDateTime.now(), screeningStart);

        for(String key: seatKeys(screeningId, seatIds)){
            redis.opsForValue().set(key, BOOKED, ttl);
        }

        releaseAll(screeningId, userId);

    }

    public void markFree(Long screeningId, List<Long> seatIds){
        
        redis.delete(seatKeys(screeningId, seatIds));
    
    }
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
