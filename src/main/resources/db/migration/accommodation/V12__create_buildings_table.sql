CREATE TABLE buildings (
    id          UUID         NOT NULL,
    space_id    UUID         NOT NULL,
    name        VARCHAR(255) NOT NULL,
    code        VARCHAR(50),
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_buildings        PRIMARY KEY (id),
    CONSTRAINT fk_buildings_space FOREIGN KEY (space_id) REFERENCES spaces (id)
);

CREATE INDEX idx_buildings_space_id ON buildings (space_id);
CREATE INDEX idx_buildings_space_active ON buildings (space_id, is_active);
