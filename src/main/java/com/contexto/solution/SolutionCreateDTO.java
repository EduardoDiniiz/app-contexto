package com.contexto.solution;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Solução pronta, gerada no Claude Code, para salvar (POST) ou substituir (PUT). */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolutionCreateDTO {

    @NotNull(message = "O projeto é obrigatório")
    private Long projectId;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 255, message = "O título deve ter no máximo 255 caracteres")
    private String title;

    @NotBlank(message = "O pedido é obrigatório")
    private String request;

    @NotBlank(message = "O resumo é obrigatório")
    private String summary;

    @Size(max = 100, message = "O modelo deve ter no máximo 100 caracteres")
    private String model;

    @Size(max = 500, message = "As extensões devem ter no máximo 500 caracteres")
    private String contextExtensions;

    @Size(max = 500, message = "A busca deve ter no máximo 500 caracteres")
    private String contextQuery;

    @Valid
    @NotNull(message = "A lista de arquivos é obrigatória (pode ser vazia)")
    private List<SolutionFileRequestDTO> files = new ArrayList<>();
}
