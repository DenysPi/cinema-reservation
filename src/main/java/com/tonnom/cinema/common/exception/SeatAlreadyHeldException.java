package com.tonnom.cinema.common.exception;

public class SeatAlreadyHeldException extends RuntimeException{
    public SeatAlreadyHeldException(String message) {
        super(message);
    }
}
