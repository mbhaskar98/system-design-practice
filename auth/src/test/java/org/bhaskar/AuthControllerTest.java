package org.bhaskar;

import org.bhaskar.dao.User;
import org.bhaskar.utils.http.headerutils.AuthHeaderUtils;
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

        PositiveTests() {
            this.email = "email";
            this.password = "password";
        }

        @Test
        @DisplayName("success")
        void successResponse() throws Exception {
            String authHeader = "Basic ZW1haWw6cGFzc3dvcmQ=";

            Mockito.when(headerUtils.extractBasicAuthUserCredentials(authHeader))
                    .thenReturn(new AuthHeaderUtils.BasicAuthCredentials(email, password));
            Mockito.when(userService.findByEmail("email"))
                    .thenReturn(new User(1, email, password));
            mockMvc.perform(MockMvcRequestBuilders
                            .post("/api/v1/login")
                            .header("Authorization", authHeader))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json("{'status':'SUCCESS','message':'','data':" + email + "}"));
        }
    }
}