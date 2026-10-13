package com.tonnom.cinema.screening.dto;


import java.time.LocalDateTime;

import com.tonnom.cinema.screening.model.Screening;

import lombok.Builder;

@Builder 
public record ScreeningResponse(Long screeningId, LocalDateTime startTime, int price, MovieSum movie, HallSum hall) {
    
    
    public record MovieSum(Long movieId, String title, Integer duration){}
    public record HallSum(Long hallId, String name, Integer rowCount, Integer seatsPerRow){}

    public static ScreeningResponse from(Screening screening){

        return ScreeningResponse.builder()
                                    .screeningId(screening.getId())
                                    .startTime(screening.getStartTime())
                                    .price(screening.getPrice())
                                    .movie(new MovieSum(screening.getMovie().getId(), screening.getMovie().getTitle(), screening.getMovie().getDuration()))
                                    .hall(new HallSum(screening.getHall().getId(), screening.getHall().getName(), screening.getHall().getRowCount(), screening.getHall().getSeatsPerRow()))
                                    .build();
    }
}
