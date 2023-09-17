package org.bhaskar.utils.tokenutils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.InvalidKeyException;
import org.assertj.core.api.Assertions;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.security.Key;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class JWTUtilsTest {
    @InjectMocks
    private JWTUtils jwtUtils;
    @Mock
    private JWTWrapperInterface jwtWrapper;


    @Nullable String getSecretFromFile() throws RuntimeException {
        String secret;

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
        return secret;
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
                Assertions.assertThatThrownBy(() -> jwtUtils.createJWT(null, secret, isAdmin))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void nullSecret() {
                Assertions.assertThatThrownBy(() -> jwtUtils.createJWT(userName, null, isAdmin))
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
                secret = "dGVzdCBzZWNyZXQ=";
                Mockito
                        .when(jwtWrapper.generateJWTString(userName, isAdmin, jwtUtils.getSigningKey(secret)))
                        .thenThrow(new InvalidKeyException("Secret key length insufficient"));
                Assertions.assertThat(jwtUtils.createJWT(userName, secret, isAdmin))
                        .isEqualTo("");
            }
        }

        @Nested
        class PositiveTests {
            PositiveTests() {
                userName = "bhaskar";
                secret = getSecretFromFile();
                isAdmin = false;
            }

            @Test
            void emptyUsername() {
                userName = "";
                Mockito
                        .when(jwtWrapper.generateJWTString("", isAdmin, jwtUtils.getSigningKey(secret)))
                        .thenReturn("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IiIsImFkbWluIjpmYWxzZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ.I5wvwhInKIzmQSTsn_2i0DrzsVRkoyJyByFhOoeXhIc");
                Assertions.assertThat(jwtUtils.createJWT("", secret, isAdmin))
                        .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6IiIsImFkbWluIjpmYWxzZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ.I5wvwhInKIzmQSTsn_2i0DrzsVRkoyJyByFhOoeXhIc");
            }


            @Test
            void someUsername() {
                userName = "bhaskar";
                Mockito
                        .when(jwtWrapper.generateJWTString(userName, isAdmin, jwtUtils.getSigningKey(secret)))
                        .thenReturn("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6ZmFsc2UsImp0aSI6IjExMTExMTExLTExMTEtMTExMS0xMTExLTExMTExMTExMTExMSIsImlhdCI6MTAwLCJleHAiOjIwMH0.kfU8YNqsZIuF9KdOjjMAcEJ13-IJPi-A1OpjSvnmvf8");
                Assertions.assertThat(jwtUtils.createJWT(userName, secret, isAdmin))
                        .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6ZmFsc2UsImp0aSI6IjExMTExMTExLTExMTEtMTExMS0xMTExLTExMTExMTExMTExMSIsImlhdCI6MTAwLCJleHAiOjIwMH0.kfU8YNqsZIuF9KdOjjMAcEJ13-IJPi-A1OpjSvnmvf8");
            }

            @Test
            void adminUser() {
                isAdmin = true;
                Mockito
                        .when(jwtWrapper.generateJWTString(userName, true, jwtUtils.getSigningKey(secret)))
                        .thenReturn("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6dHJ1ZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ.ZflBdwbqKg-AHqUoMG1klY_hdqRzHjQxg43s_iYyVTI");
                Assertions.assertThat(jwtUtils.createJWT(userName, secret, isAdmin))
                        .isEqualTo("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6dHJ1ZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ.ZflBdwbqKg-AHqUoMG1klY_hdqRzHjQxg43s_iYyVTI");
            }
        }

    }

    @Nested
    class IsValidJWTTests {
        private String token;
        private String secret;

        @BeforeEach
        void setup() {
            token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6ZmFsc2UsImp0aSI6IjExMTExMTExLTExMTEtMTExMS0xMTExLTExMTExMTExMTExMSIsImlhdCI6MTAwLCJleHAiOjIwMH0.kfU8YNqsZIuF9KdOjjMAcEJ13-IJPi-A1OpjSvnmvf8";
            secret = getSecretFromFile();
        }

        @Nested
        class NegativeTests {
            @Test
            void nullSecret() {
                secret = null;
                Assertions.assertThatThrownBy(() -> jwtUtils.isValidJWT(token, secret))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void emptySecret() {
                secret = "";
                Assertions.assertThatThrownBy(() -> jwtUtils.isValidJWT(token, secret))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void nonBase64EncodedSecret() {
                secret = "|| ";
                Assertions.assertThat(jwtUtils.isValidJWT(token, secret))
                        .isEqualTo(false);
            }

            @Test
            void nullToken() {
                token = null;
                Assertions.assertThatThrownBy(() -> jwtUtils.isValidJWT(token, secret))
                        .isInstanceOf(IllegalArgumentException.class);
            }

            @Test
            void emptyToken() {
                token = "";
                Mockito
                        .when(jwtWrapper.parseClaims(token, jwtUtils.getSigningKey(secret)))
                        .thenThrow(new IllegalArgumentException("JWT String argument cannot be null or empty."));
                Assertions.assertThat(jwtUtils.isValidJWT(token, secret))
                        .isEqualTo(false);
            }

            @Test
            void invalidToken() {
                token = "skabjsabkva";
                Mockito
                        .when(jwtWrapper.parseClaims(token, jwtUtils.getSigningKey(secret)))
                        .thenThrow(new MalformedJwtException("JWT strings must contain exactly 2 period characters. Found: 0"));
                Assertions.assertThat(jwtUtils.isValidJWT(token, secret))
                        .isEqualTo(false);
            }

            @Test
            void expiredToken() {
                Mockito
                        .when(jwtWrapper.parseClaims(token, jwtUtils.getSigningKey(secret)))
                        .thenThrow(new MalformedJwtException("JWT expired at 1970-01-01T00:03:20Z. Current time: 2023-09-17T22:27:48Z, a difference of 1694989468545 milliseconds.  Allowed clock skew: 0 milliseconds."));
                Assertions.assertThat(jwtUtils.isValidJWT(token, secret))
                        .isEqualTo(false);
            }
        }

        @Nested
        class PositiveTests {


            @Test
            void validJWT() {
                Mockito.when(jwtWrapper.parseClaims(Mockito.anyString(), Mockito.any(Key.class))).thenReturn(new Jws<>() {
                    @Override
                    public String getSignature() {
                        return "signature";
                    }

                    @Override
                    public JwsHeader getHeader() {
                        return null;
                    }

                    @Override
                    public Claims getBody() {
                        return null;
                    }
                });
                Assertions.assertThat(jwtUtils.isValidJWT(token, secret))
                        .isEqualTo(true);

            }

        }
    }
}