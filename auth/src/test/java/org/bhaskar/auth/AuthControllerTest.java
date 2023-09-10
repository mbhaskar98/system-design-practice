package org.bhaskar.auth;

import org.bhaskar.dao.User;
import org.bhaskar.response.dto.LoginResponse;
import org.bhaskar.user.UserService;
import org.bhaskar.utils.http.headerutils.AuthHeaderUtils;
import org.bhaskar.utils.http.headerutils.AuthHeaderUtils.BasicAuthCredentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@WebMvcTest(controllers = AuthController.class)
class AuthControllerTest {

    @MockBean
    private UserService userService;

    @MockBean
    private AuthHeaderUtils headerUtils;

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private AuthService authService;

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("route not implemented")
        void routeNotImplemented() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/looogin"))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @DisplayName("method not allowed")
        void methodNotAllowed() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/login"))
                    .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("auth without auth header")
        public void emptyAuthHeader() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/login"))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.content().json("{'status':'ERROR','message':'missing credentials'}"));
        }


        @Test
        @DisplayName("invalid credentials")
        public void invalidCredentials() throws Exception {
            String authHeader = "Basic hello";

            Mockito.when(headerUtils.extractBasicAuthUserCredentials(authHeader))
                    .thenReturn(AuthHeaderUtils.BasicAuthCredentials.EMPTY_CREDENTIALS);
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/v1/login")
                            .header("Authorization", authHeader))
                    .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                    .andExpect(MockMvcResultMatchers.content().json("{'status':'ERROR','message':'invalid credentials'}"));
        }
    }

    @Nested
    class PositiveTests {

        private final String email;
        private final String password;

        private final boolean isAdmin;
        private String token;

        // Mock objects
        private BasicAuthCredentials mockCredentials;
        private User mockUser;
        private LoginResponse mockLoginResponse;

        private String authHeader;

        PositiveTests() {
            this.email = "email";
            this.password = "password";
            this.token = "token";
            this.isAdmin = false;
        }

        void mockObjects() {
            BasicAuthCredentials mockCredentials = new BasicAuthCredentials(email, password);
            User mockUser = new User(1, email, password, isAdmin);
            LoginResponse mockLoginResponse = new LoginResponse(token);
            authHeader = "Basic ZW1haWw6cGFzc3dvcmQ=";
            Mockito.when(headerUtils.extractBasicAuthUserCredentials(authHeader))
                    .thenReturn(mockCredentials);
            Mockito.when(userService.findByEmail(email))
                    .thenReturn(mockUser);
            Mockito.when(authService.generateJWTResponseForUser(mockUser, "prod-secret")).thenReturn(mockLoginResponse);
        }

        @Test
        @DisplayName("success")
        void successResponse() throws Exception {
            mockObjects();
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/v1/login")
                            .header("Authorization", authHeader))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json("{'status':'SUCCESS','message':'','data': {'token' : '" + token + "'}}"));
        }

        @Test
        @DisplayName("return jwt token")
        void validJWT() throws Exception {
            token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
                    "eyJ1c2VybmFtZSI6ImJoYXNrYXIiLCJhZG1pbiI6dHJ1ZSwianRpIjoiMTExMTExMTEtMTExMS0xMTExLTExMTEtMTExMTExMTExMTExIiwiaWF0IjoxMDAsImV4cCI6MjAwfQ." +
                    "wBZ-6qWQsesc5IgaNCS-MBgA7Q54natY_jVJOguP3JY";
            mockObjects();
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/v1/login")
                            .header("Authorization", authHeader))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json("{'status':'SUCCESS','message':'','data': {'token' : '" + token + "'}}"));
        }

    }
}