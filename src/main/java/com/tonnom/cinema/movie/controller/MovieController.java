package com.tonnom.cinema.movie.controller;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.tonnom.cinema.movie.dto.MovieResponse;
import com.tonnom.cinema.movie.service.MovieService;

@RestController 
@RequestMapping("/movies")
@RequiredArgsConstructor 
@Slf4j 
public class MovieController {
    private final MovieService movieService;

    @GetMapping
    public List<MovieResponse> getAll(){
        log.info("Getting all movies");
        return movieService.getAll();
    }

    @GetMapping("/{id}")
    public  MovieResponse getOne(@PathVariable Long id){
        return movieService.getById(id);
    }
}