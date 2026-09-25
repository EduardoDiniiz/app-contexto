package com.contexto.solution;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/solutions")
@RequiredArgsConstructor
public class SolutionController {

    private final SolutionService solutionService;
    private final SolutionZipWriter zipWriter;

    @PostMapping
    public ResponseEntity<SolutionDetailDTO> create(@Valid @RequestBody SolutionCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(solutionService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SolutionDetailDTO> update(@PathVariable Long id, @Valid @RequestBody SolutionCreateDTO dto) {
        return ResponseEntity.ok(solutionService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<SolutionSummaryDTO>> findAll(@RequestParam(required = false) Long projectId) {
        return ResponseEntity.ok(solutionService.findAll(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolutionDetailDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(solutionService.findById(id));
    }

    @GetMapping("/{id}/zip")
    public ResponseEntity<byte[]> downloadZip(@PathVariable Long id) {
        byte[] zip = zipWriter.write(solutionService.findById(id));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename("solucao-" + id + ".zip").build().toString())
                .body(zip);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        solutionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
