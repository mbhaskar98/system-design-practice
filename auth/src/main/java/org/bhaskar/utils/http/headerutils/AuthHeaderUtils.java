package org.bhaskar.utils.http.headerutils;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Slf4j
@Component
public record AuthHeaderUtils() {

    private static final String basicAuthPrefix = "Basic ";

    private @NotNull String extractAuthParams(@NotNull AuthScheme authScheme,
                                              @NotNull String authorizationHeader) {

        String expectedPrefix = "";

        switch (authScheme) {
            case BASIC -> {
                expectedPrefix = AuthHeaderUtils.basicAuthPrefix;
            }
            case BEARER -> {
                log.debug("Bearer");
                log.error("Not implement:" + authScheme.name());
            }
            case DIGEST -> {
                log.debug("Digest");
                log.error("Not implement:" + authScheme.name());
            }
            default -> log.error("Not implement:" + authScheme.name());
        }

        if (authorizationHeader.length() < expectedPrefix.length()) {
            log.error("Header length is less than prefix length:" + expectedPrefix);
            return "";
        }

        String actualPrefix = authorizationHeader.substring(0, expectedPrefix.length());
        log.debug("Header prefix:" + actualPrefix);


        if (!actualPrefix.equals(expectedPrefix)) {
            log.error("Actual prefix:" + actualPrefix + " not equals expected prefix:" + expectedPrefix);
            return "";
        }

        return authorizationHeader.substring(expectedPrefix.length());
    }

    @Contract("_ -> new")
    public @NotNull BasicAuthCredentials extractBasicAuthUserCredentials(@NotNull String authorizationHeader) {

        String base64Credentials = extractAuthParams(AuthScheme.BASIC, authorizationHeader);


        try {
            // Decode the base64 string
            byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
            String decodedCredentials = new String(decodedBytes);

            // Split email and password
            String[] credentials = decodedCredentials.split(":");
            if (credentials.length == 2) {
                return new BasicAuthCredentials(credentials[0], credentials[1]);
            }
        } catch (IllegalArgumentException e) {
            log.error("Error while decoding:{}, exception:{}", authorizationHeader, e.getMessage());
        }
        return BasicAuthCredentials.EMPTY_CREDENTIALS;
    }

    private enum AuthScheme {
        BASIC,
        DIGEST,
        BEARER,
    }

    public record BasicAuthCredentials(String email,
                                       String password) {
        public static BasicAuthCredentials EMPTY_CREDENTIALS = new BasicAuthCredentials("", "");
    }
}
