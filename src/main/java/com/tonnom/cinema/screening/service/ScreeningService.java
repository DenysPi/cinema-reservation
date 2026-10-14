package com.tonnom.cinema.screening.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.tonnom.cinema.booking.repository.BookingSeatRepository;
import com.tonnom.cinema.common.exception.NotFoundException;
import com.tonnom.cinema.hall.model.Seat;
import com.tonnom.cinema.hall.repository.SeatRepository;
import com.tonnom.cinema.screening.dto.ScreeningResponse;
import com.tonnom.cinema.screening.dto.SeatStatus;
import com.tonnom.cinema.screening.dto.SeatsMapResponse;
import com.tonnom.cinema.screening.dto.SeatsMapResponse.SeatInfo;
import com.tonnom.cinema.screening.model.Screening;
import com.tonnom.cinema.screening.repository.ScreeningRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ScreeningService {
    
    private final ScreeningRepository screeningRepository;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public List<ScreeningResponse> getUpcoming(Long movieId, LocalDate date){
        LocalDateTime from;
        LocalDateTime to;

        if(date == null){
            from = LocalDateTime.now();
            to = from.plusMonths(1);
        }
        else{
            from = date.atStartOfDay();
            to = date.plusDays(1).atStartOfDay();
        }

        List<Screening> screenings;

        if(movieId == null){
            screenings = screeningRepository.findByStartTimeBetweenOrderByStartTime(from, to);
        }
        else{
            screenings = screeningRepository.findByMovieIdAndStartTimeBetweenOrderByStartTime(movieId, from, to);
        }
        return screenings.stream().map(ScreeningResponse::from).toList();
    }

    public ScreeningResponse getById(Long id){
        return screeningRepository.findById(id)
                                    .map(ScreeningResponse::from)
                                    .orElseThrow();
    }

    public SeatsMapResponse getSeatsMap(Long screeningId){

        Screening screening = screeningRepository.findById(screeningId).orElseThrow(() -> new NotFoundException("The screeing not found with id: "+ screeningId));

        List<Seat> seats = seatRepository.findByHallIdOrderByRowLabelAscSeatNumberAsc(screening.getHall().getId());

        Set<Long> takenSeatsIds = bookingSeatRepository.findTakenSeats(screeningId);

        List<SeatInfo> result = new ArrayList<>();

        for(Seat seat: seats){
            SeatStatus status = takenSeatsIds.contains(seat.getId()) ? SeatStatus.BOOKED : SeatStatus.FREE;
            result.add(SeatInfo.builder()
                                    .seatId(seat.getId())
                                    .rowLabel(seat.getRowLabel())
                                    .seatNumber(seat.getSeatNumber())
                                    .status(status)
                                    .build());
        }

        return new SeatsMapResponse(screeningId, result);
    }
}