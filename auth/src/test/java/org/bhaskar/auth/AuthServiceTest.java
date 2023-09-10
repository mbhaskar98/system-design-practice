package org.bhaskar.auth;

import org.assertj.core.api.Assertions;
import org.bhaskar.response.dto.LoginResponse;
import org.bhaskar.user.model.User;
import org.bhaskar.utils.tokenutils.JWTUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    private final String secret;
    private final User user;

    @InjectMocks
    private AuthService authService;
    @Mock
    private JWTUtils jwtUtils;

    AuthServiceTest() {
        secret = "prod-secret";
        user = new User(1, "email", "password", false);
    }


    @Nested
    class GenerateJWTTests {

        @Nested
        class NegativeTests {

            @Test
            @DisplayName("Null user")
            void nullUser() {
                Assertions.assertThatThrownBy(() -> {
                    authService.generateJWTResponseForUser(null, secret);
                }).isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            @DisplayName("Null Secret")
            void nullSecret() {

                Assertions.assertThatThrownBy(() -> {
                    authService.generateJWTResponseForUser(user, null);
                }).isInstanceOf(IllegalArgumentException.class);
            }
        }

        @Nested
        class PositiveTests {

            @Test
            @DisplayName("success response")
            void success() {
                String token = "token-----asfag";
                LoginResponse expectedResponse = new LoginResponse(token);
                Mockito.when(jwtUtils.createJWT(user.getEmail(), secret, user.isAdmin()))
                        .thenReturn(token);

                Assertions.assertThat(authService.generateJWTResponseForUser(user, secret))
                        .isEqualTo(expectedResponse);
            }

        }
    }

}