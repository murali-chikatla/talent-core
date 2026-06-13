package com.nexora.rsp.talentcore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageRequestDto {

    @Min(value = 0, message = "Page index must be zero or greater")
    @Schema(description = "Zero-based page index", example = "0", defaultValue = "0")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must not exceed 100")
    @Schema(description = "Number of records per page", example = "10", defaultValue = "10")
    private Integer size = 10;

    @Schema(description = "Entity field used for sorting", example = "id", defaultValue = "id")
    private String sortBy = "id";

    @Pattern(regexp = "(?i)^\\s*(asc|desc)?\\s*$", message = "Sort direction must be ASC or DESC")
    @Schema(description = "Sort direction", example = "ASC", defaultValue = "ASC", allowableValues = {"ASC", "DESC"})
    private String sortDirection = "ASC";
}
