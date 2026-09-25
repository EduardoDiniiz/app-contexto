package com.contexto.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ScanRequestDTO(
        @NotBlank(message = "O caminho é obrigatório")
        @Size(max = 1024, message = "O caminho deve ter no máximo 1024 caracteres")
        String path,

        @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
        String name
) {}
