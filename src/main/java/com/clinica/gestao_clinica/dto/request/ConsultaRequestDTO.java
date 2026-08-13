package com.clinica.gestao_clinica.dto.request;

import com.clinica.gestao_clinica.enums.StatusConsulta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaRequestDTO(

        @NotNull
        LocalDateTime dataConsulta,

        @NotNull
        StatusConsulta statusConsulta,

        @NotBlank
        String motivo,

        @NotNull
        Long medicoId,

        @NotNull
        Long pacienteId

) {
}
