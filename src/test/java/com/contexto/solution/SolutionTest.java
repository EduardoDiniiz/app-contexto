package com.contexto.solution;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class SolutionTest {

    @Test
    void replaceFilesLinksFilesAndKeepsOrder() {
        Solution solution = new Solution();

        solution.replaceFiles(Arrays.asList(file("a.java"), file("b.java")));

        assertThat(solution.getFiles()).extracting(SolutionFile::getPath).containsExactly("a.java", "b.java");
        assertThat(solution.getFiles()).extracting(SolutionFile::getPosition).containsExactly(0, 1);
        assertThat(solution.getFiles()).allSatisfy(f -> assertThat(f.getSolution()).isSameAs(solution));
    }

    @Test
    void replaceFilesDropsPreviousFiles() {
        Solution solution = new Solution();
        solution.replaceFiles(Arrays.asList(file("antigo.java"), file("outro.java")));

        solution.replaceFiles(Collections.singletonList(file("novo.java")));

        assertThat(solution.getFiles()).extracting(SolutionFile::getPath).containsExactly("novo.java");
        assertThat(solution.getFiles().get(0).getPosition()).isZero();
    }

    private static SolutionFile file(String path) {
        return SolutionFile.builder().path(path).action(FileAction.CREATE).content("x").build();
    }
}
