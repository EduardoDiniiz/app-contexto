package com.contexto.scan;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class LanguageResolver {

    private static final Map<String, String> BY_FILE_NAME;
    private static final Map<String, String> BY_EXTENSION;

    static {
        Map<String, String> byName = new HashMap<>();
        byName.put("dockerfile", "dockerfile");
        byName.put("makefile", "makefile");
        byName.put("jenkinsfile", "groovy");
        byName.put("pom.xml", "maven");
        BY_FILE_NAME = Collections.unmodifiableMap(byName);

        Map<String, String> byExt = new HashMap<>();
        register(byExt, "java", "java");
        register(byExt, "kotlin", "kt", "kts");
        register(byExt, "groovy", "groovy", "gradle");
        register(byExt, "scala", "scala");
        register(byExt, "javascript", "js", "mjs", "cjs");
        register(byExt, "jsx", "jsx");
        register(byExt, "typescript", "ts");
        register(byExt, "tsx", "tsx");
        register(byExt, "vue", "vue");
        register(byExt, "svelte", "svelte");
        register(byExt, "python", "py");
        register(byExt, "ruby", "rb");
        register(byExt, "php", "php");
        register(byExt, "go", "go");
        register(byExt, "rust", "rs");
        register(byExt, "c", "c", "h");
        register(byExt, "cpp", "cpp", "hpp");
        register(byExt, "csharp", "cs");
        register(byExt, "swift", "swift");
        register(byExt, "dart", "dart");
        register(byExt, "sql", "sql");
        register(byExt, "html", "html", "htm");
        register(byExt, "css", "css");
        register(byExt, "scss", "scss");
        register(byExt, "less", "less");
        register(byExt, "json", "json");
        register(byExt, "xml", "xml");
        register(byExt, "yaml", "yml", "yaml");
        register(byExt, "toml", "toml");
        register(byExt, "properties", "properties");
        register(byExt, "markdown", "md");
        register(byExt, "bash", "sh");
        register(byExt, "powershell", "ps1");
        register(byExt, "batch", "bat", "cmd");
        register(byExt, "terraform", "tf");
        register(byExt, "protobuf", "proto");
        register(byExt, "graphql", "graphql");
        BY_EXTENSION = Collections.unmodifiableMap(byExt);
    }

    private static void register(Map<String, String> map, String language, String... extensions) {
        for (String extension : extensions) {
            map.put(extension, language);
        }
    }

    public String resolve(String fileName, String extension) {
        String byName = BY_FILE_NAME.get(fileName.toLowerCase());
        if (byName != null) {
            return byName;
        }
        return extension == null ? "text" : BY_EXTENSION.getOrDefault(extension, "text");
    }
}
