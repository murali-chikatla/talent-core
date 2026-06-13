package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@Schema(description = "Authenticated user profile")
public class CurrentUserResponse {

    @Schema(description = "Authenticated user first Name", example = "Mahesh")
    private String firstName;

    @Schema(description = "Authenticated user last Name", example = "Kumar")
    private String lastName;

    @Schema(description = "Authenticated user email", example = "murali@test.com")
    private String email;

    @Schema(description = "Role codes assigned to the authenticated user", example = "[\"EMPLOYEE\"]")
    private List<String> roles;
}
