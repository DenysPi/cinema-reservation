package com.tonnom.cinema.config;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tonnom.cinema.hall.model.Hall;
import com.tonnom.cinema.hall.model.Seat;
import com.tonnom.cinema.hall.repository.HallRepository;
import com.tonnom.cinema.hall.repository.SeatRepository;
import com.tonnom.cinema.movie.model.Movie;
import com.tonnom.cinema.movie.repository.MovieRepository;

import com.tonnom.cinema.screening.model.Screening;
import com.tonnom.cinema.screening.repository.ScreeningRepository;
import com.tonnom.cinema.user.model.User;
import com.tonnom.cinema.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component 
@RequiredArgsConstructor 
@Slf4j 
public class DataSeeder implements CommandLineRunner {
    private final MovieRepository movieRepository;
    private final ScreeningRepository screeningRepository;
    private final UserRepository userRepository;
    private final HallRepository hallRepository;
    private final SeatRepository seatRepository;



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

        Hall hall = hallRepository.save(Hall.builder()
                .name("Salle 1")
                .rowCount(10)
                .seatsPerRow(12)
                .build());
        

        List<Seat> seats = new ArrayList<>();
        for (int row = 0; row < hall.getRowCount(); row++) {
            String label = String.valueOf((char) ('A' + row));
            for (int number = 1; number <= hall.getSeatsPerRow(); number++) {
                seats.add(Seat.builder()
                        .hall(hall)
                        .rowLabel(label)
                        .seatNumber(number)
                        .build());
            }
        }

        seatRepository.saveAll(seats);


        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1)
                .withMinute(0).withSecond(0).withNano(0);
        
        screeningRepository.save(Screening.builder()
                .movie(dune)
                .hall(hall)
                .startTime(tomorrow.withHour(14))
                .price(9)
                .build());

        screeningRepository.save(Screening.builder()
                .movie(inception)
                .hall(hall)
                .startTime(tomorrow.withHour(17).withMinute(30))
                .price(10)
                .build());

        screeningRepository.save(Screening.builder()
                .movie(spiderMan)
                .hall(hall)
                .startTime(tomorrow.withHour(21))
                .price(11)
                .build());

        List<User> users = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
                users.add(User.builder()
                        .firstName("User" + i)
                        .build());
        }
        userRepository.saveAll(users);
        log.info("Data inserted");
    }
}
