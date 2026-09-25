package com.contexto.file;

/** Metadados mínimos de um arquivo já indexado, usados para detectar mudanças sem carregar o conteúdo. */
public record FileSnapshot(Long id, String relativePath, String contentHash) {}
