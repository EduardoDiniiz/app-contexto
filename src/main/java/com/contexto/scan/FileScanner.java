package com.contexto.scan;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.contexto.config.ScannerProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Percorre um diretório e entrega ao consumer cada arquivo de texto relevante.
 * Retorna a quantidade de arquivos ignorados (filtros, binários ou erro de leitura).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileScanner {

    private static final int BINARY_PROBE_BYTES = 8000;
    private static final char BOM = '﻿';
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private final ScannerProperties properties;
    private final LanguageResolver languageResolver;

    public int scan(Path root, Consumer<ScannedFile> consumer) {
        List<PathMatcher> globs = properties.getIgnoredFileGlobs().stream()
                .map(glob -> FileSystems.getDefault().getPathMatcher("glob:" + glob))
                .collect(Collectors.toList());
        ScanVisitor visitor = new ScanVisitor(root, consumer, globs);
        try {
            Files.walkFileTree(root, visitor);
        } catch (IOException e) {
            throw new UncheckedIOException("Falha ao percorrer " + root, e);
        }
        return visitor.skipped;
    }

    private final class ScanVisitor extends SimpleFileVisitor<Path> {

        private final Path root;
        private final Consumer<ScannedFile> consumer;
        private final List<PathMatcher> globs;
        private int skipped;

        private ScanVisitor(Path root, Consumer<ScannedFile> consumer, List<PathMatcher> globs) {
            this.root = root;
            this.consumer = consumer;
            this.globs = globs;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
            boolean ignored = !dir.equals(root)
                    && properties.getIgnoredDirectories().contains(dir.getFileName().toString());
            return ignored ? FileVisitResult.SKIP_SUBTREE : FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (!attrs.isRegularFile()) {
                return FileVisitResult.CONTINUE;
            }
            Optional<ScannedFile> scanned = isIgnored(file, attrs) ? Optional.empty() : read(file, attrs);
            if (scanned.isPresent()) {
                consumer.accept(scanned.get());
            } else {
                skipped++;
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) {
            log.warn("Não foi possível ler {}: {}", file, exc.getMessage());
            skipped++;
            return FileVisitResult.CONTINUE;
        }

        private boolean isIgnored(Path file, BasicFileAttributes attrs) {
            Path fileName = file.getFileName();
            String extension = extensionOf(fileName.toString());
            return attrs.size() > properties.getMaxFileSizeBytes()
                    || (extension != null && properties.getIgnoredExtensions().contains(extension))
                    || globs.stream().anyMatch(glob -> glob.matches(fileName));
        }

        private Optional<ScannedFile> read(Path file, BasicFileAttributes attrs) {
            try {
                byte[] bytes = Files.readAllBytes(file);
                return decode(bytes).map(content -> toScannedFile(file, attrs, bytes, content));
            } catch (IOException e) {
                log.warn("Não foi possível ler {}: {}", file, e.getMessage());
                return Optional.empty();
            }
        }

        private ScannedFile toScannedFile(Path file, BasicFileAttributes attrs, byte[] bytes, String content) {
            String fileName = file.getFileName().toString();
            String extension = extensionOf(fileName);
            return new ScannedFile(
                    root.relativize(file).toString().replace('\\', '/'),
                    extension,
                    languageResolver.resolve(fileName, extension),
                    bytes.length,
                    sha256(bytes),
                    content,
                    LocalDateTime.ofInstant(attrs.lastModifiedTime().toInstant(), ZoneOffset.UTC));
        }
    }

    static String extensionOf(String fileName) {
        int dot = fileName.lastIndexOf('.');
        if (dot <= 0 || dot == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(dot + 1).toLowerCase();
    }

    static Optional<String> decode(byte[] bytes) {
        if (isBinary(bytes)) {
            return Optional.empty();
        }
        String text = decodeUtf8(bytes).orElseGet(() -> new String(bytes, StandardCharsets.ISO_8859_1));
        return Optional.of(!text.isEmpty() && text.charAt(0) == BOM ? text.substring(1) : text);
    }

    private static boolean isBinary(byte[] bytes) {
        int limit = Math.min(bytes.length, BINARY_PROBE_BYTES);
        for (int i = 0; i < limit; i++) {
            if (bytes[i] == 0) {
                return true;
            }
        }
        return false;
    }

    private static Optional<String> decodeUtf8(byte[] bytes) {
        try {
            return Optional.of(StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(bytes))
                    .toString());
        } catch (CharacterCodingException e) {
            return Optional.empty();
        }
    }

    private static String sha256(byte[] bytes) {
        try {
            return toHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 indisponível", e);
        }
    }

    private static String toHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            out[i * 2] = HEX_DIGITS[(bytes[i] >> 4) & 0xF];
            out[i * 2 + 1] = HEX_DIGITS[bytes[i] & 0xF];
        }
        return new String(out);
    }
}
