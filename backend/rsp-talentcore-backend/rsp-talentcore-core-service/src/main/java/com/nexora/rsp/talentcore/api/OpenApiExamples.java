package com.nexora.rsp.talentcore.api;

public final class OpenApiExamples {

    public static final String VALIDATION_ERROR = """
            {
              "page": "Page index must be zero or greater",
              "size": "Page size must not exceed 100"
            }
            """;

    public static final String PAGE_RESOURCE_RESPONSE = """
            {
              "content": [
                {
                  "id": 1,
                  "employeeId": "EMP001",
                  "firstName": "Murali",
                  "lastName": "Kumar",
                  "email": "murali@test.com",
                  "primarySkill": "Java",
                  "status": "ACTIVE",
                  "mobile": "9533714520",
                  "experienceYears": 8.0
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
              "message": "Resource not found"
            }
            """;

    public static final String CONFLICT = """
            {
              "timestamp": "2026-05-24T22:00:00",
              "status": 409,
              "message": "Employee ID already exists"
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
