package com.contexto.file;

import java.time.LocalDateTime;

public record ProjectFileSummaryDTO(
        Long id,
        String relativePath,
        String extension,
        String language,
        Long sizeBytes,
        LocalDateTime lastModifiedAt
) {}
