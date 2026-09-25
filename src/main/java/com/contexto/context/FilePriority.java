package com.contexto.context;

import java.util.Comparator;
import java.util.Set;

import com.contexto.file.ProjectFileSummaryDTO;

/**
 * Ordena arquivos pelo valor que têm para entender o projeto: README e manifestos primeiro,
 * depois configuração, código e por último testes. Garante que, se o limite de caracteres
 * cortar o contexto, o que fica de fora é o menos importante.
 */
final class FilePriority {

    private static final Set<String> MANIFESTS = Set.of(
            "pom.xml", "build.gradle", "build.gradle.kts", "settings.gradle", "package.json",
            "tsconfig.json", "requirements.txt", "pyproject.toml", "go.mod", "cargo.toml",
            "composer.json", "gemfile", "dockerfile", "docker-compose.yml", "docker-compose.yaml",
            "claude.md");

    private static final Set<String> CONFIG_EXTENSIONS = Set.of("yml", "yaml", "properties", "toml", "sql");

    static final Comparator<ProjectFileSummaryDTO> COMPARATOR =
            Comparator.comparingInt(FilePriority::rank).thenComparing(ProjectFileSummaryDTO::relativePath);

    private FilePriority() {
    }

    private static int rank(ProjectFileSummaryDTO file) {
        String path = file.relativePath().toLowerCase();
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (name.startsWith("readme")) {
            return 0;
        }
        if (MANIFESTS.contains(name)) {
            return 1;
        }
        if (isTest(path)) {
            return 4;
        }
        if (file.extension() != null && CONFIG_EXTENSIONS.contains(file.extension())) {
            return 2;
        }
        return 3;
    }

    private static boolean isTest(String path) {
        return path.contains("/test/") || path.contains("/tests/") || path.contains("__tests__")
                || path.contains(".test.") || path.contains(".spec.") || path.endsWith("test.java");
    }
}
