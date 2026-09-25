package com.contexto.solution;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionFileRequestDTO {

    // Relativo à raiz do projeto: sem "..", sem barra inicial e sem letra de unidade (evita zip slip no download).
    @NotBlank(message = "O caminho do arquivo é obrigatório")
    @Size(max = 1024, message = "O caminho deve ter no máximo 1024 caracteres")
    @Pattern(regexp = "^(?![/\\\\])(?![A-Za-z]:)(?!.*(^|[/\\\\])\\.\\.([/\\\\]|$)).+$",
            message = "O caminho deve ser relativo à raiz do projeto e não pode conter '..'")
    private String path;

    @NotNull(message = "A ação do arquivo é obrigatória (CREATE, MODIFY ou DELETE)")
    private FileAction action;

    @Size(max = 50, message = "A linguagem deve ter no máximo 50 caracteres")
    private String language;

    private String description;

    /** Conteúdo completo do arquivo; ignorado quando a ação é DELETE. */
    private String content;
}
