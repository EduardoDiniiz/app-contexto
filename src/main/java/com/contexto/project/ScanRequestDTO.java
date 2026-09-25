package com.contexto.project;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScanRequestDTO {

    @NotBlank(message = "O caminho é obrigatório")
    @Size(max = 1024, message = "O caminho deve ter no máximo 1024 caracteres")
    private String path;

    @Size(max = 255, message = "O nome deve ter no máximo 255 caracteres")
    private String name;
}
