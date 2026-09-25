package com.contexto.solution;

import static org.assertj.core.api.Assertions.assertThat;

import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SolutionFileRequestDTOTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @ParameterizedTest
    @ValueSource(strings = {"src/main/App.java", "pom.xml", "./README.md", "src\\main\\App.java", "a/..b/c.txt"})
    void acceptsPathsRelativeToProjectRoot(String path) {
        assertThat(validator.validate(file(path))).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"/etc/passwd", "\\windows\\x", "C:/Projetos/x.java", "../fora.txt", "src/../../fora", "a\\..\\b"})
    void rejectsAbsoluteOrEscapingPaths(String path) {
        assertThat(validator.validate(file(path))).isNotEmpty();
    }

    private static SolutionFileRequestDTO file(String path) {
        return new SolutionFileRequestDTO(path, FileAction.CREATE, "java", "desc", "conteudo");
    }
}
