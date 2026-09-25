-- Soluções geradas pelo Claude. Sobrevivem à exclusão do índice do projeto (project_id vira NULL).
CREATE TABLE solution (
    id                   BIGSERIAL       PRIMARY KEY,
    project_id           BIGINT          REFERENCES project(id) ON DELETE SET NULL,
    project_name         VARCHAR(255)    NOT NULL,
    title                VARCHAR(255)    NOT NULL,
    request              TEXT            NOT NULL,
    context_extensions   VARCHAR(500),
    context_query        VARCHAR(500),
    context_max_chars    INTEGER         NOT NULL,
    status               VARCHAR(20)     NOT NULL,
    summary              TEXT,
    error_message        TEXT,
    raw_output           TEXT,
    model                VARCHAR(100),
    input_tokens         BIGINT,
    cache_read_tokens    BIGINT,
    output_tokens        BIGINT,
    duration_ms          BIGINT,
    completed_at         TIMESTAMP,
    created_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_solution_project_created ON solution (project_id, created_at DESC);
CREATE INDEX idx_solution_status ON solution (status);

CREATE TABLE solution_file (
    id            BIGSERIAL       PRIMARY KEY,
    solution_id   BIGINT          NOT NULL REFERENCES solution(id) ON DELETE CASCADE,
    position      INTEGER         NOT NULL,
    path          VARCHAR(1024)   NOT NULL,
    action        VARCHAR(20)     NOT NULL,
    language      VARCHAR(50),
    description   TEXT,
    content       TEXT            NOT NULL
);

CREATE INDEX idx_solution_file_solution ON solution_file (solution_id, position);
