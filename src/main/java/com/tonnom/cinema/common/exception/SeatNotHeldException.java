package com.tonnom.cinema.common.exception;

public class SeatNotHeldException extends RuntimeException{
    public SeatNotHeldException(String message){
        super(message);
    }
}
