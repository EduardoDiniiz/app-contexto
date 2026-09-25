package com.contexto.file;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.contexto.project.Project;
import com.contexto.scan.ScannedFile;

@Mapper(componentModel = "spring")
public interface ProjectFileMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", source = "project")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProjectFile toEntity(ScannedFile file, Project project);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "project", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ScannedFile file, @MappingTarget ProjectFile entity);

    ProjectFileContentDTO toContentDTO(ProjectFile entity);

    FileSearchResultDTO toSearchResultDTO(FileSearchHit hit);
}
