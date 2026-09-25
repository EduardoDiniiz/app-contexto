package com.contexto.project;

import java.time.LocalDateTime;

public record ProjectResponseDTO(
        Long id,
        String name,
        String rootPath,
        Integer fileCount,
        Long totalBytes,
        LocalDateTime lastScannedAt,
        LocalDateTime createdAt
) {}
