package com.tonnom.cinema.movie.model;

import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Table(name = "movies")
@Data 
@Builder 
@NoArgsConstructor
@AllArgsConstructor 
public class Movie {
    @Id 
    @Column(name="movie_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column (name="title", nullable = false)
    private String title;
    @Column (name="duration", nullable = false)
    private int duration;
}
