package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "Role assignment request")
public class AssignRoleRequest {

    @NotNull(message = "User id is required")
    @Schema(description = "User id that will receive the roles", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @NotEmpty(message = "Role codes are required")
    @Schema(description = "Role codes to assign. Blank role codes are rejected.", example = "[\"TEAM_MANAGER\", \"EMPLOYEE\"]", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<@NotBlank(message = "Role code is required") String> roleCodes;
}
