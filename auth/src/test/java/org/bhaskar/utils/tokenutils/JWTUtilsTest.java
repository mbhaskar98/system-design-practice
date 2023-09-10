package org.bhaskar.utils.tokenutils;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

class JWTUtilsTest {
    private JWTUtils jwtUtils;

    @BeforeEach
    void setup() {
        jwtUtils = new JWTUtils();
    }

    @Nested
    class CreateJWTTests {
        private String userName;

        private String secret;

        private boolean isAdmin;


        @Nested
        class NegativeTests {
            NegativeTests() {
                userName = "";
                secret = "";
                isAdmin = false;
            }

            @Test
            void nullUserName() {
                Assertions.assertThatThrownBy(() -> {
                            jwtUtils.createJWT(null, secret, isAdmin);
                        })
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void nullSecret() {
                Assertions.assertThatThrownBy(() -> {
                            jwtUtils.createJWT(userName, null, isAdmin);
                        })
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void emptySecret() {
                Assertions.assertThat(jwtUtils.createJWT(userName, secret, isAdmin))
                        .isEqualTo("");
            }
        }

        @Nested
        class PositiveTests {
            private final Date issuedAt;
            private final Date expiration;
            private final UUID uuid;
            private final Instant instant;


            PositiveTests() {
                userName = "bhaskar";
                secret = "dGhpcyBpcyBhIHZlcnkgbG9uZyBzZWNyZXQga2V5IHRoYXQgaXMgdmVyeSBsb25n";
                isAdmin = false;
                issuedAt = Date.from(Instant.ofEpochSecond(100));
                expiration = Date.from(Instant.ofEpochSecond(200));
                uuid = UUID.fromString("11111111-1111-1111-1111-111111111111");
                instant = Instant.ofEpochSecond(1);
            }

            private void mockStaticMethodsAndTest(@NotNull TestFunction testFunction) {
                try (MockedStatic<Instant> mockInstance = Mockito.mockStatic(Instant.class);
                     MockedStatic<Date> mockDate = Mockito.mockStatic(Date.class);
                     MockedStatic<UUID> mockUUID = Mockito.mockStatic(UUID.class)
                ) {
                    mockInstance.when(Instant::now).thenReturn(instant);

                    mockDate.when(() -> Date.from(instant)
                    ).thenReturn(issuedAt);
                    mockDate.when(() -> Date.from(instant.plus(20L, ChronoUnit.MINUTES))
                    ).thenReturn(expiration);

                    mockUUID.when(UUID::randomUUID).thenReturn(uuid);

                    testFunction.run();
                }
            }

            @Test
            void emptyUsername() {
                mockStaticMethodsAndTest(() -> {
                    Assertions.assertThat(jwtUtils.createJWT("", secret, isAdmin))
                            .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9" +
                                    ".eyJ1c2VybmFtZSI6IiIsImFkbWluIjpmYWxzZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ." +
                                    "yB9ovnns6MSog4Keo9hCaS4MwCWsNgczkKIYq2sD5SA");
                });
            }


            @Test
            void someUsername() {
                mockStaticMethodsAndTest(() -> {
                    Assertions.assertThat(jwtUtils.createJWT(userName, secret, isAdmin))
                            .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                                    "eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6ZmFsc2UsImp0aSI6IjExMTExMTExLTExMTEtMTExMS0xMTExLTExMTExMTExMTExMSIsImlhdCI6MTAwLCJleHAiOjIwMH0." +
                                    "BALHrlUmCWorj56-NiYsufIsaDmukoErSzS9ujglHLg");
                });
            }

            @Test
            void adminUser() {
                mockStaticMethodsAndTest(() -> {
                    Assertions.assertThat(jwtUtils.createJWT(userName, secret, true))
                            .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                                    "eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6dHJ1ZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ." +
                                    "wBZ-6qWQsesc5IgaNCS-MBgA7Q54natY_jVJOguP3JY");
                });
            }

            private interface TestFunction {
                void run();
            }
        }

    }

}