package com.contexto.file;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectFileRepository extends JpaRepository<ProjectFile, Long> {

    @Query("SELECT new com.contexto.file.FileSnapshot(f.id, f.relativePath, f.contentHash)"
            + " FROM ProjectFile f"
            + " WHERE f.project.id = :projectId")
    List<FileSnapshot> findSnapshotsByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT new com.contexto.file.ProjectFileSummaryDTO("
            + "f.id, f.relativePath, f.extension, f.language, f.sizeBytes, f.lastModifiedAt)"
            + " FROM ProjectFile f"
            + " WHERE f.project.id = :projectId"
            + " ORDER BY f.relativePath")
    List<ProjectFileSummaryDTO> findSummariesByProjectId(@Param("projectId") Long projectId);

    @Query("SELECT new com.contexto.file.FileContent(f.id, f.relativePath, f.language, f.content)"
            + " FROM ProjectFile f"
            + " WHERE f.id IN :ids")
    List<FileContent> findContentsByIdIn(@Param("ids") Collection<Long> ids);

    Optional<ProjectFile> findByIdAndProjectId(Long id, Long projectId);

    @Query(value = "SELECT f.id AS \"id\","
            + " f.relative_path AS \"relativePath\","
            + " f.language AS \"language\","
            + " ts_rank(f.content_tsv, q) AS \"rank\","
            + " ts_headline('simple', f.content, q, 'MaxFragments=2, MaxWords=25, MinWords=8') AS \"snippet\""
            + " FROM project_file f, websearch_to_tsquery('simple', :query) q"
            + " WHERE f.project_id = :projectId"
            + " AND (f.content_tsv @@ q OR f.relative_path ILIKE '%' || :query || '%')"
            + " ORDER BY \"rank\" DESC, f.relative_path"
            + " LIMIT :limit", nativeQuery = true)
    List<FileSearchHit> search(
            @Param("projectId") Long projectId,
            @Param("query") String query,
            @Param("limit") int limit);
}
