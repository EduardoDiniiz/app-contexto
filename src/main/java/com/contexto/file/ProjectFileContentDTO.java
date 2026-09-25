package com.contexto.file;

import java.time.LocalDateTime;

public record ProjectFileContentDTO(
        Long id,
        String relativePath,
        String language,
        Long sizeBytes,
        String contentHash,
        String content,
        LocalDateTime lastModifiedAt
) {}
