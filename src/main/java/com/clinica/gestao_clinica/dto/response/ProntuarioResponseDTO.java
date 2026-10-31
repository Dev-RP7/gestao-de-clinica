package com.clinica.gestao_clinica.dto.response;

public record ProntuarioResponseDTO(

        Long id,
        Long consultaId,
        double peso,
        double altura,
        double pressao,
        double temperatura,
        String diagnostico,
        String tratamento,
        String observacao

) {
}
