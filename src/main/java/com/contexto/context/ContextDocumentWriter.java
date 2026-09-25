package com.contexto.context;

import java.util.ArrayList;
import java.util.List;

import com.contexto.file.FileContent;
import com.contexto.project.ProjectResponseDTO;

/**
 * Monta o documento de contexto no formato recomendado para textos longos no Claude:
 * cada arquivo em um bloco {@code <document>} com {@code <source>} e {@code <document_content>}.
 * Respeita um limite de caracteres e registra os arquivos que ficaram de fora.
 */
final class ContextDocumentWriter {

    // Reserva para fechar as tags e listar arquivos omitidos.
    private static final int CLOSING_RESERVE = 2_000;

    private final StringBuilder documents = new StringBuilder();
    private final List<String> omitted = new ArrayList<>();
    private final String header;
    private final int maxChars;
    private int documentCount;

    ContextDocumentWriter(ProjectResponseDTO project, String fileTree, String query, int maxChars) {
        this.header = renderHeader(project, fileTree, query);
        this.maxChars = maxChars;
    }

    /** Estimativa usada para evitar buscar do banco arquivos que certamente não cabem. */
    boolean mightFit(long sizeBytes) {
        return sizeBytes + 200 <= remaining();
    }

    void append(FileContent file) {
        String document = renderDocument(documentCount + 1, file);
        if (document.length() > remaining()) {
            omit(file.getRelativePath());
            return;
        }
        documents.append(document);
        documentCount++;
    }

    void omit(String relativePath) {
        omitted.add(relativePath);
    }

    String build() {
        StringBuilder out = new StringBuilder(header.length() + documents.length() + 512);
        out.append(header)
                .append("<documents>\n").append(documents).append("</documents>\n");
        if (!omitted.isEmpty()) {
            out.append("<omitted_files reason=\"limite de caracteres\" count=\"")
                    .append(omitted.size()).append("\">\n");
            omitted.forEach(path -> out.append(path).append('\n'));
            out.append("</omitted_files>\n");
        }
        return out.append("</project_context>\n").toString();
    }

    private int remaining() {
        return maxChars - header.length() - documents.length() - CLOSING_RESERVE;
    }

    private static String renderHeader(ProjectResponseDTO project, String fileTree, String query) {
        StringBuilder out = new StringBuilder()
                .append("<project_context>\n")
                .append("<project name=\"").append(project.getName())
                .append("\" root=\"").append(project.getRootPath())
                .append("\" files=\"").append(project.getFileCount())
                .append("\" scanned_at=\"").append(project.getLastScannedAt()).append("\"/>\n");
        if (query != null) {
            out.append("<filter query=\"").append(query).append("\"/>\n");
        }
        return out.append("<file_tree>\n").append(fileTree).append("</file_tree>\n").toString();
    }

    private static String renderDocument(int index, FileContent file) {
        return "<document index=\"" + index + "\">\n"
                + "<source>" + file.getRelativePath() + "</source>\n"
                + "<language>" + file.getLanguage() + "</language>\n"
                + "<document_content>\n" + file.getContent()
                + (file.getContent().endsWith("\n") ? "" : "\n")
                + "</document_content>\n"
                + "</document>\n";
    }
}
