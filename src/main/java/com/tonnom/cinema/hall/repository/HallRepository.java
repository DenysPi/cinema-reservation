package com.tonnom.cinema.hall.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tonnom.cinema.hall.model.Hall;

public interface HallRepository extends JpaRepository<Hall, Long>{
    
    
} 
