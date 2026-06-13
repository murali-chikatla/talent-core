package com.nexora.rsp.talentcore.api;

public final class OpenApiExamples {

    public static final String VALIDATION_ERROR = """
            {
              "email": "Invalid email format",
              "password": "Password must contain minimum 8 characters"
            }
            """;

    public static final String REGISTER_REQUEST = """
            {
              "employeeCode": "EMP001",
              "firstName": "Murali",
              "lastName": "Kumar",
              "email": "murali@test.com",
              "password": "Password@123"
            }
            """;

    public static final String LOGIN_REQUEST = """
            {
              "email": "murali@test.com",
              "password": "Password@123"
            }
            """;

    public static final String REFRESH_TOKEN_REQUEST = """
            {
              "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh"
            }
            """;

    public static final String CHANGE_PASSWORD_REQUEST = """
            {
              "currentPassword": "Password@123",
              "newPassword": "Newpass@123",
              "confirmPassword": "Newpass@123"
            }
            """;

    public static final String ASSIGN_ROLE_REQUEST = """
            {
              "userId": 1,
              "roleCodes": ["TEAM_MANAGER", "EMPLOYEE"]
            }
            """;

    public static final String USER_RESPONSE = """
            {
              "userId": 1,
              "employeeCode": "EMP001",
              "firstName": "Murali",
              "lastName": "Kumar",
              "email": "murali@test.com",
              "active": true,
              "roles": ["EMPLOYEE"]
            }
            """;

    public static final String LOGIN_RESPONSE = """
            {
              "accessToken": "eyJhbGciOiJIUzI1NiJ9.access",
              "refreshToken": "eyJhbGciOiJIUzI1NiJ9.refresh",
              "tokenType": "Bearer"
            }
            """;

    public static final String REFRESH_TOKEN_RESPONSE = """
            {
              "accessToken": "eyJhbGciOiJIUzI1NiJ9.access",
              "tokenType": "Bearer"
            }
            """;

    public static final String CURRENT_USER_RESPONSE = """
            {
              "email": "murali@test.com",
              "roles": ["EMPLOYEE"]
            }
            """;

    public static final String PAGE_USER_RESPONSE = """
            {
              "content": [
                {
                  "userId": 1,
                  "employeeCode": "EMP001",
                  "firstName": "Murali",
                  "lastName": "Kumar",
                  "email": "murali@test.com",
                  "active": true,
                  "roles": ["EMPLOYEE"]
                }
              ],
              "page": 0,
              "size": 10,
              "totalElements": 1,
              "totalPages": 1,
              "first": true,
              "last": true,
              "empty": false
            }
            """;

    public static final String AUTHENTICATION_REQUIRED = """
            {
              "timestamp": "2026-05-24T22:00:00",
              "status": 401,
              "message": "Authentication required"
            }
            """;

    public static final String ACCESS_DENIED = """
            {
              "timestamp": "2026-05-24T22:00:00",
              "status": 403,
              "message": "Access denied"
            }
            """;

    public static final String NOT_FOUND = """
            {
              "timestamp": "2026-05-24T22:00:00",
              "status": 404,
              "message": "User not found"
            }
            """;

    public static final String CONFLICT = """
            {
              "timestamp": "2026-05-24T22:00:00",
              "status": 409,
              "message": "Employee code already exists"
            }
            """;

    public static final String INTERNAL_ERROR = """
            {
              "timestamp": "2026-05-24T22:00:00",
              "status": 500,
              "message": "Internal server error"
            }
            """;

    private OpenApiExamples() {
    }
}
