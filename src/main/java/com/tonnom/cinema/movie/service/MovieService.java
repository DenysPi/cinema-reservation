package com.tonnom.cinema.movie.service;

import java.util.List;

import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

import com.tonnom.cinema.movie.repository.MovieRepository;
import com.tonnom.cinema.movie.dto.MovieResponse;


@Service 
@RequiredArgsConstructor 
public class MovieService {
    
    private final MovieRepository movieRepository;

    public List<MovieResponse> getAll(){
        return movieRepository.findAll()
                                .stream()
                                .map(MovieResponse::from)
                                .toList();

    }

    public MovieResponse getById(Long id){
        return movieRepository.findById(id)
                                .map(MovieResponse::from)
                                .orElseThrow();
    }

}
