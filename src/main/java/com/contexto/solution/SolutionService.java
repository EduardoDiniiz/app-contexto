package com.contexto.solution;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.contexto.common.exception.ResourceNotFoundException;
import com.contexto.project.Project;
import com.contexto.project.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SolutionService {

    private static final String RESOURCE = "Solução";

    private final SolutionRepository solutionRepository;
    private final SolutionMapper solutionMapper;
    private final ProjectService projectService;

    @Transactional
    public SolutionDetailDTO create(SolutionCreateDTO dto) {
        Solution solution = new Solution();
        apply(dto, solution);
        return solutionMapper.toDetailDTO(solutionRepository.save(solution));
    }

    /** Substitui a solução inteira, ex.: quando o usuário pede ajustes numa solução já salva. */
    @Transactional
    public SolutionDetailDTO update(Long id, SolutionCreateDTO dto) {
        Solution solution = findEntity(id);
        apply(dto, solution);
        return solutionMapper.toDetailDTO(solutionRepository.save(solution));
    }

    @Transactional(readOnly = true)
    public List<SolutionSummaryDTO> findAll(Long projectId) {
        return projectId == null
                ? solutionRepository.findAllSummaries()
                : solutionRepository.findSummariesByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public SolutionDetailDTO findById(Long id) {
        return solutionMapper.toDetailDTO(findEntity(id));
    }

    @Transactional
    public void delete(Long id) {
        solutionRepository.delete(findEntity(id));
    }

    private void apply(SolutionCreateDTO dto, Solution solution) {
        Project project = projectService.findEntity(dto.getProjectId());
        solution.setProject(project);
        solution.setProjectName(project.getName());
        solution.setTitle(dto.getTitle().trim());
        solution.setRequest(dto.getRequest().trim());
        solution.setSummary(dto.getSummary());
        solution.setModel(blankToNull(dto.getModel()));
        solution.setContextExtensions(blankToNull(dto.getContextExtensions()));
        solution.setContextQuery(blankToNull(dto.getContextQuery()));
        solution.replaceFiles(dto.getFiles().stream()
                .map(solutionMapper::toFileEntity)
                .collect(Collectors.toList()));
    }

    private Solution findEntity(Long id) {
        return solutionRepository.findWithFilesById(id)
                .orElseThrow(() -> new ResourceNotFoundException(RESOURCE, id));
    }

    private static String blankToNull(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }
}
