package com.tonnom.cinema.movie.dto;

import com.tonnom.cinema.movie.model.Movie;

import lombok.Builder;


@Builder 
public record MovieResponse(Long movieId, String title, int duration){

    public static MovieResponse from(Movie movie){
        return MovieResponse.builder()
                                .movieId(movie.getId())
                                .title(movie.getTitle())
                                .duration(movie.getDuration())
                                .build();
    }
}