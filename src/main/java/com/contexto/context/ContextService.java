package com.contexto.context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contexto.file.FileContent;
import com.contexto.file.FileSearchResultDTO;
import com.contexto.file.ProjectFileService;
import com.contexto.file.ProjectFileSummaryDTO;
import com.contexto.project.ProjectResponseDTO;
import com.contexto.project.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContextService {

    private static final int FETCH_CHUNK_SIZE = 50;
    private static final int SEARCH_LIMIT = 100;

    private final ProjectService projectService;
    private final ProjectFileService fileService;
    private final FileTreeRenderer treeRenderer;

    @Transactional(readOnly = true)
    public String buildContext(Long projectId, Set<String> extensions, String query, int maxChars) {
        ProjectResponseDTO project = projectService.findById(projectId);
        List<ProjectFileSummaryDTO> files = filterByExtension(fileService.listFiles(projectId), extensions);
        String normalizedQuery = query == null || query.isBlank() ? null : query.trim();

        String tree = treeRenderer.render(files.stream().map(ProjectFileSummaryDTO::relativePath).toList());
        ContextDocumentWriter writer = new ContextDocumentWriter(project, tree, normalizedQuery, maxChars);
        List<ProjectFileSummaryDTO> ordered = normalizedQuery == null
                ? files.stream().sorted(FilePriority.COMPARATOR).toList()
                : orderBySearch(projectId, normalizedQuery, files);

        writeDocuments(writer, ordered);
        return writer.build();
    }

    private List<ProjectFileSummaryDTO> filterByExtension(List<ProjectFileSummaryDTO> files, Set<String> extensions) {
        if (extensions == null || extensions.isEmpty()) {
            return files;
        }
        Set<String> normalized = extensions.stream()
                .map(ext -> ext.trim().toLowerCase().replaceFirst("^\\.", ""))
                .collect(Collectors.toSet());
        return files.stream()
                .filter(file -> file.extension() != null && normalized.contains(file.extension()))
                .toList();
    }

    private List<ProjectFileSummaryDTO> orderBySearch(Long projectId, String query,
            List<ProjectFileSummaryDTO> files) {
        Map<Long, ProjectFileSummaryDTO> byId = files.stream()
                .collect(Collectors.toMap(ProjectFileSummaryDTO::id, Function.identity()));
        return fileService.search(projectId, query, SEARCH_LIMIT).stream()
                .map(FileSearchResultDTO::id)
                .map(byId::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private void writeDocuments(ContextDocumentWriter writer, List<ProjectFileSummaryDTO> ordered) {
        for (int start = 0; start < ordered.size(); start += FETCH_CHUNK_SIZE) {
            List<ProjectFileSummaryDTO> chunk = ordered.subList(start, Math.min(start + FETCH_CHUNK_SIZE, ordered.size()));
            List<Long> toFetch = new ArrayList<>();
            chunk.forEach(file -> {
                if (writer.mightFit(file.sizeBytes())) {
                    toFetch.add(file.id());
                }
            });
            Map<Long, FileContent> contents = toFetch.isEmpty() ? Map.of()
                    : fileService.findContents(toFetch).stream()
                            .collect(Collectors.toMap(FileContent::id, Function.identity()));
            chunk.forEach(file -> writeOrOmit(writer, file, contents.get(file.id())));
        }
    }

    private void writeOrOmit(ContextDocumentWriter writer, ProjectFileSummaryDTO file, FileContent content) {
        if (content == null) {
            writer.omit(file.relativePath());
        } else {
            writer.append(content);
        }
    }
}
