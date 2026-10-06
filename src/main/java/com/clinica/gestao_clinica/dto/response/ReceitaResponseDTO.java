// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Dados de uma receita devolvidos pela API.
public record ReceitaResponseDTO(

        // Id da receita.
        Long id,
        // Id do prontuário.
        Long prontuarioId,
        // Medicamento.
        String medicamento,
        // Dosagem.
        String dosagem,
        // Frequência.
        String frequencia,
        // Duração.
        String duracao,
        // Observação.
        String observacao

) {
}
