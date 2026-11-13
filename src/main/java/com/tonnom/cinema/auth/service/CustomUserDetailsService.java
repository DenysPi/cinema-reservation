package com.tonnom.cinema.auth.service;



import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tonnom.cinema.user.model.User;
import com.tonnom.cinema.user.repository.UserRepository;
import com.tonnom.cinema.auth.security.UserPrincipal;
import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor 
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override 
    public UserDetails loadUserByUsername(String email){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Username not Found"));
        return UserPrincipal.create(user);
    }
}
