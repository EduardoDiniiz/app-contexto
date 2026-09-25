-- Incremento 50 casa com o allocationSize da entidade e permite batch insert no Hibernate.
CREATE SEQUENCE project_file_seq INCREMENT BY 50;

CREATE TABLE project_file (
    id                BIGINT          PRIMARY KEY DEFAULT nextval('project_file_seq'),
    project_id        BIGINT          NOT NULL REFERENCES project(id) ON DELETE CASCADE,
    relative_path     VARCHAR(1024)   NOT NULL,
    extension         VARCHAR(50),
    language          VARCHAR(50),
    size_bytes        BIGINT          NOT NULL,
    content_hash      VARCHAR(64)     NOT NULL,
    content           TEXT            NOT NULL,
    -- Dicionário 'simple': não aplica stemming, adequado para identificadores de código.
    content_tsv       TSVECTOR        GENERATED ALWAYS AS (to_tsvector('simple', left(content, 500000))) STORED,
    last_modified_at  TIMESTAMP       NOT NULL,
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_project_file_path UNIQUE (project_id, relative_path)
);

CREATE INDEX idx_project_file_content_tsv ON project_file USING GIN (content_tsv);
CREATE INDEX idx_project_file_extension ON project_file (project_id, extension);
