package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request payload for issuing a new access token")
public class RefreshTokenRequest {

    @NotBlank(
            message=
                    "Refresh token is required"
    )
    @Schema(description = "Refresh token returned by the login API", example = "eyJhbGciOiJIUzI1NiJ9.refresh", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;

}
