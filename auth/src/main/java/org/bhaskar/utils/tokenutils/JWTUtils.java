package org.bhaskar.utils.tokenutils;

import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JWTUtils {
    /**
     * Create a jwt token based {@code user} and {@code isAdmin} as claims
     * and signing the token using {@code secret}
     *
     * @param username value to be used with username field in the token
     * @param secret   secret key to sign the jwt
     * @param isAdmin  whether the user is admin or not
     * @return jwt token
     * @throws IllegalArgumentException if {@code username} or {@code secret} is null
     */
    public String createJWT(@NotNull String username,
                            @NotNull String secret,
                            boolean isAdmin) throws IllegalArgumentException {
        String result = "";
        if (secret.isEmpty()) {
            log.error("Empty secret");
            return result;
        }

        byte[] decodedString = null;
        try {
            decodedString = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            log.error("Error while decoding:{}, exception:{}", secret, e.getMessage());
            return result;
        }

        Key hmacKey = new SecretKeySpec(
                decodedString,
                SignatureAlgorithm.HS256.getJcaName());

        long expirationMinutes = 20;

        try {
            result = Jwts.builder()
                    .claim("username", username)
                    .claim("admin", isAdmin)
                    .setId(UUID.randomUUID().toString())
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plus(expirationMinutes, ChronoUnit.MINUTES)))
                    .signWith(hmacKey, SignatureAlgorithm.HS256)
                    .setHeaderParam("typ", Header.JWT_TYPE)
                    .compact();

        } catch (InvalidKeyException e) {
            log.error("Error while creating key:{}, exception:{}", hmacKey, e.getMessage());
        }

        return result;
    }

}
