package com.nexora.rsp.talentcore.api.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nexora.rsp.talentcore.dto.LoginRequest;
import com.nexora.rsp.talentcore.dto.UserRequest;
import com.nexora.rsp.talentcore.repository.RefreshTokenRepository;
import com.nexora.rsp.talentcore.repository.UserRepository;
import com.nexora.rsp.talentcore.repository.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIT {

    private static final String PASSWORD = "Password@123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUserData() {

        refreshTokenRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void registerLoginAndCurrentUserFlowSucceedsWithJwt() throws Exception {

        UserRequest userRequest = userRequest("it-user@example.com", "IT001");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("it-user@example.com"))
                .andExpect(jsonPath("$.active").value(true));

        LoginRequest loginRequest = loginRequest("it-user@example.com", PASSWORD);

        String loginResponse = mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode tokenPayload = objectMapper.readTree(loginResponse);
        String accessToken = tokenPayload.get("accessToken").asText();

        mockMvc.perform(get("/api/users/current")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("it-user@example.com"))
                .andExpect(jsonPath("$.roles").isArray());

        assertThat(refreshTokenRepository.findAll()).hasSize(1);
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void registerRejectsValidationFailures() throws Exception {

        UserRequest request = userRequest("not-an-email", "IT002");
        request.setFirstName("");
        request.setPassword("short");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Invalid email format"))
                .andExpect(jsonPath("$.firstName").value("First name is required"))
                .andExpect(jsonPath("$.password").value("Password must contain minimum 8 characters"));
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void loginRejectsInvalidCredentials() throws Exception {

        UserRequest userRequest = userRequest("invalid-login@example.com", "IT003");
        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = loginRequest("invalid-login@example.com", "wrong-password");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void currentUserRejectsMissingAndInvalidJwt() throws Exception {

        mockMvc.perform(get("/api/users/current"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication required"));

        mockMvc.perform(get("/api/users/current")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Authentication required"));
    }

    @Test
    @WithMockUser(roles = "EMPLOYEE")
    void registerRejectsForbiddenRoleWithAccessDeniedResponse() throws Exception {

        UserRequest userRequest = userRequest("forbidden-register@example.com", "IT004");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.message").value("Access denied"));
    }

    private UserRequest userRequest(String email, String employeeCode) {

        UserRequest request = new UserRequest();
        request.setEmployeeCode(employeeCode);
        request.setFirstName("Integration");
        request.setLastName("Test");
        request.setEmail(email);
        request.setPassword(PASSWORD);

        return request;
    }

    private LoginRequest loginRequest(String email, String password) {

        LoginRequest request = new LoginRequest();
        request.setEmail(email);
        request.setPassword(password);

        return request;
    }
}
