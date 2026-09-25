package com.contexto.scan;

import java.time.LocalDateTime;

public record ScannedFile(
        String relativePath,
        String extension,
        String language,
        long sizeBytes,
        String contentHash,
        String content,
        LocalDateTime lastModifiedAt
) {}
