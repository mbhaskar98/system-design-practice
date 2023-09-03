package org.bhaskar.utils.http.headerutils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;


class AuthHeaderUtilsTest {

    final private AuthHeaderUtils authHeaderUtils;
    private AuthHeaderUtils.BasicAuthCredentials expected;
    private AuthHeaderUtils.BasicAuthCredentials actual;


    AuthHeaderUtilsTest() {
        authHeaderUtils = new AuthHeaderUtils();
    }

    @Nested
    class BasicAuthHeaderTests {

        @Nested
        class NegativeTests {
            @BeforeEach
            void setupEmpty() {
                expected = AuthHeaderUtils.BasicAuthCredentials.EMPTY_CREDENTIALS;
            }

            @Test
            @DisplayName("Empty auth header")
            void emptyHeader() {
                actual = authHeaderUtils.extractBasicAuthUserCredentials("");
                Assertions.assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Some other scheme than basic")
            void differentAuthScheme() {
                actual = authHeaderUtils.extractBasicAuthUserCredentials("abcd asjfnsajf");
                Assertions.assertThat(actual).isEqualTo(expected);
            }

            @Test
            @DisplayName("Some non base 64 encoded")
            void notBase64Encoded() {
                actual = authHeaderUtils.extractBasicAuthUserCredentials("Basic ,,asdasd");
                Assertions.assertThat(actual).isEqualTo(expected);
            }
        }

        @Nested
        class PositiveTests {
            @BeforeEach
            void setupEmpty() {
                expected = new AuthHeaderUtils.BasicAuthCredentials("email", "password");
            }

            @Test
            @DisplayName("Basic 64 encoded user name and password")
            void base64EncodedBasicAuthHeader() {
                actual = authHeaderUtils.extractBasicAuthUserCredentials("Basic ZW1haWw6cGFzc3dvcmQ=");
                Assertions.assertThat(actual).isEqualTo(expected);
            }
        }
    }

}