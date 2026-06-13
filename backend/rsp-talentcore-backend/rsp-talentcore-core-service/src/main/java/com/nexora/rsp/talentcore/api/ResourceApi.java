package com.nexora.rsp.talentcore.api;


import com.nexora.rsp.talentcore.dto.ResourceRequest;
import com.nexora.rsp.talentcore.dto.ResourceResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(
        name = "Resource Management",
        description = "APIs for managing workforce resources within TalentCore"
)
@RequestMapping("/api/resources")
public interface ResourceApi {

    @Operation(
            summary = "Create Resource",
            description = "Creates a new resource in the system."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource created successfully",
                    content = @Content(
                            schema = @Schema(implementation = ResourceResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Employee ID already exists"
            )
    })
    @PostMapping
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    ResourceResponse createResource(
            @Valid @RequestBody ResourceRequest request
    );


    @Operation(
            summary = "Get Resource By Id",
            description = "Returns resource details for the given resource id."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found"
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    ResourceResponse getResource(
            @Parameter(
                    description = "Unique Resource Identifier",
                    example = "1"
            )
            @PathVariable Long id
    );

    @Operation(
            summary = "Update Resource",
            description = "Updates an existing resource."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Resource updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request"
            )
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    ResourceResponse updateResource(
            @Parameter(
                    description = "Unique Resource Identifier",
                    example = "1"
            )
            @PathVariable Long id,

            @Valid @RequestBody ResourceRequest request
    );

    @Operation(
            summary = "Delete Resource",
            description = "Soft deletes a resource."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Resource deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Resource not found"
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','ORGANIZATION_ADMIN','RESOURCE_MANAGER')")
    void deleteResource(
            @Parameter(
                    description = "Unique Resource Identifier",
                    example = "1"
            )
            @PathVariable Long id
    );


}