package com.clinica.gestao_clinica.dto.response;

public record RelatorioConsultaResponseDTO(

        Long total,
        Long agendadas,
        Long confirmadas,
        Long concluidas,
        Long canceladas
) {
}
