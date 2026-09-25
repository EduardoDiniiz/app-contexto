package com.contexto.scan;

import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class LanguageResolver {

    private static final Map<String, String> BY_FILE_NAME = Map.of(
            "dockerfile", "dockerfile",
            "makefile", "makefile",
            "jenkinsfile", "groovy",
            "pom.xml", "maven");

    private static final Map<String, String> BY_EXTENSION = Map.ofEntries(
            Map.entry("java", "java"),
            Map.entry("kt", "kotlin"),
            Map.entry("kts", "kotlin"),
            Map.entry("groovy", "groovy"),
            Map.entry("gradle", "groovy"),
            Map.entry("scala", "scala"),
            Map.entry("js", "javascript"),
            Map.entry("mjs", "javascript"),
            Map.entry("cjs", "javascript"),
            Map.entry("jsx", "jsx"),
            Map.entry("ts", "typescript"),
            Map.entry("tsx", "tsx"),
            Map.entry("vue", "vue"),
            Map.entry("svelte", "svelte"),
            Map.entry("py", "python"),
            Map.entry("rb", "ruby"),
            Map.entry("php", "php"),
            Map.entry("go", "go"),
            Map.entry("rs", "rust"),
            Map.entry("c", "c"),
            Map.entry("h", "c"),
            Map.entry("cpp", "cpp"),
            Map.entry("hpp", "cpp"),
            Map.entry("cs", "csharp"),
            Map.entry("swift", "swift"),
            Map.entry("dart", "dart"),
            Map.entry("sql", "sql"),
            Map.entry("html", "html"),
            Map.entry("htm", "html"),
            Map.entry("css", "css"),
            Map.entry("scss", "scss"),
            Map.entry("less", "less"),
            Map.entry("json", "json"),
            Map.entry("xml", "xml"),
            Map.entry("yml", "yaml"),
            Map.entry("yaml", "yaml"),
            Map.entry("toml", "toml"),
            Map.entry("properties", "properties"),
            Map.entry("md", "markdown"),
            Map.entry("sh", "bash"),
            Map.entry("ps1", "powershell"),
            Map.entry("bat", "batch"),
            Map.entry("cmd", "batch"),
            Map.entry("tf", "terraform"),
            Map.entry("proto", "protobuf"),
            Map.entry("graphql", "graphql"));

    public String resolve(String fileName, String extension) {
        String byName = BY_FILE_NAME.get(fileName.toLowerCase());
        if (byName != null) {
            return byName;
        }
        return extension == null ? "text" : BY_EXTENSION.getOrDefault(extension, "text");
    }
}
