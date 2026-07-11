package com.gabrielf.revendedora_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    public String gerarToken(String login) {
        Date agora =  new Date();
        Date expiracao =  new Date(agora.getTime() + expirationMs);

        return Jwts.builder()
                .subject(login)
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChaveAssinatura())
                .compact();

    }

    public String extrairLogin(String token) {
        return extrairClaim(token, Claims::getSubject);

    }

    public boolean tokenValido (String token, String login) {
        String loginDoToken =  extrairLogin(token);
        return loginDoToken.equals(login) && !tokenExpirado(token);

    }

    private boolean tokenExpirado(String token) {
        return extrairClaim(token, Claims::getExpiration).before(new Date());

    }

    private <T> T extrairClaim(String token, Function<Claims, T> resolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getChaveAssinatura())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);

    }

    private SecretKey getChaveAssinatura() {
        return Keys.hmacShaKeyFor(secret.getBytes());

    }


}
