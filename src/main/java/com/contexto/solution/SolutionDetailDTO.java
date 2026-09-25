package com.contexto.solution;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Value;

@Value
public class SolutionDetailDTO {
    Long id;
    Long projectId;
    String projectName;
    String title;
    String request;
    String summary;
    String model;
    String contextExtensions;
    String contextQuery;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<SolutionFileDTO> files;
}
