package com.contexto.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class FileTreeRendererTest {

    private final FileTreeRenderer renderer = new FileTreeRenderer();

    @Test
    void rendersDirectoriesBeforeFilesWithTreeConnectors() {
        String tree = renderer.render(Arrays.asList(
                "pom.xml",
                "src/main/App.java",
                "src/main/util/Strings.java"));

        assertThat(tree).isEqualTo(
                "├── src/\n"
                + "│   └── main/\n"
                + "│       ├── util/\n"
                + "│       │   └── Strings.java\n"
                + "│       └── App.java\n"
                + "└── pom.xml\n");
    }
}
