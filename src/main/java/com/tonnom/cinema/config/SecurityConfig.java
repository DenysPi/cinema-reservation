package com.tonnom.cinema.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

@Configuration 
@EnableWebSecurity 

public class SecurityConfig {
    

    @Bean 
    public JwtAuthenticationConverter jwtAunteficatioConverter(){
        JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
        roles.setAuthoritiesClaimName("roles");
        roles.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(roles);
        return converter;
    }
    
    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(request ->{
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("http://localhost:5173"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
                    config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
                    return config;
                }))
                .authorizeHttpRequests(auth -> auth
                                .requestMatchers("/", "/movies/**").permitAll()
                                .requestMatchers("/auth/**").permitAll()
                                .requestMatchers("/ws/**").permitAll()  
                                .anyRequest().authenticated()
                )  
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) 
                .oauth2ResourceServer(oath -> oath
                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAunteficatioConverter()))
                )   

                .build();

    }
}
