package com.contexto.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;

class FileTreeRendererTest {

    private final FileTreeRenderer renderer = new FileTreeRenderer();

    @Test
    void rendersDirectoriesBeforeFilesWithTreeConnectors() {
        String tree = renderer.render(List.of(
                "pom.xml",
                "src/main/App.java",
                "src/main/util/Strings.java"));

        assertThat(tree).isEqualTo("""
                ├── src/
                │   └── main/
                │       ├── util/
                │       │   └── Strings.java
                │       └── App.java
                └── pom.xml
                """);
    }
}
