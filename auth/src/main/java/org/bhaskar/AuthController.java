package org.bhaskar;

import jakarta.annotation.Nullable;
import org.bhaskar.dao.User;
import org.bhaskar.dto.Response;
import org.bhaskar.utils.http.headerutils.AuthHeaderUtils;
import org.bhaskar.utils.http.headerutils.AuthHeaderUtils.BasicAuthCredentials;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/login")
public record AuthController(UserService userService,
                             AuthHeaderUtils headerUtils) {


    @PostMapping
    public ResponseEntity<Response<String>> login(
            @Nullable @RequestHeader("Authorization") String authorizationHeader) {
        // Empty authorization header
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            return ResponseBuilder
                    .<String>getErrorResponseBuilder()
                    .message("missing credentials")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .build()
                    .getResponse();
        }

        BasicAuthCredentials credentials = headerUtils.extractBasicAuthUserCredentials(authorizationHeader);
        String email = credentials.email();
        String password = credentials.password();
        User user = userService.findByEmail(email);
        // Wrong credentials
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseBuilder
                    .<String>getErrorResponseBuilder()
                    .message("invalid credentials")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .build()
                    .getResponse();
        }

        // Return JWT
        return ResponseBuilder
                .<String>getSuccessResponseBuilder()
                .data(user.getEmail())
                .message("")
                .httpStatus(HttpStatus.OK)
                .build()
                .getResponse();
    }
}
