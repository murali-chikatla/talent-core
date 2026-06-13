package com.nexora.rsp.talentcore.api;

import com.nexora.rsp.talentcore.dto.PageResponse;
import com.nexora.rsp.talentcore.dto.ResourceResponse;
import com.nexora.rsp.talentcore.dto.ResourceSearchRequest;
import com.nexora.rsp.talentcore.execeptions.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.nexora.rsp.talentcore.api.OpenApiExamples.*;

@Tag(
        name = "Resource Search",
        description = "Authenticated resource search APIs with filter, pagination and sorting support"
)
@RequestMapping("/api/resources")
public interface ResourceSearchApi {

    @Operation(
            summary = "Search resources",
            description = """
                    Searches workforce resources using optional filters.
                    
                    Validation notes:
                    - page must be zero or greater
                    - size must be between 1 and 100
                    - sortDirection must be ASC or DESC
                    
                    Supported filters:
                    - employeeId, firstName, lastName, email and primarySkill use LIKE matching
                    - experienceYears and status use exact matching
                    - null, empty and blank values are ignored
                    """,
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resources fetched successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(value = PAGE_RESOURCE_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination, sort or filter request",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "Referenced resource data not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    ResponseEntity<PageResponse<ResourceResponse>> searchResources(
            @Parameter(description = "Search filters, pagination and sorting options")
            @Valid @ParameterObject ResourceSearchRequest request
    );
}
