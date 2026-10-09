package com.tonnom.cinema.movie.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tonnom.cinema.movie.model.Movie;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    
    
} 
