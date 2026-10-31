package com.clinica.gestao_clinica.dto.response;

public record RelatorioConsultaResponseDTO(

        long total,
        long agendadas,
        long confirmadas,
        long realizadas,
        long canceladas

) {
}
