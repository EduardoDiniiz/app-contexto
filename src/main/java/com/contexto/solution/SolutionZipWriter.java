package com.contexto.solution;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Component;

/**
 * Empacota os arquivos de uma solução com a mesma estrutura de pastas do projeto,
 * mais um SOLUCAO.md com o resumo e os arquivos a remover.
 */
@Component
public class SolutionZipWriter {

    private static final String SUMMARY_FILE = "SOLUCAO.md";

    public byte[] write(SolutionDetailDTO solution) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(buffer, StandardCharsets.UTF_8)) {
            addEntry(zip, SUMMARY_FILE, renderSummary(solution));
            for (SolutionFileDTO file : solution.getFiles()) {
                if (file.getAction() != FileAction.DELETE) {
                    addEntry(zip, file.getPath(), file.getContent());
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao gerar o ZIP da solução " + solution.getId(), e);
        }
        return buffer.toByteArray();
    }

    private static void addEntry(ZipOutputStream zip, String path, String content) throws IOException {
        zip.putNextEntry(new ZipEntry(path));
        zip.write(content.getBytes(StandardCharsets.UTF_8));
        zip.closeEntry();
    }

    private static String renderSummary(SolutionDetailDTO solution) {
        StringBuilder out = new StringBuilder()
                .append("# ").append(solution.getTitle()).append("\n\n")
                .append("**Projeto:** ").append(solution.getProjectName()).append("\n\n")
                .append("## Pedido\n\n").append(solution.getRequest()).append("\n\n")
                .append("## Solução\n\n").append(solution.getSummary() == null ? "" : solution.getSummary()).append("\n");

        List<String> deleted = solution.getFiles().stream()
                .filter(file -> file.getAction() == FileAction.DELETE)
                .map(file -> "- `" + file.getPath() + "`")
                .collect(Collectors.toList());
        if (!deleted.isEmpty()) {
            out.append("\n## Arquivos a remover\n\n").append(String.join("\n", deleted)).append("\n");
        }
        return out.toString();
    }
}
