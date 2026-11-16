package com.tonnom.cinema.user.service;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties.Jwt;
import org.springframework.stereotype.Service;

import com.tonnom.cinema.common.exception.NotFoundException;
import com.tonnom.cinema.user.dto.UserResponse;
import com.tonnom.cinema.user.model.User;
import com.tonnom.cinema.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getById(Long userId){

        User user = userRepository.findById(userId).
                orElseThrow(() -> new NotFoundException("User not found with id: " + userId));
        
                return UserResponse.builder()
                                .id(userId)
                                .email(user.getEmail())
                                .firstName(user.getFirstName())
                                .role(user.getRole())
                                .build();


    }
}
