package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProntuarioRequestDTO(

        @NotNull(message = "Consulta é obrigatória")
        Long consultaId,

        @Positive(message = "Peso deve ser maior que zero")
        double peso,

        @Positive(message = "Altura deve ser maior que zero")
        double altura,

        @Positive(message = "Pressão deve ser maior que zero")
        double pressao,

        @Positive(message = "Temperatura deve ser maior que zero")
        double temperatura,

        @NotBlank(message = "Diagnóstico é obrigatório")
        String diagnostico,

        @NotBlank(message = "Tratamento é obrigatório")
        String tratamento,

        @NotBlank(message = "Observação é obrigatória")
        String observacao

) {

}
