CREATE TABLE users
(
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    employee_code VARCHAR(50),

    first_name VARCHAR(100) NOT NULL,

    last_name VARCHAR(100),

    email VARCHAR(255) NOT NULL,

    password_hash VARCHAR(500) NOT NULL,

    user_status VARCHAR(30),

    created_by VARCHAR(100),

    created_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    modified_by VARCHAR(100),

    modified_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    is_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT uk_employee_code
        UNIQUE(employee_code),

    CONSTRAINT uk_user_email
        UNIQUE(email)
);


CREATE TABLE roles
(
    role_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    role_code VARCHAR(50),

    role_name VARCHAR(100) NOT NULL,

    role_description VARCHAR(500),

    created_by VARCHAR(100),

    created_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    modified_by VARCHAR(100),

    modified_ts TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    is_deleted BOOLEAN DEFAULT FALSE,

    CONSTRAINT uk_role_code
        UNIQUE(role_code)
);


CREATE TABLE user_roles
(
    user_role_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    user_id BIGINT NOT NULL,

    role_id BIGINT NOT NULL,

    CONSTRAINT fk_user
        FOREIGN KEY(user_id)
            REFERENCES users(user_id),

    CONSTRAINT fk_role
        FOREIGN KEY(role_id)
            REFERENCES roles(role_id),

    CONSTRAINT uk_user_role
        UNIQUE(user_id,role_id)
);
