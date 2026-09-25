package com.contexto.solution;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SolutionMapper {

    @Mapping(target = "projectId", source = "project.id")
    SolutionDetailDTO toDetailDTO(Solution solution);

    SolutionFileDTO toFileDTO(SolutionFile file);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "solution", ignore = true)
    @Mapping(target = "position", ignore = true)
    @Mapping(target = "path", source = "path", qualifiedByName = "normalizePath")
    @Mapping(target = "content",
            expression = "java(dto.getAction() == FileAction.DELETE || dto.getContent() == null ? \"\" : dto.getContent())")
    SolutionFile toFileEntity(SolutionFileRequestDTO dto);

    // @Named: sem ele o MapStruct usaria este método para converter toda String do mapper.
    @Named("normalizePath")
    default String normalizePath(String path) {
        return path.trim().replace('\\', '/').replaceFirst("^\\./", "");
    }
}
