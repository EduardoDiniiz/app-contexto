package com.contexto.project;

import lombok.Value;

@Value
public class ScanResultDTO {
    ProjectResponseDTO project;
    int added;
    int updated;
    int removed;
    int unchanged;
    int skipped;
    long durationMs;
}
