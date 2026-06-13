package com.nexora.rsp.talentcore;

import com.nexora.rsp.talentcore.config.UserPrincipal;
import com.nexora.rsp.talentcore.domain.RefreshToken;
import com.nexora.rsp.talentcore.domain.Role;
import com.nexora.rsp.talentcore.domain.User;
import com.nexora.rsp.talentcore.domain.UserRole;
import com.nexora.rsp.talentcore.domain.UserStatus;
import com.nexora.rsp.talentcore.dto.AssignRoleRequest;
import com.nexora.rsp.talentcore.dto.ChangePasswordRequest;
import com.nexora.rsp.talentcore.dto.LoginRequest;
import com.nexora.rsp.talentcore.dto.LogoutRequest;
import com.nexora.rsp.talentcore.dto.RefreshTokenRequest;
import com.nexora.rsp.talentcore.dto.UserRequest;
import com.nexora.rsp.talentcore.util.TokenHashUtil;

import java.time.LocalDateTime;
import java.util.List;

public final class TestDataFactory {

    public static final String EMAIL = "murali@test.com";
    public static final String PASSWORD = "Password@123";
    public static final String NEW_PASSWORD = "Newpass@123";
    public static final String EMPLOYEE = "EMPLOYEE";
    public static final String TEAM_MANAGER = "TEAM_MANAGER";

    private TestDataFactory() {
    }

    public static User user() {

        User user = new User();

        user.setUserId(1L);
        user.setEmployeeCode("EMP001");
        user.setFirstName("Murali");
        user.setLastName("Test");
        user.setEmail(EMAIL);
        user.setPasswordHash("encoded-password");
        user.setUserStatus(UserStatus.ACTIVE);

        return user;
    }

    public static Role role(String roleCode) {

        Role role = new Role();

        role.setRoleId(1L);
        role.setRoleCode(roleCode);
        role.setRoleName(roleCode);
        role.setRoleDescription(roleCode + " role");

        return role;
    }

    public static UserRole userRole(User user, Role role) {

        UserRole userRole = new UserRole();

        userRole.setUserRoleId(1L);
        userRole.setUser(user);
        userRole.setRole(role);

        return userRole;
    }

    public static RefreshToken refreshToken(User user, String rawToken) {

        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setRefreshTokenId(1L);
        refreshToken.setUser(user);
        refreshToken.setTokenHash(TokenHashUtil.hash(rawToken));
        refreshToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        refreshToken.setRevoked(false);

        return refreshToken;
    }

    public static RefreshToken revokedRefreshToken(User user, String rawToken) {

        RefreshToken refreshToken = refreshToken(user, rawToken);
        refreshToken.setRevoked(true);

        return refreshToken;
    }

    public static RefreshToken expiredRefreshToken(User user, String rawToken) {

        RefreshToken refreshToken = refreshToken(user, rawToken);
        refreshToken.setExpiryDate(LocalDateTime.now().minusMinutes(1));

        return refreshToken;
    }

    public static UserRequest userRequest(String email) {

        UserRequest request = new UserRequest();

        request.setEmployeeCode("EMP001");
        request.setFirstName("Murali");
        request.setLastName("Test");
        request.setEmail(email);
        request.setPassword(PASSWORD);

        return request;
    }

    public static LoginRequest loginRequest(String email, String password) {

        LoginRequest request = new LoginRequest();

        request.setEmail(email);
        request.setPassword(password);

        return request;
    }

    public static RefreshTokenRequest refreshTokenRequest(String refreshToken) {

        RefreshTokenRequest request = new RefreshTokenRequest();

        request.setRefreshToken(refreshToken);

        return request;
    }

    public static LogoutRequest logoutRequest(String refreshToken) {

        LogoutRequest request = new LogoutRequest();

        request.setRefreshToken(refreshToken);

        return request;
    }

    public static ChangePasswordRequest changePasswordRequest() {

        ChangePasswordRequest request = new ChangePasswordRequest();

        request.setCurrentPassword(PASSWORD);
        request.setNewPassword(NEW_PASSWORD);
        request.setConfirmPassword(NEW_PASSWORD);

        return request;
    }

    public static AssignRoleRequest assignRoleRequest(Long userId, List<String> roleCodes) {

        AssignRoleRequest request = new AssignRoleRequest();

        request.setUserId(userId);
        request.setRoleCodes(roleCodes);

        return request;
    }

    public static UserPrincipal principal(Long userId, String email, List<String> roles) {

        return UserPrincipal.builder()
                .userId(userId)
                .email(email)
                .roles(roles)
                .build();
    }
}
