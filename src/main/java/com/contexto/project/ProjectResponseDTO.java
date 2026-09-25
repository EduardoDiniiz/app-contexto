package com.contexto.project;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class ProjectResponseDTO {
    Long id;
    String name;
    String rootPath;
    Integer fileCount;
    Long totalBytes;
    LocalDateTime lastScannedAt;
    LocalDateTime createdAt;
}
