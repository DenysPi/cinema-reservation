package com.tonnom.cinema.screening.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tonnom.cinema.screening.model.Screening;


public interface ScreeningRepository extends  JpaRepository<Screening, Long>{

    List<Screening> findByStartTimeBetweenOrderByStartTime(LocalDateTime from, LocalDateTime to);

    List<Screening> findByMovieIdAndStartTimeBetweenOrderByStartTime(Long movieId, LocalDateTime now, LocalDateTime to);

} 
