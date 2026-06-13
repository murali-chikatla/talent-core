package com.nexora.rsp.talentcore.service.impl;


import com.nexora.rsp.talentcore.config.UserPrincipal;
import com.nexora.rsp.talentcore.domain.*;
import com.nexora.rsp.talentcore.dto.*;
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
import com.nexora.rsp.talentcore.service.UserService;
import com.nexora.rsp.talentcore.util.JwtUtil;
import com.nexora.rsp.talentcore.util.PaginationUtil;
import com.nexora.rsp.talentcore.util.TokenHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleRepository roleRepository;
    private final GenericSearchBuilder genericSearchBuilder;
    private final UserMapper userMapper;


    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public UserResponse registerUser(UserRequest request) {

        throwWhen(
                userRepository.existsByEmail(request.getEmail()),
                () -> new ResourceAlreadyExistsException("Email already exists")
        );

        User user = new User();

        user.setEmployeeCode(request.getEmployeeCode());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());

        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setUserStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        log.info(
                "User registered successfully for email={}",
                savedUser.getEmail()
        );

        return userMapper.toResponse(savedUser, List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<UserResponse> searchUsers(UserSearchRequest request) {

        Pageable pageable = PaginationUtil.toPageable(request);

        Specification<User> specification =
                genericSearchBuilder.build(request);

        Page<User> users =
                userRepository.findAll(
                        specification,
                        pageable
                );

        return PaginationUtil.toPageResponse(
                users.map(userMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public void assignRoles(AssignRoleRequest request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<String> roleCodes = normalizeRoleCodes(request.getRoleCodes());
        List<Role> roles = roleRepository.findByRoleCodeIn(roleCodes);

        Set<String> resolvedRoleCodes = roles.stream()
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());

        List<String> missingRoleCodes = roleCodes.stream()
                .filter(roleCode -> !resolvedRoleCodes.contains(roleCode))
                .toList();

        throwWhen(
                !missingRoleCodes.isEmpty(),
                () -> new ResourceNotFoundException(
                        "Role not found: " + String.join(", ", missingRoleCodes)
                )
        );

        Set<String> assignedRoleCodes = userRoleRepository.findByUser(user)
                .stream()
                .map(UserRole::getRole)
                .filter(Objects::nonNull)
                .map(Role::getRoleCode)
                .collect(Collectors.toSet());

        List<UserRole> userRoles = roles.stream()
                .filter(role -> !assignedRoleCodes.contains(role.getRoleCode()))
                .map(role -> createUserRole(user, role))
                .toList();

        userRoleRepository.saveAll(userRoles);

        log.info(
                "Role assignment completed for userId={} requestedRoleCount={} assignedRoleCount={}",
                user.getUserId(),
                roleCodes.size(),
                userRoles.size()
        );
    }


    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> invalidCredentials(request.getEmail()));

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        throwWhen(
                !passwordMatches,
                () -> invalidCredentials(request.getEmail())
        );

        List<String> roles = userRoleRepository.findByUser(user).stream().map(userRole -> userRole.getRole().getRoleCode()).toList();

        String token = jwtUtil.generateAccessToken(user.getUserId(),user.getEmail(), roles);
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(),user.getEmail());
        refreshTokenRepository.save(
                createRefreshToken(
                        user,
                        refreshToken
                )
        );

        log.info(
                "User login successful for email={}",
                user.getEmail()
        );
        log.info(
                "Refresh token generated for userId={}",
                user.getUserId()
        );

        return LoginResponse.builder().accessToken(token).refreshToken(refreshToken).tokenType("Bearer").build();

    }

    @Override
    @Transactional
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken =request.getRefreshToken();
        String tokenHash = TokenHashUtil.hash(refreshToken);

        RefreshToken tokenEntity =
                refreshTokenRepository.findByTokenHash(tokenHash).orElseThrow(() ->
                                refreshTokenRejected("Invalid refresh token"));


        throwWhen(
                Boolean.TRUE.equals(
                        tokenEntity.getRevoked()
                ),
                () -> refreshTokenRejected("Refresh token revoked")
        );

        throwWhen(
                tokenEntity.getExpiryDate().isBefore(LocalDateTime.now()),
                () -> refreshTokenRejected("Refresh token expired")
        );


        String email = jwtUtil.extractEmail(refreshToken);


        User user =userRepository.findByEmail(email).orElseThrow( () ->new ResourceNotFoundException("User not found"));


        List<String> roles =userRoleRepository.findByUser(user)
                        .stream()
                        .map(
                                userRole ->
                                        userRole.getRole().getRoleCode()

                        )
                        .toList();

        String accessToken =jwtUtil.generateAccessToken(
                        user.getUserId(),
                        user.getEmail(),
                        roles);

        log.info(
                "Access token refreshed for userId={}",
                user.getUserId()
        );

        return RefreshTokenResponse.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    @Transactional
    public void logout(LogoutRequest request){

        String tokenHash =TokenHashUtil.hash(request.getRefreshToken());
        RefreshToken refreshToken =  refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(()->refreshTokenRejected("Invalid refresh token") );
        refreshToken.setRevoked( true );
        refreshTokenRepository .save(refreshToken);
        log.info(
                "Refresh token revoked for refreshTokenId={}",
                refreshToken.getRefreshTokenId()
        );
        SecurityContextHolder.clearContext();

    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        String email =authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(  () ->new ResourceNotFoundException("User not found"));


        throwWhen(
                !passwordEncoder.matches(
                        request.getCurrentPassword(),
                        user.getPasswordHash()
                ),
                () -> {
                    log.warn(
                            "Password change rejected because current password is invalid for userId={}",
                            user.getUserId()
                    );
                    return new UnauthorizedException("Current password invalid");
                }
        );

        throwWhen(
                !request.getNewPassword().equals(
                        request.getConfirmPassword()
                ),
                () -> {
                    log.warn(
                            "Password change rejected because confirmation does not match for userId={}",
                            user.getUserId()
                    );
                    return new UnauthorizedException("Passwords do not match");
                }
        );


        user.setPasswordHash( passwordEncoder.encode( request.getNewPassword()));

        userRepository.save(user);

        List<RefreshToken> tokens = refreshTokenRepository.findByUser(user);
        tokens.forEach(token -> token.setRevoked(true));

        refreshTokenRepository.saveAll(tokens);

        log.info(
                "Password changed for userId={}",
                user.getUserId()
        );

    }


    @Override
    public CurrentUserResponse  getCurrentUser() {
        Authentication authentication = SecurityContextHolder
                        .getContext()
                        .getAuthentication();
        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();


        User user = userRepository
                .findByEmail(principal.getEmail())
                .orElse(null);

        throwWhen(
                user == null,
                () -> {
                    log.warn(
                            "Current user not found for email={}",
                            principal.getEmail()
                    );
                    return new ResourceNotFoundException("User not found");
                }
        );

        return CurrentUserResponse
                .builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(
                        user.getEmail()
                )
                .roles(
                        principal.getRoles()
                )
                .build();

    }

    private void throwWhen(boolean condition, Supplier<? extends RuntimeException> exceptionSupplier) {

        Stream.of(condition)
                .filter(Boolean::booleanValue)
                .map(ignored -> exceptionSupplier.get())
                .forEach(exception -> {
                    throw exception;
                });
    }

    private List<String> normalizeRoleCodes(List<String> roleCodes) {

        return Stream.ofNullable(roleCodes)
                .flatMap(Collection::stream)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(Predicate.not(String::isBlank))
                .distinct()
                .toList();
    }

    private UserRole createUserRole(User user, Role role) {

        UserRole userRole = new UserRole();

        userRole.setUser(user);
        userRole.setRole(role);

        return userRole;
    }

    private InvalidCredentialsException invalidCredentials(String email) {

        log.warn(
                "Invalid login attempt for email={}",
                email
        );

        return new InvalidCredentialsException("Invalid credentials");
    }

    private UnauthorizedException refreshTokenRejected(String message) {

        log.warn(
                "Refresh token request rejected reason={}",
                message
        );

        return new UnauthorizedException(message);
    }

    private RefreshToken createRefreshToken(User user, String refreshToken) {

        RefreshToken refreshTokenEntity = new RefreshToken();

        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setTokenHash(TokenHashUtil.hash(refreshToken));
        refreshTokenEntity.setRevoked(false);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(30));

        return refreshTokenEntity;
    }
}
