package org.bhaskar.utils.tokenutils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import java.security.Key;

public interface JWTWrapperInterface {
    String generateJWTString(String username,
                             boolean isAdmin,
                             Key hmacKey);

    Jws<Claims> parseClaims(String token, Key hmacKey);
}