package com.contexto.file;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class ProjectFileSummaryDTO {
    Long id;
    String relativePath;
    String extension;
    String language;
    Long sizeBytes;
    LocalDateTime lastModifiedAt;
}
