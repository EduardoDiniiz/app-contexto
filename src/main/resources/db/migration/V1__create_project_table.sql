CREATE TABLE project (
    id               BIGSERIAL       PRIMARY KEY,
    name             VARCHAR(255)    NOT NULL,
    root_path        VARCHAR(1024)   NOT NULL UNIQUE,
    file_count       INTEGER         NOT NULL DEFAULT 0,
    total_bytes      BIGINT          NOT NULL DEFAULT 0,
    last_scanned_at  TIMESTAMP,
    created_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);
