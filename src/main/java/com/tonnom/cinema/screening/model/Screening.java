package com.tonnom.cinema.screening.model;

import java.time.LocalDateTime;

import com.tonnom.cinema.movie.model.Movie;
import com.tonnom.cinema.hall.model.Hall;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "screenings")
@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class Screening {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="screening_id", nullable = false, unique = true)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id")
    private Movie movie;


    @ManyToOne (fetch = FetchType.LAZY)
    @JoinColumn(name = "hall_id")
    private Hall hall;

    @Column(name="start_time")
    LocalDateTime startTime;

    @Column(name = "price")
    
    int price;
}