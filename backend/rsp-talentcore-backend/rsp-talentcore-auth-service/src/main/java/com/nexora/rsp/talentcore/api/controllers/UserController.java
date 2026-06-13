package com.nexora.rsp.talentcore.api.controllers;

import com.nexora.rsp.talentcore.api.UserApi;
import com.nexora.rsp.talentcore.dto.*;
import com.nexora.rsp.talentcore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<UserResponse> registerUser(UserRequest request) {

        UserResponse response = userService.registerUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Override
    public ResponseEntity<Void> assignRoles(AssignRoleRequest request) {

        userService.assignRoles(request);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {

        return ResponseEntity.ok(userService.login(request));
    }



    @Override
    public ResponseEntity<RefreshTokenResponse> refreshToken(RefreshTokenRequest request) {
        return ResponseEntity.ok(userService.refreshToken(request));
    }


    @Override
    public ResponseEntity<Void> logout(LogoutRequest request) {
        userService.logout(request);
        return ResponseEntity.noContent().build();

    }


    @Override
    public ResponseEntity<Void> changePassword(ChangePasswordRequest request) {

        userService
                .changePassword(
                        request
                );

        return ResponseEntity
                .noContent()
                .build();
    }

    @Override
    public ResponseEntity<
            CurrentUserResponse
            >

    getCurrentUser() {

        return ResponseEntity.ok(

                userService
                        .getCurrentUser()
        );

    }

}
