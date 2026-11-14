package com.tonnom.cinema.auth.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.tonnom.cinema.auth.dto.LoginRequest;
import com.tonnom.cinema.auth.dto.RegisterRequest;
import com.tonnom.cinema.auth.dto.TokenResponse;
import com.tonnom.cinema.auth.security.UserPrincipal;
import com.tonnom.cinema.user.model.User;
import com.tonnom.cinema.user.repository.UserRepository;


import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    public void register(RegisterRequest request){
        
        String email = request.email().trim().toLowerCase();


        User user = User.builder()
                    .firstName(request.firstName())
                    .email(email)
                    .password(passwordEncoder.encode(request.password()))
                    
                    .build();

        userRepository.save(user);
    }

    public TokenResponse login(LoginRequest request){
        Authentication auth = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password()));

        UserPrincipal user = (UserPrincipal) auth.getPrincipal();


        return TokenResponse.builder()
                        .accesToken(tokenService.createAccessToken(user))
                        .build();
    } 
}
