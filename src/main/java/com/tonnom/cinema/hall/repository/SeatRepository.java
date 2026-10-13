package com.tonnom.cinema.hall.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tonnom.cinema.hall.model.Seat;

public interface SeatRepository extends JpaRepository<Seat, Long>{
    List<Seat> findByHallIdOrderByRowLabelAscSeatNumberAsc(Long hallId);
    List<Seat> findByHallId(Long hallId);
}
