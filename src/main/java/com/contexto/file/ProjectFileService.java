package com.contexto.file;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contexto.common.exception.ResourceNotFoundException;
import com.contexto.project.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectFileService {

    private final ProjectFileRepository fileRepository;
    private final ProjectFileMapper fileMapper;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public List<ProjectFileSummaryDTO> listFiles(Long projectId) {
        projectService.requireExists(projectId);
        return fileRepository.findSummariesByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public ProjectFileContentDTO findFile(Long projectId, Long fileId) {
        return fileRepository.findByIdAndProjectId(fileId, projectId)
                .map(fileMapper::toContentDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo", fileId));
    }

    @Transactional(readOnly = true)
    public List<FileSearchResultDTO> search(Long projectId, String query, int limit) {
        projectService.requireExists(projectId);
        return fileRepository.search(projectId, query.trim(), limit).stream()
                .map(fileMapper::toSearchResultDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<FileContent> findContents(Collection<Long> fileIds) {
        return fileRepository.findContentsByIdIn(fileIds);
    }
}
