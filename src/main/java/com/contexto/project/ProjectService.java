package com.contexto.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contexto.common.exception.BusinessException;
import com.contexto.common.exception.ResourceNotFoundException;
import com.contexto.scan.ScanService;
import com.contexto.scan.SyncResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private static final String RESOURCE = "Projeto";

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final ScanService scanService;

    /** Escaneia o diretório: cria o projeto se for novo, ou sincroniza se o caminho já estiver indexado. */
    @Transactional
    public ScanResultDTO scan(ScanRequestDTO dto) {
        Path root = resolveDirectory(dto.getPath());
        Project project = projectRepository.findByRootPath(root.toString())
                .orElseGet(() -> projectRepository.save(newProject(root)));
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            project.setName(dto.getName().trim());
        }
        return synchronize(project);
    }

    @Transactional
    public ScanResultDTO rescan(Long id) {
        Project project = findEntity(id);
        resolveDirectory(project.getRootPath());
        return synchronize(project);
    }

    @Transactional(readOnly = true)
    public List<ProjectResponseDTO> findAll() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProjectResponseDTO findById(Long id) {
        return projectMapper.toResponseDTO(findEntity(id));
    }

    @Transactional(readOnly = true)
    public void requireExists(Long id) {
        if (!projectRepository.existsById(id)) {
            throw new ResourceNotFoundException(RESOURCE, id);
        }
    }

    @Transactional
    public void delete(Long id) {
        requireExists(id);
        projectRepository.deleteById(id);
    }

    private ScanResultDTO synchronize(Project project) {
        long start = System.nanoTime();
        SyncResult result = scanService.synchronize(project);
        project.registerScan(result.getTotalFiles(), result.getTotalBytes());
        Project saved = projectRepository.save(project);
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        return projectMapper.toScanResultDTO(saved, result, durationMs);
    }

    private Project findEntity(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

    private Project newProject(Path root) {
        Path fileName = root.getFileName();
        return Project.builder()
                .name(fileName != null ? fileName.toString() : root.toString())
                .rootPath(root.toString())
                .build();
    }

    private Path resolveDirectory(String rawPath) {
        try {
            Path path = Paths.get(rawPath.trim());
            if (!path.isAbsolute() || !Files.isDirectory(path)) {
                throw new BusinessException("Caminho não é um diretório absoluto existente: " + rawPath);
            }
            return path.toRealPath();
        } catch (InvalidPathException | IOException e) {
            throw new BusinessException("Caminho inválido: " + rawPath);
        }
    }
}
