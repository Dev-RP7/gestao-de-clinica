package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ProntuarioRequestDTO(

        @NotNull
        Long consultaId,

        @Positive
        double peso,

        @Positive
        double altura,

        @Positive
        double pressao,

        @Positive
        double temperatura,

        @NotBlank
        String diagnostico,

        @NotBlank
        String tratamento,

        @NotBlank
        String observacao

) {

}
