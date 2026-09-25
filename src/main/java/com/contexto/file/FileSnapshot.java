package com.contexto.file;

import lombok.Value;

/** Metadados mínimos de um arquivo já indexado, usados para detectar mudanças sem carregar o conteúdo. */
@Value
public class FileSnapshot {
    Long id;
    String relativePath;
    String contentHash;
}
