package com.tonnom.cinema.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tonnom.cinema.movie.model.Movie;
import com.tonnom.cinema.movie.repository.MovieRepository;

import com.tonnom.cinema.screening.model.Screening;
import com.tonnom.cinema.screening.repository.ScreeningRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@RequiredArgsConstructor 
@Slf4j 
public class DataSeeder implements CommandLineRunner {
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;



    @Override 
    public void run(String... args){
        if(movieRepository.count()>0){
            return ;
        }

        Movie dune = movieRepository.save(
            Movie.builder()
                    .title("Dune")
                    .duration(155)
                    .build());
        Movie inception = movieRepository.save(
            Movie.builder()
                    .title("Inception")
                    .duration(55)
                    .build());
        
        Movie spiderMan = movieRepository.save(
            Movie.builder()
                    .title("Spider Man 1")
                    .duration(125)
                    .build());

        
        Screening screening = Screening.builder()
                                        .movie(spiderMan)
                                        .startTime(LocalDateTime.now().plusDays(1).withHour(20).withMinute(0))
                                        .price(15)
                                        .build();

        screeningRepository.save(screening);

        
        log.info("Data inserted");
    }
}
