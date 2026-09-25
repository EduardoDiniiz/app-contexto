package com.contexto.file;

public record FileSearchResultDTO(
        Long id,
        String relativePath,
        String language,
        Float rank,
        String snippet
) {}
