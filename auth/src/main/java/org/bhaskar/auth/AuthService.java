package org.bhaskar.auth;

import org.bhaskar.response.dto.LoginResponse;
import org.bhaskar.user.model.User;
import org.bhaskar.utils.tokenutils.JWTUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private JWTUtils jwtUtils;

    /**
     * Generate {@code LoginResponse} object for {@code user}
     *
     * @param user current {@code User}
     * @return {@code LoginResponse}
     * @throws IllegalArgumentException in case null user is passed
     */
    public @NotNull LoginResponse generateJWTResponseForUser(@NotNull User user,
                                                             @NotNull String secret) throws IllegalArgumentException {
        String jwtToken = jwtUtils.createJWT(user.getEmail(), secret, user.isAdmin());
        return new LoginResponse(jwtToken);
    }
}
