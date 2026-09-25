package com.contexto.solution;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SolutionRepository extends JpaRepository<Solution, Long> {

    String SUMMARY_SELECT = "SELECT new com.contexto.solution.SolutionSummaryDTO("
            + "s.id, p.id, s.projectName, s.title, SIZE(s.files), s.model, s.createdAt, s.updatedAt)"
            + " FROM Solution s LEFT JOIN s.project p";

    @Query(SUMMARY_SELECT + " ORDER BY s.createdAt DESC")
    List<SolutionSummaryDTO> findAllSummaries();

    @Query(SUMMARY_SELECT + " WHERE p.id = :projectId ORDER BY s.createdAt DESC")
    List<SolutionSummaryDTO> findSummariesByProjectId(@Param("projectId") Long projectId);

    @EntityGraph(attributePaths = {"files", "project"})
    Optional<Solution> findWithFilesById(Long id);
}
