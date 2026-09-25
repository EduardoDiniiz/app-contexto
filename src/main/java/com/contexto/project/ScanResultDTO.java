package com.contexto.project;

public record ScanResultDTO(
        ProjectResponseDTO project,
        int added,
        int updated,
        int removed,
        int unchanged,
        int skipped,
        long durationMs
) {}
