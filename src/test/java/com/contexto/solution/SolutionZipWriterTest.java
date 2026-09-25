package com.contexto.solution;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.jupiter.api.Test;

class SolutionZipWriterTest {

    private final SolutionZipWriter writer = new SolutionZipWriter();

    @Test
    void writesFilesWithProjectPathsAndSummaryListingDeletions() throws IOException {
        SolutionDetailDTO solution = solution(
                new SolutionFileDTO(1L, "src/main/java/App.java", FileAction.MODIFY, "java", "altera", "class App {}"),
                new SolutionFileDTO(2L, "docs/novo.md", FileAction.CREATE, "markdown", "cria", "# Novo"),
                new SolutionFileDTO(3L, "src/Old.java", FileAction.DELETE, "java", "remove", ""));

        Map<String, String> entries = unzip(writer.write(solution));

        assertThat(entries).containsOnlyKeys("SOLUCAO.md", "src/main/java/App.java", "docs/novo.md");
        assertThat(entries.get("src/main/java/App.java")).isEqualTo("class App {}");
        assertThat(entries.get("SOLUCAO.md"))
                .contains("# Exportar CSV")
                .contains("Resumo da solução")
                .contains("## Arquivos a remover")
                .contains("`src/Old.java`");
    }

    private static SolutionDetailDTO solution(SolutionFileDTO... files) {
        return new SolutionDetailDTO(7L, 1L, "app-ponto", "Exportar CSV", "Crie a exportação",
                "Resumo da solução", "claude-opus-5", null, null,
                LocalDateTime.now(), LocalDateTime.now(), Arrays.asList(files));
    }

    private static Map<String, String> unzip(byte[] zip) throws IOException {
        Map<String, String> entries = new LinkedHashMap<>();
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(zip), StandardCharsets.UTF_8)) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                ByteArrayOutputStream content = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    content.write(buffer, 0, read);
                }
                entries.put(entry.getName(), new String(content.toByteArray(), StandardCharsets.UTF_8));
            }
        }
        return entries;
    }
}
