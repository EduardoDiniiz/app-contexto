package com.contexto.file;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class ProjectFileContentDTO {
    Long id;
    String relativePath;
    String language;
    Long sizeBytes;
    String contentHash;
    String content;
    LocalDateTime lastModifiedAt;
}
