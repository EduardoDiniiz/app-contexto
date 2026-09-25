package com.contexto.scan;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class ScannedFile {
    String relativePath;
    String extension;
    String language;
    long sizeBytes;
    String contentHash;
    String content;
    LocalDateTime lastModifiedAt;
}
