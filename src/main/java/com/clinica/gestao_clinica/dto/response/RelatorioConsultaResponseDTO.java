// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Resumo com a quantidade de consultas em cada status.
public record RelatorioConsultaResponseDTO(

        // Total de consultas.
        long total,
        // Quantas estão agendadas.
        long agendadas,
        // Quantas estão confirmadas.
        long confirmadas,
        // Quantas já foram realizadas.
        long realizadas,
        // Quantas foram canceladas.
        long canceladas

) {
}
