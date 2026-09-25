package com.contexto.file;

public interface FileSearchHit {

    Long getId();

    String getRelativePath();

    String getLanguage();

    Float getRank();

    String getSnippet();
}
