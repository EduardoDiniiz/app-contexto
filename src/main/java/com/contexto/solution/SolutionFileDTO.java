package com.contexto.solution;

import lombok.Value;

@Value
public class SolutionFileDTO {
    Long id;
    String path;
    FileAction action;
    String language;
    String description;
    String content;
}
