package com.contexto.file;

import lombok.Value;

@Value
public class FileSearchResultDTO {
    Long id;
    String relativePath;
    String language;
    Float rank;
    String snippet;
}
