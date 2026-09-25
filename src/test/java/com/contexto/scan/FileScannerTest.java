package com.contexto.scan;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.contexto.config.ScannerProperties;

class FileScannerTest {

    @TempDir
    Path root;

    private FileScanner scanner;

    @BeforeEach
    void setUp() {
        ScannerProperties properties = new ScannerProperties(
                1024, Set.of("node_modules", ".git"), Set.of("png"), List.of(".env*", "*.min.js"));
        scanner = new FileScanner(properties, new LanguageResolver());
    }

    @Test
    void scansTextFilesWithRelativePathLanguageAndHash() throws IOException {
        write("src/main/App.java", "class App {}");

        List<ScannedFile> files = scan();

        assertThat(files).singleElement().satisfies(file -> {
            assertThat(file.relativePath()).isEqualTo("src/main/App.java");
            assertThat(file.extension()).isEqualTo("java");
            assertThat(file.language()).isEqualTo("java");
            assertThat(file.content()).isEqualTo("class App {}");
            assertThat(file.contentHash()).hasSize(64);
        });
    }

    @Test
    void skipsIgnoredDirectoriesExtensionsGlobsBinariesAndLargeFiles() throws IOException {
        write("keep.md", "# ok");
        write("node_modules/lib/index.js", "x");
        write(".git/config", "x");
        write("image.png", "x");
        write(".env.local", "SECRET=1");
        write("app.min.js", "x");
        write("big.txt", "a".repeat(2048));
        Files.write(root.resolve("data.bin"), new byte[] {1, 0, 2});

        List<ScannedFile> files = new ArrayList<>();
        int skipped = scanner.scan(root, files::add);

        assertThat(files).extracting(ScannedFile::relativePath).containsExactly("keep.md");
        assertThat(skipped).isEqualTo(5);
    }

    @Test
    void fallsBackToLatin1AndStripsBom() throws IOException {
        Files.write(root.resolve("latin.txt"), "ação".getBytes(StandardCharsets.ISO_8859_1));
        write("bom.txt", "﻿conteúdo");

        List<ScannedFile> files = scan();

        assertThat(files).extracting(ScannedFile::content).containsExactlyInAnyOrder("ação", "conteúdo");
    }

    @Test
    void resolvesLanguageByFileNameWhenThereIsNoExtension() throws IOException {
        write("Dockerfile", "FROM alpine");

        assertThat(scan()).singleElement().satisfies(file -> {
            assertThat(file.extension()).isNull();
            assertThat(file.language()).isEqualTo("dockerfile");
        });
    }

    private List<ScannedFile> scan() {
        List<ScannedFile> files = new ArrayList<>();
        scanner.scan(root, files::add);
        return files;
    }

    private void write(String relativePath, String content) throws IOException {
        Path file = root.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content, StandardCharsets.UTF_8);
    }
}
