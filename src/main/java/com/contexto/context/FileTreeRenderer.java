package com.contexto.context;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.stereotype.Component;

/** Renderiza caminhos relativos ("a/b/c.java") como árvore de diretórios no estilo do comando tree. */
@Component
public class FileTreeRenderer {

    public String render(Collection<String> relativePaths) {
        Node root = new Node();
        relativePaths.forEach(path -> root.add(path.split("/")));
        StringBuilder out = new StringBuilder();
        root.render(out, "");
        return out.toString();
    }

    private static final class Node {

        // Diretórios antes de arquivos, ambos em ordem alfabética.
        private final Map<String, Node> directories = new TreeMap<>();
        private final Map<String, Node> files = new TreeMap<>();

        private void add(String[] segments) {
            Node current = this;
            for (int i = 0; i < segments.length; i++) {
                boolean isFile = i == segments.length - 1;
                Map<String, Node> target = isFile ? current.files : current.directories;
                current = target.computeIfAbsent(segments[i], key -> new Node());
            }
        }

        private void render(StringBuilder out, String indent) {
            int remaining = directories.size() + files.size();
            for (Map.Entry<String, Node> dir : directories.entrySet()) {
                boolean last = --remaining == 0;
                out.append(indent).append(last ? "└── " : "├── ").append(dir.getKey()).append("/\n");
                dir.getValue().render(out, indent + (last ? "    " : "│   "));
            }
            for (String file : files.keySet()) {
                boolean last = --remaining == 0;
                out.append(indent).append(last ? "└── " : "├── ").append(file).append('\n');
            }
        }
    }
}
