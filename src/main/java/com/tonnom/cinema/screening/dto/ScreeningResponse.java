package com.tonnom.cinema.screening.dto;


import java.time.LocalDateTime;

import com.tonnom.cinema.screening.model.Screening;

import lombok.Builder;

@Builder 
public record ScreeningResponse(Long screeningId, LocalDateTime startTime, int price) {
    
    public static ScreeningResponse from(Screening screening){
        return ScreeningResponse.builder()
                                    .screeningId(screening.getId())
                                    .startTime(screening.getStartTime())
                                    .price(screening.getPrice())
                                    .build();
    }
}
