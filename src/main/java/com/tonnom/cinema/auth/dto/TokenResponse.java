package com.tonnom.cinema.auth.dto;

import lombok.Builder;

@Builder 
public record TokenResponse(String accesToken) {
    
}
