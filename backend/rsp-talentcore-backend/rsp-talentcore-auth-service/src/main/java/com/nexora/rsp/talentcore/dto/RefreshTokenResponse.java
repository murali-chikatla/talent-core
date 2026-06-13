package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Schema(description = "Access token response generated from a valid refresh token")
public class RefreshTokenResponse {

    @Schema(description = "New access token", example = "eyJhbGciOiJIUzI1NiJ9.access")
    private String accessToken;

    @Schema(description = "Token type", example = "Bearer")
    private String tokenType;

}
