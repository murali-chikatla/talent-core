package com.nexora.rsp.talentcore.execeptions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void handleResourceAlreadyExistsExceptionReturnsConflict() {

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceAlreadyExistsException(
                        new ResourceAlreadyExistsException("Email already exists")
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getMessage()).isEqualTo("Email already exists");
    }

    @Test
    void handleBindExceptionReturnsFieldErrors() {

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(
                new FieldError(
                        "request",
                        "email",
                        "Email is required"
                )
        );

        ResponseEntity<Map<String, String>> response =
                handler.handleBindException(new BindException(bindingResult));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).containsEntry("email", "Email is required");
    }

    @Test
    void handleTypeMismatchExceptionReturnsBadRequest() {

        MethodArgumentTypeMismatchException exception =
                new MethodArgumentTypeMismatchException(
                        "abc",
                        Long.class,
                        "userId",
                        null,
                        new TypeMismatchException("abc", Long.class)
                );

        ResponseEntity<ErrorResponse> response =
                handler.handleTypeMismatchException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid value for userId");
    }

    @Test
    void handleDataIntegrityViolationReturnsConflictWithResolvedConstraintMessage() {

        ResponseEntity<ErrorResponse> response =
                handler.handleDataAccessException(
                        new DataIntegrityViolationException(
                                "could not execute statement [constraint [uk_employee_code]]"
                        )
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Internal Server Error");
    }

    @Test
    void handleDataIntegrityViolationReturnsGenericMessageForUnknownConstraint() {

        ResponseEntity<ErrorResponse> response =
                handler.handleDataAccessException(
                        new DataRetrievalFailureException(
                                "could not execute statement [constraint [uk_unknown]]"
                        )
                );

        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Internal Server Error");
    }

    @Test
    void handleAuthenticationExceptionReturnsUnauthorized() {

        ResponseEntity<ErrorResponse> response =
                handler.handleAuthenticationException(
                        new BadCredentialsException("Bad credentials")
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getMessage()).isEqualTo("Authentication required");
    }

    @Test
    void handleAccessDeniedExceptionReturnsForbidden() {

        ResponseEntity<ErrorResponse> response =
                handler.handleAccessDeniedException(
                        new AccessDeniedException("Access Denied")
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getStatus()).isEqualTo(403);
        assertThat(response.getBody().getMessage()).isEqualTo("Access denied");
    }

    @Test
    void handleGenericExceptionReturnsInternalServerError() {

        ResponseEntity<ErrorResponse> response =
                handler.handleGenericException(new RuntimeException("Unexpected"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getMessage()).isEqualTo("Unexpected");
    }

    @Test
    void handleInvalidCredentialsExceptionReturnsUnauthorized() {

        ResponseEntity<ErrorResponse> response =
                handler.handleInvalidCredentialsException(
                        new InvalidCredentialsException("Invalid credentials")
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid credentials");
    }

    @Test
    void handleResourceNotFoundReturnsNotFound() {

        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(
                        new ResourceNotFoundException("User not found")
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("User not found");
    }

    @Test
    void handleUnauthorizedReturnsUnauthorized() {

        ResponseEntity<ErrorResponse> response =
                handler.handleUnauthorized(
                        new UnauthorizedException("Refresh token expired")
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getMessage()).isEqualTo("Refresh token expired");
    }
}
