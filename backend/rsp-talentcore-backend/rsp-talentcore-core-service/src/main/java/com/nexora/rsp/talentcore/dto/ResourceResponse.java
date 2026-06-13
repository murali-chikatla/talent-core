package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Resource response")
@Setter
@Getter
public class ResourceResponse {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "EMP001")
    private String employeeId;

    @Schema(example = "Murali")
    private String firstName;

    @Schema(example = "Chikatla")
    private String lastName;


    @Schema(example = "email")
    private String email;

    @Schema(example = "Java")
    private String primarySkill;

    @Schema(example = "AVAILABLE")
    private String status;

    @Schema(example = "9533714520")
    private String mobile;

    @Schema(example = "experienceYears")
    private Double experienceYears;





}