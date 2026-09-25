package com.contexto.solution;

import java.time.LocalDateTime;

import lombok.Value;

@Value
public class SolutionSummaryDTO {
    Long id;
    Long projectId;
    String projectName;
    String title;
    Integer fileCount;
    String model;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
