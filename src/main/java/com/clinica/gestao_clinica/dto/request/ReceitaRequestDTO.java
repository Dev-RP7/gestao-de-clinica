package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReceitaRequestDTO(

        @NotNull
        Long prontuarioId,

        @NotBlank
        String medicamento,

        @NotBlank
        String dosagem,

        @NotBlank
        String frequencia,

        @NotBlank
        String duracao,

        @NotBlank
        String observacao


) {
}
