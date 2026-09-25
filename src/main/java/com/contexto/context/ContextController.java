package com.contexto.context;

import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

/**
 * Entrega o contexto do projeto pronto para colar/enviar ao Claude.
 * ~4 caracteres ≈ 1 token: o padrão de 400 mil caracteres fica em torno de 100 mil tokens.
 */
@RestController
@Validated
@RequestMapping("/api/v1/projects/{projectId}/context")
@RequiredArgsConstructor
public class ContextController {

    private static final MediaType TEXT_UTF8 = new MediaType("text", "plain", StandardCharsets.UTF_8);

    private final ContextService contextService;

    @GetMapping
    public ResponseEntity<String> buildContext(
            @PathVariable Long projectId,
            @RequestParam(required = false) Set<String> extensions,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "400000")
            @Min(value = 5_000, message = "maxChars mínimo é 5000")
            @Max(value = 4_000_000, message = "maxChars máximo é 4000000") int maxChars) {
        return ResponseEntity.ok()
                .contentType(TEXT_UTF8)
                .body(contextService.buildContext(projectId, extensions, q, maxChars));
    }
}
