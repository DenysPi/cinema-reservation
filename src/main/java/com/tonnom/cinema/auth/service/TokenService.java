package com.tonnom.cinema.auth.service;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.tonnom.cinema.auth.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Service 
@RequiredArgsConstructor 
public class TokenService {
    private final JwtEncoder jwtEncoder;

    @Value("${jwt.expiration}")
    private long expirationMinutes;

    public String createAccessToken(UserPrincipal user){
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                                .issuer("cinema-reservation")
                                .issuedAt(now)
                                .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
                                .subject(user.getId().toString())
                                .claim("email", user.getUsername())
                                .claim("roles", List.of(user.getRole()))
                                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }
}
