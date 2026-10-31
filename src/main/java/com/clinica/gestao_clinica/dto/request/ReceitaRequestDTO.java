package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReceitaRequestDTO(

        @NotNull(message = "Prontuário é obrigatório")
        Long prontuarioId,

        @NotBlank(message = "Medicamento é obrigatório")
        String medicamento,

        @NotBlank(message = "Dosagem é obrigatória")
        String dosagem,

        @NotBlank(message = "Frequência é obrigatória")
        String frequencia,

        @NotBlank(message = "Duração é obrigatória")
        String duracao,

        @NotBlank(message = "Observação é obrigatória")
        String observacao

) {
}
