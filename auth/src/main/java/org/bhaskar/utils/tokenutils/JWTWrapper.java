package org.bhaskar.utils.tokenutils;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Component
class JWTWrapper implements JWTWrapperInterface {

    @Override
    public String generateJWTString(String username, boolean isAdmin, Key hmacKey) {
        long expirationMinutes = 20;
        return Jwts.builder()
                .claim("username", username)
                .claim("admin", isAdmin)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .setHeaderParam("typ", Header.JWT_TYPE)
                .compact();
    }

    @Override
    public Jws<Claims> parseClaims(String token, Key hmacKey) {
        return Jwts.parserBuilder()
                .setSigningKey(hmacKey)
                .build()
                .parseClaimsJws(token);
    }
}
