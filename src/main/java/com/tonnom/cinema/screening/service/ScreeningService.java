package com.tonnom.cinema.screening.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tonnom.cinema.screening.dto.ScreeningResponse;
import com.tonnom.cinema.screening.model.Screening;
import com.tonnom.cinema.screening.repository.ScreeningRepository;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class ScreeningService {
    
    private final ScreeningRepository screeningRepository;

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
}