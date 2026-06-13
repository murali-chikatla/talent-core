package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "JWT login response")
public class LoginResponse {

    @Schema(description = "Bearer access token used for authenticated API calls", example = "eyJhbGciOiJIUzI1NiJ9.access")
    private String accessToken;

    @Schema(description = "Refresh token used to request a new access token", example = "eyJhbGciOiJIUzI1NiJ9.refresh")
    private String refreshToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;
}
