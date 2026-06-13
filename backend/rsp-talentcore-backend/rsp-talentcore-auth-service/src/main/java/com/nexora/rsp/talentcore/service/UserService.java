package com.nexora.rsp.talentcore.service;

import com.nexora.rsp.talentcore.dto.*;

public interface UserService {

    UserResponse registerUser(UserRequest request);

    PageResponse<UserResponse> searchUsers(UserSearchRequest request);

    void assignRoles(AssignRoleRequest request);

    LoginResponse login(LoginRequest request);

    RefreshTokenResponse refreshToken(RefreshTokenRequest request);

    void logout( LogoutRequest request);

    void changePassword(ChangePasswordRequest request);
    CurrentUserResponse  getCurrentUser();
}
