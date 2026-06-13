package com.nexora.rsp.talentcore.api.controllers;

import com.nexora.rsp.talentcore.TestDataFactory;
import com.nexora.rsp.talentcore.dto.AssignRoleRequest;
import com.nexora.rsp.talentcore.dto.ChangePasswordRequest;
import com.nexora.rsp.talentcore.dto.CurrentUserResponse;
import com.nexora.rsp.talentcore.dto.LoginRequest;
import com.nexora.rsp.talentcore.dto.LoginResponse;
import com.nexora.rsp.talentcore.dto.LogoutRequest;
import com.nexora.rsp.talentcore.dto.PageResponse;
import com.nexora.rsp.talentcore.dto.RefreshTokenRequest;
import com.nexora.rsp.talentcore.dto.RefreshTokenResponse;
import com.nexora.rsp.talentcore.dto.UserRequest;
import com.nexora.rsp.talentcore.dto.UserResponse;
import com.nexora.rsp.talentcore.dto.UserSearchRequest;
import com.nexora.rsp.talentcore.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @InjectMocks
    private UserSearchController userSearchController;

    @Test
    void registerUserReturnsCreatedResponse() {

        UserRequest request = TestDataFactory.userRequest(TestDataFactory.EMAIL);
        UserResponse userResponse = new UserResponse();
        userResponse.setUserId(1L);
        userResponse.setEmail(TestDataFactory.EMAIL);
        userResponse.setRoles(List.of());
        userResponse.setActive(true);

        when(userService.registerUser(request)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.registerUser(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(userResponse);
    }

    @Test
    void searchUsersReturnsPageResponse() {

        UserSearchRequest request = new UserSearchRequest();
        PageResponse<UserResponse> pageResponse = PageResponse.<UserResponse>builder()
                .content(List.of())
                .totalElements(0L)
                .build();

        when(userService.searchUsers(request)).thenReturn(pageResponse);

        ResponseEntity<PageResponse<UserResponse>> response = userSearchController.searchUsers(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(pageResponse);
    }

    @Test
    void assignRolesReturnsNoContent() {

        AssignRoleRequest request = TestDataFactory.assignRoleRequest(1L, List.of(TestDataFactory.EMPLOYEE));

        ResponseEntity<Void> response = userController.assignRoles(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.hasBody()).isFalse();
        verify(userService).assignRoles(request);
    }

    @Test
    void loginReturnsTokens() {

        LoginRequest request = TestDataFactory.loginRequest(TestDataFactory.EMAIL, TestDataFactory.PASSWORD);
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken("access-token")
                .refreshToken("refresh-token")
                .tokenType("Bearer")
                .build();

        when(userService.login(request)).thenReturn(loginResponse);

        ResponseEntity<LoginResponse> response = userController.login(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(loginResponse);
    }




    @Test
    void refreshTokenReturnsAccessToken() {

        RefreshTokenRequest request = TestDataFactory.refreshTokenRequest("refresh-token");
        RefreshTokenResponse refreshTokenResponse = RefreshTokenResponse.builder()
                .accessToken("access-token")
                .tokenType("Bearer")
                .build();

        when(userService.refreshToken(request)).thenReturn(refreshTokenResponse);

        ResponseEntity<RefreshTokenResponse> response = userController.refreshToken(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(refreshTokenResponse);
    }

    @Test
    void logoutReturnsNoContent() {

        LogoutRequest request = TestDataFactory.logoutRequest("refresh-token");

        ResponseEntity<Void> response = userController.logout(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).logout(request);
    }

    @Test
    void changePasswordReturnsNoContent() {

        ChangePasswordRequest request = TestDataFactory.changePasswordRequest();

        ResponseEntity<Void> response = userController.changePassword(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(userService).changePassword(request);
    }

    @Test
    void getCurrentUserReturnsCurrentUserResponse() {

        CurrentUserResponse currentUserResponse = CurrentUserResponse.builder()
                .email(TestDataFactory.EMAIL)
                .roles(List.of(TestDataFactory.EMPLOYEE))
                .build();

        when(userService.getCurrentUser()).thenReturn(currentUserResponse);

        ResponseEntity<CurrentUserResponse> response = userController.getCurrentUser();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(currentUserResponse);
    }
}
