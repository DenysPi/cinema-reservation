package com.tonnom.cinema.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

import com.tonnom.cinema.IntegrationTest;
import com.tonnom.cinema.auth.dto.LoginRequest;
import com.tonnom.cinema.auth.dto.RegisterRequest;
import com.tonnom.cinema.auth.dto.TokenResponse;
import com.tonnom.cinema.user.model.Role;
import com.tonnom.cinema.user.model.User;
import com.tonnom.cinema.user.repository.UserRepository;

public class AuthServiceTest extends IntegrationTest {
    
    @Autowired AuthService authService;
    @Autowired UserRepository userRepository;

    @Autowired JwtDecoder jwtDecoder;

    private String uniqueEmail(){
        return "test"+UUID.randomUUID()+"@gmail.com";
    }
    @Test 
    void loginWithCorrectCredentialsRetursToken(){

        User user1 = userRepository.findByEmail("user1@gmail.com").orElseThrow();

        TokenResponse response = authService.login(new LoginRequest("user1@gmail.com", "password_1234"));

        assertThat(response.accessToken()).isNotBlank();

        Jwt jwt = jwtDecoder.decode(response.accessToken());

        assertThat(jwt.getSubject()).isEqualTo(user1.getId().toString());
        assertThat(jwt.getClaimAsStringList("roles")).containsExactly("USER");
    }

    @Test 
    void loginWithWrongPasswordIsRefused(){
        assertThatThrownBy(() -> authService.login(new LoginRequest("user1@gmail.com", "wrong_password")))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test 
    void loginWithUnknownEmailIsRefused(){
        assertThatThrownBy(() -> authService.login(new LoginRequest("random@gmail.com", "password_1234")))
            .isInstanceOf(BadCredentialsException.class);
    }


    @Test 
    void registerSavesNewUser(){
        String email = uniqueEmail();

        authService.register(new RegisterRequest(email, "password_1234", "Web"));

        User user = userRepository.findByEmail(email).orElseThrow();

        assertThat(user.getFirstName()).isEqualTo("Web");
    }

    @Test 
    void registerWithExistingEmailIsRefused(){
        assertThatThrownBy(() -> authService.register(new RegisterRequest("user1@gmail.com", "strong", "Web")))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test 
    void registerGivesUserRole(){

        String email = uniqueEmail();

        authService.register(new RegisterRequest(email, "strong", "Web"));

        User user = userRepository.findByEmail(email).orElseThrow(); 

        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

}
