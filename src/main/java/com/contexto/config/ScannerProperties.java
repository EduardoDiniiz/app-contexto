package com.contexto.config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "contexto.scanner")
public class ScannerProperties {

    private long maxFileSizeBytes = 524_288;
    private Set<String> ignoredDirectories = new HashSet<>();
    private Set<String> ignoredExtensions = new HashSet<>();
    private List<String> ignoredFileGlobs = new ArrayList<>();
}
