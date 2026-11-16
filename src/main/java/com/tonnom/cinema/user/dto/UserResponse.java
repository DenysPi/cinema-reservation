package com.tonnom.cinema.user.dto;



import com.tonnom.cinema.user.model.Role;

import lombok.Builder;


@Builder 
public record UserResponse(
    Long id, String firstName, String email, Role role
) {
    
}
