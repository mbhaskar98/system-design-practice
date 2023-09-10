package org.bhaskar.utils.tokenutils;

import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;
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

            @Test
            void invalidBase64Secret() {
                Assertions.assertThat(jwtUtils.createJWT(userName, "dGVzdCBzZWNyZXQ=1234567890", isAdmin))
                        .isEqualTo("");
            }

            @Test
            void smallSecretKeyLength() {
                Assertions.assertThat(jwtUtils.createJWT(userName, "dGVzdCBzZWNyZXQ=", isAdmin))
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

                // Get the classloader for the current class
                ClassLoader classLoader = getClass().getClassLoader();

                // Specify the path to the resource file
                String resourcePath = "application-test.yml"; // Replace with the actual resource path

                try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
                    Yaml yaml = new Yaml();
                    Map<String, Object> data = yaml.load(inputStream);
                    System.out.println(((Map<String, Object>) data.get("jwt")).get("secret"));
                    secret = ((Map<String, Object>) data.get("jwt")).get("secret").toString();
                } catch (Exception e) {
                    System.out.println("Error" + e.getMessage());
                    throw new RuntimeException(e);
                }
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
                            .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IiIsImFkbWluIjpmYWxzZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ.I5wvwhInKIzmQSTsn_2i0DrzsVRkoyJyByFhOoeXhIc");
                });
            }


            @Test
            void someUsername() {
                mockStaticMethodsAndTest(() -> {
                    Assertions.assertThat(jwtUtils.createJWT(userName, secret, isAdmin))
                            .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6ZmFsc2UsImp0aSI6IjExMTExMTExLTExMTEtMTExMS0xMTExLTExMTExMTExMTExMSIsImlhdCI6MTAwLCJleHAiOjIwMH0.kfU8YNqsZIuF9KdOjjMAcEJ13-IJPi-A1OpjSvnmvf8");
                });
            }

            @Test
            void adminUser() {
                mockStaticMethodsAndTest(() -> {
                    Assertions.assertThat(jwtUtils.createJWT(userName, secret, true))
                            .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6dHJ1ZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ.ZflBdwbqKg-AHqUoMG1klY_hdqRzHjQxg43s_iYyVTI");
                });
            }

            private interface TestFunction {
                void run();
            }
        }

    }

}