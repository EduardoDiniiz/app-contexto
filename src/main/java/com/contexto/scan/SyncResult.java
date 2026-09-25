package com.contexto.scan;

import lombok.Value;

@Value
public class SyncResult {
    int added;
    int updated;
    int removed;
    int unchanged;
    int skipped;
    long totalBytes;

    public int getTotalFiles() {
        return added + updated + unchanged;
    }
}
