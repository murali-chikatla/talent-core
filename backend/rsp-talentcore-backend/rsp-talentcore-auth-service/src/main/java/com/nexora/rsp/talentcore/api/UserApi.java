package com.nexora.rsp.talentcore.api;

import com.nexora.rsp.talentcore.dto.*;
import com.nexora.rsp.talentcore.execeptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.nexora.rsp.talentcore.api.OpenApiExamples.*;

@Tag(
        name = "Authentication and User Management",
        description = "User registration, login, token lifecycle, current-user and role-assignment APIs"
)
@RequestMapping("/api/users")
public interface UserApi {

    @Operation(
            summary = "Register user",
            description = """
                    Creates a user account.
                    
                    Validation notes:
                    - employeeCode, firstName, email and password are required
                    - email must be valid and unique
                    - employeeCode must be unique
                    - password must contain at least 8 characters
                    """,
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @PostMapping("/register")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully",
                    content = @Content(schema = @Schema(implementation = UserResponse.class),
                            examples = @ExampleObject(value = USER_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "Validation failure",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "Referenced business resource was not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Unique business constraint violation",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })
    ResponseEntity<UserResponse> registerUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = UserRequest.class),
                            examples = @ExampleObject(value = REGISTER_REQUEST)))
            @Valid @RequestBody UserRequest request);

    @Operation(summary = "Login user", description = """
            Authenticates the user and returns JWT token.
            
            Business rules:
            - Email must exist
            - Password must match
            - Returns access and refresh tokens on success
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(value = LOGIN_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Invalid email or password",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Forbidden access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })
    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Login request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(value = LOGIN_REQUEST)))
            @Valid @RequestBody LoginRequest request);



    @Operation(
            summary = "Refresh access token",
            description =
                    "Generates a new access token using a valid, non-revoked, non-expired refresh token."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token generated successfully",
                    content = @Content(schema = @Schema(implementation = RefreshTokenResponse.class),
                            examples = @ExampleObject(value = REFRESH_TOKEN_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "Refresh token is missing",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Refresh token invalid, expired or revoked",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Forbidden access",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })
    @PostMapping("/refresh-token")
    ResponseEntity<RefreshTokenResponse> refreshToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class),
                            examples = @ExampleObject(value = REFRESH_TOKEN_REQUEST)))
            @Valid @RequestBody RefreshTokenRequest request);


    @Operation(
            summary = "Logout user",
            description =
                    "Revokes the supplied refresh token and clears the current security context.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Logout successful"),
            @ApiResponse(responseCode = "400", description = "Refresh token is missing",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Authentication required or invalid refresh token",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })
    @PostMapping(   "/logout"  )

    ResponseEntity<Void> logout(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token revocation request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LogoutRequest.class),
                            examples = @ExampleObject(value = REFRESH_TOKEN_REQUEST)))
            @Valid @RequestBody LogoutRequest request );




    @Operation(
            summary =
                    "Change password",
            description =
                    "Changes the authenticated user's password and revokes existing refresh tokens.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )

    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Authentication required or current password invalid",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })

    @PostMapping(
            "/change-password"
    )

    ResponseEntity<Void>
    changePassword(

            @Valid

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Password change request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ChangePasswordRequest.class),
                            examples = @ExampleObject(value = CHANGE_PASSWORD_REQUEST)))
            @RequestBody

            ChangePasswordRequest
                    request
    );


    @Operation(
            summary =
                    "Get current user",
            description =
                    "Returns the current authenticated user's email and role codes.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )

    @ApiResponses({
            @ApiResponse(responseCode="200", description= "User fetched successfully",
                    content = @Content(schema = @Schema(implementation = CurrentUserResponse.class),
                            examples = @ExampleObject(value = CURRENT_USER_RESPONSE))),
            @ApiResponse(responseCode="400", description= "Invalid request",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode="401", description= "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode="403", description= "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode="404", description= "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode="409", description= "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode="500", description= "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })

    @GetMapping(
            "/current"
    )

    ResponseEntity<
            CurrentUserResponse
            >

    getCurrentUser();


    @Operation(
            summary = "Assign roles",
            description = """
                    Assigns one or more roles to a user.
                    
                    Validation notes:
                    - userId is required
                    - at least one non-blank role code is required
                    - existing user-role mappings are ignored
                    """,
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Roles assigned successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Authenticated user does not have permission",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "User or role not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Duplicate role assignment",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })
    @PostMapping("/roles")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    ResponseEntity<Void> assignRoles(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Role assignment request",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AssignRoleRequest.class),
                            examples = @ExampleObject(value = ASSIGN_ROLE_REQUEST)))
            @Valid @RequestBody AssignRoleRequest request
    );



}
