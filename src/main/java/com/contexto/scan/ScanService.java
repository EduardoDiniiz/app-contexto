package com.contexto.scan;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contexto.file.FileSnapshot;
import com.contexto.file.ProjectFile;
import com.contexto.file.ProjectFileMapper;
import com.contexto.file.ProjectFileRepository;
import com.contexto.project.Project;

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
                .collect(Collectors.toMap(FileSnapshot::getRelativePath, Function.identity()));
        Counters counters = new Counters();

        int skipped = fileScanner.scan(Paths.get(project.getRootPath()),
                file -> apply(project, file, existing.remove(file.getRelativePath()), counters));
        removeDeleted(existing.values().stream().map(FileSnapshot::getId).collect(Collectors.toList()));

        log.info("Projeto {} sincronizado: +{} ~{} -{} ={} ignorados={}", project.getName(),
                counters.added, counters.updated, existing.size(), counters.unchanged, skipped);
        return new SyncResult(counters.added, counters.updated, existing.size(),
                counters.unchanged, skipped, counters.totalBytes);
    }

    private void apply(Project project, ScannedFile file, FileSnapshot snapshot, Counters counters) {
        if (snapshot == null) {
            fileRepository.save(fileMapper.toEntity(file, project));
            counters.added++;
        } else if (!snapshot.getContentHash().equals(file.getContentHash())) {
            ProjectFile entity = fileRepository.getReferenceById(snapshot.getId());
            fileMapper.updateEntity(file, entity);
            counters.updated++;
        } else {
            counters.unchanged++;
        }
        counters.totalBytes += file.getSizeBytes();
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
        for (int start = 0; start < ids.size(); start += DELETE_CHUNK_SIZE) {
            List<Long> chunk = ids.subList(start, Math.min(start + DELETE_CHUNK_SIZE, ids.size()));
            fileRepository.deleteAllByIdInBatch(new ArrayList<>(chunk));
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
