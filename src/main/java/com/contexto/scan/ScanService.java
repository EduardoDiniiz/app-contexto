package com.contexto.scan;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contexto.file.FileSnapshot;
import com.contexto.file.ProjectFile;
import com.contexto.file.ProjectFileMapper;
import com.contexto.file.ProjectFileRepository;
import com.contexto.project.Project;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Sincroniza o disco com o banco de forma incremental: só grava arquivos novos ou cujo hash mudou,
 * e remove do banco o que não existe mais no disco.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ScanService {

    private static final int FLUSH_INTERVAL = 50;
    private static final int DELETE_CHUNK_SIZE = 1000;

    private final FileScanner fileScanner;
    private final ProjectFileRepository fileRepository;
    private final ProjectFileMapper fileMapper;
    private final EntityManager entityManager;

    @Transactional
    public SyncResult synchronize(Project project) {
        Map<String, FileSnapshot> existing = fileRepository.findSnapshotsByProjectId(project.getId()).stream()
                .collect(Collectors.toMap(FileSnapshot::relativePath, Function.identity()));
        Counters counters = new Counters();

        int skipped = fileScanner.scan(Path.of(project.getRootPath()),
                file -> apply(project, file, existing.remove(file.relativePath()), counters));
        removeDeleted(existing.values().stream().map(FileSnapshot::id).toList());

        log.info("Projeto {} sincronizado: +{} ~{} -{} ={} ignorados={}", project.getName(),
                counters.added, counters.updated, existing.size(), counters.unchanged, skipped);
        return new SyncResult(counters.added, counters.updated, existing.size(),
                counters.unchanged, skipped, counters.totalBytes);
    }

    private void apply(Project project, ScannedFile file, FileSnapshot snapshot, Counters counters) {
        if (snapshot == null) {
            fileRepository.save(fileMapper.toEntity(file, project));
            counters.added++;
        } else if (!snapshot.contentHash().equals(file.contentHash())) {
            ProjectFile entity = fileRepository.getReferenceById(snapshot.id());
            fileMapper.updateEntity(file, entity);
            counters.updated++;
        } else {
            counters.unchanged++;
        }
        counters.totalBytes += file.sizeBytes();
        flushPeriodically(counters);
    }

    /** Evita que o persistence context cresça com o conteúdo de milhares de arquivos. */
    private void flushPeriodically(Counters counters) {
        if (++counters.pendingWrites % FLUSH_INTERVAL == 0) {
            entityManager.flush();
            entityManager.clear();
        }
    }

    private void removeDeleted(List<Long> ids) {
        List<Long> remaining = new ArrayList<>(ids);
        while (!remaining.isEmpty()) {
            List<Long> chunk = remaining.subList(0, Math.min(DELETE_CHUNK_SIZE, remaining.size()));
            fileRepository.deleteAllByIdInBatch(List.copyOf(chunk));
            chunk.clear();
        }
    }

    private static final class Counters {
        private int added;
        private int updated;
        private int unchanged;
        private int pendingWrites;
        private long totalBytes;
    }
}
