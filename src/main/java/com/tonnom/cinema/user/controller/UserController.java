package com.tonnom.cinema.user.controller;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tonnom.cinema.user.dto.UserResponse;
import com.tonnom.cinema.user.service.UserService;

import lombok.RequiredArgsConstructor;


@RestController 
@RequestMapping("/users")
@RequiredArgsConstructor 
public class UserController {
    
    private final UserService userService;

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal Jwt jwt){
        Long userId = Long.valueOf(jwt.getSubject());
        return userService.getById(userId);
    }
}
