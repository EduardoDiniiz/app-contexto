package com.contexto.scan;

public record SyncResult(
        int added,
        int updated,
        int removed,
        int unchanged,
        int skipped,
        long totalBytes
) {
    public int totalFiles() {
        return added + updated + unchanged;
    }
}
