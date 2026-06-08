CREATE TABLE users (
    id                UUID         NOT NULL,
    mobile_number     VARCHAR(15)  NOT NULL,
    full_name         VARCHAR(255) NOT NULL,
    profile_photo_url TEXT,
    is_active         BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_users              PRIMARY KEY (id),
    CONSTRAINT uq_users_mobile_number UNIQUE (mobile_number)
);
