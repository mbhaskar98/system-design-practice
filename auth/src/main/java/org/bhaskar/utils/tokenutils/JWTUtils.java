package org.bhaskar.utils.tokenutils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.InvalidKeyException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;

@Slf4j
@Component
public class JWTUtils {
    @Autowired
    private JWTWrapperInterface jwtWrapper;

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
                            boolean isAdmin) {
        String result = "";
        if (secret.isEmpty()) {
            log.error("Empty secret");
            return result;
        }

        Key hmacKey = getSigningKey(secret);
        if (hmacKey == null) {
            log.error("Empty or illegal signing key");
            return result;
        }

        try {
            result = jwtWrapper.generateJWTString(
                    username,
                    isAdmin,
                    hmacKey
            );
        } catch (InvalidKeyException e) {
            log.error("Error while creating key:{}, exception:{}", hmacKey, e.getMessage());
        }

        return result;
    }

    public boolean isValidJWT(@NotNull String token,
                              @NotNull String secret) {
        Key hmacKey = getSigningKey(secret);
        if (hmacKey == null) {
            log.error("Empty or illegal signing key");
            return false;
        }
        try {
            Jws<Claims> jwt = jwtWrapper.parseClaims(token, hmacKey);
            log.info(jwt.toString());

        } catch (
                ExpiredJwtException
                | UnsupportedJwtException
                | MalformedJwtException
                | SignatureException
                | IllegalArgumentException e) {
            log.error("Error while parsing jwt:{}", e.getMessage());
            return false;
        }
        return true;
    }

    public @Nullable Key getSigningKey(@NotNull String secret) {
        Key hmacKey = null;
        byte[] decodedString = null;
        try {
            decodedString = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException e) {
            log.error("Error while decoding:{}, exception:{}", secret, e.getMessage());
            return hmacKey;
        }
        hmacKey = new SecretKeySpec(
                decodedString,
                SignatureAlgorithm.HS256.getJcaName());
        return hmacKey;
    }

}
