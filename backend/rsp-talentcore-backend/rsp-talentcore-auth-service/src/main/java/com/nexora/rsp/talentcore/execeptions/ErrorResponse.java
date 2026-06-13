package com.nexora.rsp.talentcore.execeptions;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Schema(description = "Standard error response returned by the auth service")
public class ErrorResponse {

    @Schema(description = "Error timestamp in service local time", example = "2026-05-24T22:00:00")
    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code", example = "409")
    private Integer status;

    @Schema(description = "Safe business error message", example = "Employee code already exists")
    private String message;
}
