package com.nexora.rsp.talentcore.service.impl;

import com.nexora.rsp.talentcore.TestDataFactory;
import com.nexora.rsp.talentcore.config.UserPrincipal;
import com.nexora.rsp.talentcore.domain.RefreshToken;
import com.nexora.rsp.talentcore.domain.Role;
import com.nexora.rsp.talentcore.domain.User;
import com.nexora.rsp.talentcore.domain.UserRole;
import com.nexora.rsp.talentcore.dto.AssignRoleRequest;
import com.nexora.rsp.talentcore.dto.ChangePasswordRequest;
import com.nexora.rsp.talentcore.dto.CurrentUserResponse;
import com.nexora.rsp.talentcore.dto.LoginRequest;
import com.nexora.rsp.talentcore.dto.LoginResponse;
import com.nexora.rsp.talentcore.dto.LogoutRequest;
import com.nexora.rsp.talentcore.dto.RefreshTokenRequest;
import com.nexora.rsp.talentcore.dto.RefreshTokenResponse;
import com.nexora.rsp.talentcore.dto.UserRequest;
import com.nexora.rsp.talentcore.dto.UserResponse;
import com.nexora.rsp.talentcore.dto.UserSearchRequest;
import com.nexora.rsp.talentcore.execeptions.InvalidCredentialsException;
import com.nexora.rsp.talentcore.execeptions.ResourceAlreadyExistsException;
import com.nexora.rsp.talentcore.execeptions.ResourceNotFoundException;
import com.nexora.rsp.talentcore.execeptions.UnauthorizedException;
import com.nexora.rsp.talentcore.mapper.UserMapper;
import com.nexora.rsp.talentcore.repository.RefreshTokenRepository;
import com.nexora.rsp.talentcore.repository.RoleRepository;
import com.nexora.rsp.talentcore.repository.UserRepository;
import com.nexora.rsp.talentcore.repository.UserRoleRepository;
import com.nexora.rsp.talentcore.search.GenericSearchBuilder;
import com.nexora.rsp.talentcore.util.JwtUtil;
import com.nexora.rsp.talentcore.util.TokenHashUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private GenericSearchBuilder genericSearchBuilder;

    @Spy
    private UserMapper userMapper = new UserMapper();

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @AfterEach
    void clearSecurityContext() {

        SecurityContextHolder.clearContext();
    }

    @Test
    void registerUserCreatesActiveUser() {

        UserRequest request = TestDataFactory.userRequest(TestDataFactory.EMAIL);

        when(userRepository.existsByEmail(TestDataFactory.EMAIL)).thenReturn(false);
        when(passwordEncoder.encode(TestDataFactory.PASSWORD)).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setUserId(1L);
            return user;
        });

        UserResponse response = userService.registerUser(request);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getEmail()).isEqualTo(TestDataFactory.EMAIL);
        assertThat(response.getActive()).isTrue();
        verify(passwordEncoder).encode(TestDataFactory.PASSWORD);
    }

    @Test
    void registerUserRejectsDuplicateEmail() {

        UserRequest request = TestDataFactory.userRequest(TestDataFactory.EMAIL);

        when(userRepository.existsByEmail(TestDataFactory.EMAIL)).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(request))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessage("Email already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginReturnsAccessAndRefreshTokensForUserWithoutRoles() {

        User user = TestDataFactory.user();
        LoginRequest request = TestDataFactory.loginRequest(
                user.getEmail(),
                TestDataFactory.PASSWORD
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TestDataFactory.PASSWORD, user.getPasswordHash())).thenReturn(true);
        when(userRoleRepository.findByUser(user)).thenReturn(List.of());
        when(jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), List.of())).thenReturn("access-token");
        when(jwtUtil.generateRefreshToken(user.getUserId(), user.getEmail())).thenReturn("refresh-token");

        LoginResponse response = userService.login(request);

        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        assertThat(refreshTokenCaptor.getValue().getTokenHash())
                .isEqualTo(TokenHashUtil.hash("refresh-token"));
        assertThat(refreshTokenCaptor.getValue().getRevoked()).isFalse();
    }

    @Test
    void loginRejectsUnknownEmail() {

        LoginRequest request = TestDataFactory.loginRequest(
                TestDataFactory.EMAIL,
                TestDataFactory.PASSWORD
        );

        when(userRepository.findByEmail(TestDataFactory.EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void loginRejectsInvalidPassword() {

        User user = TestDataFactory.user();
        LoginRequest request = TestDataFactory.loginRequest(
                user.getEmail(),
                "wrong-password"
        );

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> userService.login(request))
                .isInstanceOf(InvalidCredentialsException.class)
                .hasMessage("Invalid credentials");
    }

    @Test
    void assignRolesCreatesOnlyMissingMappings() {

        User user = TestDataFactory.user();
        Role employee = TestDataFactory.role(TestDataFactory.EMPLOYEE);
        Role manager = TestDataFactory.role(TestDataFactory.TEAM_MANAGER);
        AssignRoleRequest request = TestDataFactory.assignRoleRequest(
                user.getUserId(),
                List.of(TestDataFactory.EMPLOYEE, TestDataFactory.TEAM_MANAGER)
        );

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleCodeIn(request.getRoleCodes())).thenReturn(List.of(employee, manager));
        when(userRoleRepository.findByUser(user))
                .thenReturn(List.of(TestDataFactory.userRole(user, employee)));

        userService.assignRoles(request);

        ArgumentCaptor<List<UserRole>> userRolesCaptor = ArgumentCaptor.forClass(List.class);
        verify(userRoleRepository).saveAll(userRolesCaptor.capture());
        assertThat(userRolesCaptor.getValue())
                .hasSize(1)
                .first()
                .extracting(userRole -> userRole.getRole().getRoleCode())
                .isEqualTo(TestDataFactory.TEAM_MANAGER);
    }

    @Test
    void assignRolesRejectsMissingRole() {

        User user = TestDataFactory.user();
        AssignRoleRequest request = TestDataFactory.assignRoleRequest(
                user.getUserId(),
                List.of("UNKNOWN")
        );

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleCodeIn(List.of("UNKNOWN"))).thenReturn(List.of());

        assertThatThrownBy(() -> userService.assignRoles(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Role not found: UNKNOWN");
    }

    @Test
    void assignRolesRejectsMissingUser() {

        AssignRoleRequest request = TestDataFactory.assignRoleRequest(99L, List.of(TestDataFactory.EMPLOYEE));

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.assignRoles(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");

        verify(roleRepository, never()).findByRoleCodeIn(any());
    }

    @Test
    void assignRolesNormalizesNullBlankDuplicateRoleCodes() {

        User user = TestDataFactory.user();
        Role employee = TestDataFactory.role(TestDataFactory.EMPLOYEE);
        AssignRoleRequest request = TestDataFactory.assignRoleRequest(
                user.getUserId(),
                List.of(" EMPLOYEE ", "", "EMPLOYEE")
        );

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleCodeIn(List.of(TestDataFactory.EMPLOYEE))).thenReturn(List.of(employee));
        when(userRoleRepository.findByUser(user)).thenReturn(List.of());

        userService.assignRoles(request);

        verify(roleRepository).findByRoleCodeIn(List.of(TestDataFactory.EMPLOYEE));
        verify(userRoleRepository).saveAll(any());
    }

    @Test
    void assignRolesRejectsNullRoleCodesAsMissingRoles() {

        User user = TestDataFactory.user();
        AssignRoleRequest request = TestDataFactory.assignRoleRequest(
                user.getUserId(),
                null
        );

        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));
        when(roleRepository.findByRoleCodeIn(List.of())).thenReturn(List.of());
        when(userRoleRepository.findByUser(user)).thenReturn(List.of());

        userService.assignRoles(request);

        verify(userRoleRepository).saveAll(List.of());
    }

    @Test
    void refreshTokenReturnsAccessTokenForValidRefreshToken() {

        User user = TestDataFactory.user();
        String rawRefreshToken = "refresh-token";
        RefreshToken refreshToken = TestDataFactory.refreshToken(user, rawRefreshToken);
        RefreshTokenRequest request = TestDataFactory.refreshTokenRequest(rawRefreshToken);

        when(refreshTokenRepository.findByTokenHash(TokenHashUtil.hash(rawRefreshToken)))
                .thenReturn(Optional.of(refreshToken));
        when(jwtUtil.extractEmail(rawRefreshToken)).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userRoleRepository.findByUser(user))
                .thenReturn(List.of(TestDataFactory.userRole(user, TestDataFactory.role(TestDataFactory.EMPLOYEE))));
        when(jwtUtil.generateAccessToken(user.getUserId(), user.getEmail(), List.of(TestDataFactory.EMPLOYEE)))
                .thenReturn("new-access-token");

        RefreshTokenResponse response = userService.refreshToken(request);

        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getTokenType()).isEqualTo("Bearer");
        verify(refreshTokenRepository, never()).save(any(RefreshToken.class));
    }

    @Test
    void refreshTokenRejectsRevokedToken() {

        User user = TestDataFactory.user();
        RefreshToken refreshToken = TestDataFactory.revokedRefreshToken(user, "refresh-token");

        when(refreshTokenRepository.findByTokenHash(TokenHashUtil.hash("refresh-token")))
                .thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> userService.refreshToken(TestDataFactory.refreshTokenRequest("refresh-token")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token revoked");
    }

    @Test
    void refreshTokenRejectsExpiredToken() {

        User user = TestDataFactory.user();
        RefreshToken refreshToken = TestDataFactory.expiredRefreshToken(user, "refresh-token");

        when(refreshTokenRepository.findByTokenHash(TokenHashUtil.hash("refresh-token")))
                .thenReturn(Optional.of(refreshToken));

        assertThatThrownBy(() -> userService.refreshToken(TestDataFactory.refreshTokenRequest("refresh-token")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Refresh token expired");
    }

    @Test
    void refreshTokenRejectsUnknownRefreshTokenHash() {

        when(refreshTokenRepository.findByTokenHash(TokenHashUtil.hash("missing-token")))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.refreshToken(TestDataFactory.refreshTokenRequest("missing-token")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void refreshTokenRejectsWhenUserNoLongerExists() {

        User user = TestDataFactory.user();
        RefreshToken refreshToken = TestDataFactory.refreshToken(user, "refresh-token");

        when(refreshTokenRepository.findByTokenHash(TokenHashUtil.hash("refresh-token")))
                .thenReturn(Optional.of(refreshToken));
        when(jwtUtil.extractEmail("refresh-token")).thenReturn(user.getEmail());
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.refreshToken(TestDataFactory.refreshTokenRequest("refresh-token")))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void logoutRevokesRefreshTokenAndClearsSecurityContext() {

        User user = TestDataFactory.user();
        RefreshToken refreshToken = TestDataFactory.refreshToken(user, "refresh-token");
        LogoutRequest request = TestDataFactory.logoutRequest("refresh-token");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken("user", null));
        when(refreshTokenRepository.findByTokenHash(TokenHashUtil.hash("refresh-token")))
                .thenReturn(Optional.of(refreshToken));

        userService.logout(request);

        assertThat(refreshToken.getRevoked()).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(refreshTokenRepository).save(refreshToken);
    }

    @Test
    void logoutRejectsUnknownRefreshToken() {

        when(refreshTokenRepository.findByTokenHash(TokenHashUtil.hash("missing-token")))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.logout(TestDataFactory.logoutRequest("missing-token")))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void changePasswordUpdatesPasswordAndRevokesRefreshTokens() {

        User user = TestDataFactory.user();
        RefreshToken refreshToken = TestDataFactory.refreshToken(user, "refresh-token");
        ChangePasswordRequest request = TestDataFactory.changePasswordRequest();

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TestDataFactory.PASSWORD, user.getPasswordHash())).thenReturn(true);
        when(passwordEncoder.encode(TestDataFactory.NEW_PASSWORD)).thenReturn("new-password-hash");
        when(refreshTokenRepository.findByUser(user)).thenReturn(List.of(refreshToken));

        userService.changePassword(request);

        assertThat(user.getPasswordHash()).isEqualTo("new-password-hash");
        assertThat(refreshToken.getRevoked()).isTrue();
        verify(userRepository).save(user);
        verify(refreshTokenRepository).saveAll(List.of(refreshToken));
    }

    @Test
    void changePasswordRejectsInvalidCurrentPassword() {

        User user = TestDataFactory.user();
        ChangePasswordRequest request = TestDataFactory.changePasswordRequest();

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TestDataFactory.PASSWORD, user.getPasswordHash())).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Current password invalid");
    }

    @Test
    void changePasswordRejectsMissingUser() {

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(TestDataFactory.EMAIL, null));
        when(userRepository.findByEmail(TestDataFactory.EMAIL)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(TestDataFactory.changePasswordRequest()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void changePasswordRejectsMismatchedConfirmation() {

        User user = TestDataFactory.user();
        ChangePasswordRequest request = TestDataFactory.changePasswordRequest();
        request.setConfirmPassword("different-password");

        SecurityContextHolder.getContext()
                .setAuthentication(new UsernamePasswordAuthenticationToken(user.getEmail(), null));
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(TestDataFactory.PASSWORD, user.getPasswordHash())).thenReturn(true);

        assertThatThrownBy(() -> userService.changePassword(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("Passwords do not match");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void searchUsersDelegatesToSearchBuilderAndMapsPageResponse() {

        UserSearchRequest request = new UserSearchRequest();
        request.setPage(0);
        request.setSize(10);
        User user = TestDataFactory.user();
        org.springframework.data.jpa.domain.Specification<User> specification = (root, query, builder) -> builder.conjunction();
        org.springframework.data.domain.Page<User> users =
                new org.springframework.data.domain.PageImpl<>(List.of(user));

        when(genericSearchBuilder.<User>build(request)).thenReturn(specification);
        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(users);

        var response = userService.searchUsers(request);

        assertThat(response.getContent())
                .singleElement()
                .extracting(UserResponse::getEmail)
                .isEqualTo(user.getEmail());
        assertThat(response.getTotalElements()).isEqualTo(1);
    }

}
