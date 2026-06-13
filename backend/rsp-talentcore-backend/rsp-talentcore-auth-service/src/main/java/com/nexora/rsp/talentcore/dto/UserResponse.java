package com.nexora.rsp.talentcore.dto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "User account response")
public class UserResponse {

    @Schema(description = "User identifier", example = "1")
    private Long userId;

    @Schema(description = "Unique employee code", example = "EMP001")
    private String employeeCode;

    @Schema(description = "User first name", example = "Murali")
    private String firstName;

    @Schema(description = "User last name", example = "Kumar")
    private String lastName;

    @Schema(description = "User email address", example = "murali@test.com")
    private String email;

    @Schema(description = "Whether the user account is active", example = "true")
    private Boolean active;

    @Schema(description = "Assigned role codes", example = "[\"EMPLOYEE\"]")
    private List<String> roles;
}
