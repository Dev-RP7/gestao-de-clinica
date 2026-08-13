package com.clinica.gestao_clinica.dto.request;

public record ProntuarioAtualizadoRequestDTO(

        double peso,

        double altura,

        double pressao,

        double temperatura,

        String diagnostico,

        String tratamento,

        String observacao

) {
}
