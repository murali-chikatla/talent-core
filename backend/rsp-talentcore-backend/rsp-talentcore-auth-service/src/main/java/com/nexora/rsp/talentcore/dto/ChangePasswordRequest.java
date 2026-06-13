package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Password change request for the authenticated user")
public class ChangePasswordRequest {

    @NotBlank(message = "Current password required")
    @Schema(description = "Current password for verification", example = "Password@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    @NotBlank(message = "New password required")
    @Size(min = 8, max = 20)
    @Schema(description = "New password. Must contain between 8 and 20 characters.", example = "Newpass@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;


    @NotBlank(message = "Confirm password required")
    @Schema(description = "Confirmation value that must match the new password", example = "Newpass@123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String confirmPassword;
}
