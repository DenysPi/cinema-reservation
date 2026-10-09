package com.tonnom.cinema;

import org.springframework.boot.SpringApplication;

public class TestCinemaReservationApplication {

	public static void main(String[] args) {
		SpringApplication.from(CinemaReservationApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
