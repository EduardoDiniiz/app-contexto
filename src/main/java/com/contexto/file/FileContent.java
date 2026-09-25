package com.contexto.file;

import lombok.Value;

@Value
public class FileContent {
    Long id;
    String relativePath;
    String language;
    String content;
}
