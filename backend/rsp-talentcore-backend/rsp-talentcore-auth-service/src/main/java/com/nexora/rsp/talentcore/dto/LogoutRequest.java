package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Logout request for revoking a refresh token")
public class LogoutRequest {

    @NotBlank(
            message=
                    "Refresh token is required"
    )
    @Schema(description = "Refresh token to revoke", example = "eyJhbGciOiJIUzI1NiJ9.refresh", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

}
