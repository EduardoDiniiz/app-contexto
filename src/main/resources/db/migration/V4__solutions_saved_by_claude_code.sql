-- As soluções agora são geradas no Claude Code e salvas prontas: não há mais processamento
-- assíncrono, status, erro nem contagem de tokens da API.
ALTER TABLE solution
    DROP COLUMN status,
    DROP COLUMN error_message,
    DROP COLUMN raw_output,
    DROP COLUMN input_tokens,
    DROP COLUMN cache_read_tokens,
    DROP COLUMN output_tokens,
    DROP COLUMN duration_ms,
    DROP COLUMN completed_at,
    DROP COLUMN context_max_chars;

-- Soluções que falharam na versão anterior não têm resumo e não servem mais.
DELETE FROM solution WHERE summary IS NULL;
ALTER TABLE solution ALTER COLUMN summary SET NOT NULL;
