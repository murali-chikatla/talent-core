package com.nexora.rsp.talentcore.api;

import com.nexora.rsp.talentcore.dto.PageResponse;
import com.nexora.rsp.talentcore.dto.UserResponse;
import com.nexora.rsp.talentcore.dto.UserSearchRequest;
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
        name = "User Search",
        description = "Authenticated user search APIs with filter, pagination and sorting support"
)
@RequestMapping("/api/users")
public interface UserSearchApi {

    @Operation(
            summary = "Search users",
            description = """
                    Searches enterprise users using optional filters.
                    
                    Validation notes:
                    - page must be zero or greater
                    - size must be between 1 and 100
                    - sortDirection must be ASC or DESC
                    
                    Supported filters:
                    - firstName, lastName and email use LIKE matching
                    - active and roleCode use exact matching
                    - null, empty and blank values are ignored
                    """,
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users fetched successfully",
                    content = @Content(schema = @Schema(implementation = PageResponse.class),
                            examples = @ExampleObject(value = PAGE_USER_RESPONSE))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination, sort or filter request",
                    content = @Content(examples = @ExampleObject(value = VALIDATION_ERROR))),
            @ApiResponse(responseCode = "401", description = "Authentication required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = AUTHENTICATION_REQUIRED))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = ACCESS_DENIED))),
            @ApiResponse(responseCode = "404", description = "Referenced user data not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = NOT_FOUND))),
            @ApiResponse(responseCode = "409", description = "Conflict",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = CONFLICT))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = INTERNAL_ERROR)))
    })
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    @GetMapping("/search")
    ResponseEntity<PageResponse<UserResponse>> searchUsers(
            @Parameter(description = "Search filters, pagination and sorting options")
            @Valid @ParameterObject UserSearchRequest request
    );


}
