package com.contexto.config;

import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "contexto.scanner")
public record ScannerProperties(
        long maxFileSizeBytes,
        Set<String> ignoredDirectories,
        Set<String> ignoredExtensions,
        List<String> ignoredFileGlobs
) {
    public ScannerProperties {
        ignoredDirectories = ignoredDirectories == null ? Set.of() : Set.copyOf(ignoredDirectories);
        ignoredExtensions = ignoredExtensions == null ? Set.of() : Set.copyOf(ignoredExtensions);
        ignoredFileGlobs = ignoredFileGlobs == null ? List.of() : List.copyOf(ignoredFileGlobs);
    }
}
