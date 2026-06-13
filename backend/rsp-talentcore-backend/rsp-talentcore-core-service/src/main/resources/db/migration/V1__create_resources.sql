CREATE TABLE resources
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id VARCHAR(50) NOT NULL UNIQUE,

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100),

    email VARCHAR(255) NOT NULL UNIQUE,

    mobile VARCHAR(20),

    experience_years DECIMAL(5,2),

    primary_skill VARCHAR(100),

    status VARCHAR(30) NOT NULL,

    created_by VARCHAR(100),

    created_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    modified_by VARCHAR(100),

    modified_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    is_deleted BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_resource_employee_id
    ON resources(employee_id);

CREATE INDEX idx_resource_email
    ON resources(email);

CREATE INDEX idx_resource_status
    ON resources(status);

CREATE INDEX idx_resource_primary_skill
    ON resources(primary_skill);

INSERT INTO resources
(
    employee_id,
    first_name,
    last_name,
    email,
    experience_years,
    primary_skill,
    status
)
VALUES
    (
        'EMP001',
        'John',
        'Doe',
        'john.doe@test.com',
        8.0,
        'Java',
        'AVAILABLE'
    );