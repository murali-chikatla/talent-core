package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Resource creation/update request")
@Getter
@Setter
public class ResourceRequest {

    @Schema(example = "EMP001")
    private String employeeId;

    @Schema(example = "Murali")
    private String firstName;

    @Schema(example = "Chikatla")
    private String lastName;

    @Schema(example = "murali@test.com")
    private String email;

    @Schema(example = "9876543210")
    private String mobile;

    @Schema(example = "8")
    private Double experienceYears;

    @Schema(example = "Java")
    private String primarySkill;

    @Schema(example = "ACTIVE")
    private String status;
}