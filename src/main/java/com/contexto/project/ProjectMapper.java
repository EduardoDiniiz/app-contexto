package com.contexto.project;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.contexto.scan.SyncResult;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectResponseDTO toResponseDTO(Project project);

    @Mapping(target = "project", source = "project")
    ScanResultDTO toScanResultDTO(Project project, SyncResult result, long durationMs);
}
