package com.contexto.solution;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.jupiter.api.Test;

class SolutionMapperTest {

    private final SolutionMapper mapper = new SolutionMapperImpl();

    @Test
    void toDetailDTOKeepsStringsUntouchedAndToleratesNulls() {
        Solution solution = Solution.builder()
                .title(" Título com espaços ")
                .request("pedido")
                .summary("./resumo\\com\\barras")
                .build();
        solution.replaceFiles(Collections.singletonList(
                SolutionFile.builder().path("a.java").action(FileAction.CREATE).content("x").build()));

        SolutionDetailDTO dto = mapper.toDetailDTO(solution);

        assertThat(dto.getTitle()).isEqualTo(" Título com espaços ");
        assertThat(dto.getSummary()).isEqualTo("./resumo\\com\\barras");
        assertThat(dto.getProjectId()).isNull();
        assertThat(dto.getModel()).isNull();
    }

    @Test
    void toFileEntityNormalizesPathAndClearsContentOfDeletions() {
        SolutionFile created = mapper.toFileEntity(
                new SolutionFileRequestDTO(" ./src\\main\\App.java ", FileAction.CREATE, "java", null, "class App {}"));
        SolutionFile deleted = mapper.toFileEntity(
                new SolutionFileRequestDTO("src/Old.java", FileAction.DELETE, "java", null, "ignorado"));

        assertThat(created.getPath()).isEqualTo("src/main/App.java");
        assertThat(created.getContent()).isEqualTo("class App {}");
        assertThat(deleted.getContent()).isEmpty();
    }
}
