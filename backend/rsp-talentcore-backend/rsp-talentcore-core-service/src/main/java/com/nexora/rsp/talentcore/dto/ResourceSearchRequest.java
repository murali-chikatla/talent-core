package com.nexora.rsp.talentcore.dto;

import com.nexora.rsp.talentcore.enums.ResourceStatus;
import com.nexora.rsp.talentcore.search.SearchField;
import com.nexora.rsp.talentcore.search.SearchOperation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(description = "Resource search filters and pagination request")
public class ResourceSearchRequest extends PageRequestDto {

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional employee id filter. Uses case-insensitive LIKE matching.", example = "EMP001")
    private String employeeId;

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional first name filter. Uses case-insensitive LIKE matching.", example = "murali")
    private String firstName;

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional last name filter. Uses case-insensitive LIKE matching.", example = "kumar")
    private String lastName;

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional email filter. Uses case-insensitive LIKE matching.", example = "test@gmail.com")
    private String email;

    @SearchField
    @Schema(description = "Optional exact experience years filter", example = "8.00")
    private BigDecimal experienceYears;

    @SearchField(operation = SearchOperation.LIKE)
    @Schema(description = "Optional primary skill filter. Uses case-insensitive LIKE matching.", example = "Java")
    private String primarySkill;

    @SearchField
    @Schema(description = "Optional exact resource status filter", example = "ACTIVE")
    private ResourceStatus status;
}
