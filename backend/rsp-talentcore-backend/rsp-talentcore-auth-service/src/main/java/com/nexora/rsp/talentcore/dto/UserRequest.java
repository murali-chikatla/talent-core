package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for creating a user account")
public class UserRequest {

    @NotBlank(message = "Employee code is required")
    @Schema(description = "Unique employee code assigned by the organization", example = "EMP001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String employeeCode;

    @NotBlank(message = "First name is required")
    @Schema(description = "User first name", example = "Murali", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "User last name", example = "Kumar")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Schema(description = "Unique login email address", example = "murali@test.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must contain minimum 8 characters")
    @Schema(description = "Initial password. Must contain at least 8 characters.", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
