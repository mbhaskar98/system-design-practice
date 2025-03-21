package org.bhaskar.auth;

import jakarta.annotation.Nullable;
import org.bhaskar.response.ResponseBuilder;
import org.bhaskar.response.dto.LoginResponse;
import org.bhaskar.response.dto.Response;
import org.bhaskar.user.UserService;
import org.bhaskar.user.model.User;
import org.bhaskar.utils.http.headerutils.AuthHeaderUtils;
import org.bhaskar.utils.http.headerutils.AuthHeaderUtils.BasicAuthCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    @Autowired
    UserService userService;
    @Autowired
    AuthHeaderUtils headerUtils;
    @Autowired
    AuthService authService;

    @Value("${jwt.secret}")
    private String secret;


    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(
            @Nullable @RequestHeader("Authorization") String authorizationHeader) {
        // Empty authorization header
        if (authorizationHeader == null || authorizationHeader.isEmpty()) {
            return ResponseBuilder
                    .<LoginResponse>getErrorResponseBuilder()
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
                    .<LoginResponse>getErrorResponseBuilder()
                    .message("invalid credentials")
                    .httpStatus(HttpStatus.UNAUTHORIZED)
                    .build()
                    .getResponse();
        }

        LoginResponse loginResponse = authService.generateJWTResponseForUser(user, secret);
        // Return JWT
        return ResponseBuilder
                .<LoginResponse>getSuccessResponseBuilder()
                .data(loginResponse)
                .message("")
                .httpStatus(HttpStatus.OK)
                .build()
                .getResponse();
    }
}
