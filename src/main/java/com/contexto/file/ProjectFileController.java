package com.contexto.file;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/files")
@RequiredArgsConstructor
public class ProjectFileController {

    private final ProjectFileService fileService;

    @GetMapping
    public ResponseEntity<List<ProjectFileSummaryDTO>> listFiles(@PathVariable Long projectId) {
        return ResponseEntity.ok(fileService.listFiles(projectId));
    }

    @GetMapping("/{fileId}")
    public ResponseEntity<ProjectFileContentDTO> findFile(@PathVariable Long projectId, @PathVariable Long fileId) {
        return ResponseEntity.ok(fileService.findFile(projectId, fileId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<FileSearchResultDTO>> search(
            @PathVariable Long projectId,
            @RequestParam @NotBlank(message = "O termo de busca é obrigatório") String q,
            @RequestParam(defaultValue = "20") @Min(1) @Max(200) int limit) {
        return ResponseEntity.ok(fileService.search(projectId, q, limit));
    }
}
