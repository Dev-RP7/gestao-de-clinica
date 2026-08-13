package com.clinica.gestao_clinica.dto.response;

import com.clinica.gestao_clinica.enums.StatusConsulta;

import java.time.LocalDateTime;

public record ConsultaResponseDTO(

        Long id,
        LocalDateTime dataConsulta,
        StatusConsulta statusConsulta,
        Long medicoId,
        String nomeMedico,
        Long pacienteId,
        String nomePaciente

) {
}
