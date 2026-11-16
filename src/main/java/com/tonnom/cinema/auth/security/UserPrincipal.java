package com.tonnom.cinema.auth.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.tonnom.cinema.user.model.Role;
import com.tonnom.cinema.user.model.User;

import lombok.Data;


@Data
public class UserPrincipal implements UserDetails{

    private final Long id;
    private final String password;
    private final String email;
    private final Role role;


    public static UserPrincipal create(User user){
        return new UserPrincipal(
            user.getId(),
            user.getPassword(),
            user.getEmail(),
            user.getRole()
        );
    }
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority("ROLE_"+role));
    }

    public String getPassword(){
        return password;

    }

    public String getUsername(){
        return email;
    }

    
}
