package com.clinica.gestao_clinica.dto.response;

public record ReceitaResponseDTO(

        Long id,
        Long prontuarioId,
        String medicamento,
        String dosagem,
        String frequencia,
        String duracao,
        String observacao

) {
}
