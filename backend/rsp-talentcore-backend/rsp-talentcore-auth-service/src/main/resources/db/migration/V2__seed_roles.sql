INSERT INTO roles (role_code,
                   role_name,
                   role_description,
                   created_by)
VALUES ('SYSTEM_ADMIN',
        'System Admin',
        'Full platform administration',
        'SYSTEM'),

       ('ORGANIZATION_ADMIN',
        'Organization Admin',
        'Organization administration',
        'SYSTEM'),

       ('RESOURCE_MANAGER',
        'Resource Manager',
        'Manage resource allocation',
        'SYSTEM'),

       ('TEAM_MANAGER',
        'Team Manager',
        'Manage teams',
        'SYSTEM'),

       ('EMPLOYEE',
        'Employee',
        'Standard employee access',
        'SYSTEM'),

       ('VENDOR_MANAGER',
        'Vendor Manager',
        'Manage vendor organization',
        'SYSTEM'),

       ('VENDOR_RECRUITER',
        'Vendor Recruiter',
        'Manage recruitment activities',
        'SYSTEM');


CREATE TABLE refresh_tokens
(

    refresh_token_id BIGINT AUTO_INCREMENT
    PRIMARY KEY,

    user_id          BIGINT       NOT NULL,

    token_hash       VARCHAR(255) NOT NULL,

    expiry_date      TIMESTAMP    NOT NULL,

    revoked          BOOLEAN      NOT NULL DEFAULT FALSE,

    created_by       VARCHAR(100),

    created_ts       TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,

    modified_by      VARCHAR(100),

    modified_ts      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    is_deleted       BOOLEAN               DEFAULT FALSE,

    CONSTRAINT fk_refresh_user FOREIGN KEY (user_id) REFERENCES users (user_id),

    CONSTRAINT uk_token_hash UNIQUE (token_hash)

);
