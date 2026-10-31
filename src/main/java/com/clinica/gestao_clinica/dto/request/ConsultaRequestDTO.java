package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ConsultaRequestDTO(

        @NotNull(message = "Data da consulta é obrigatória")
        @Future(message = "A consulta deve ser marcada para uma data futura")
        LocalDateTime dataConsulta,

        @NotBlank(message = "Motivo é obrigatório")
        String motivo,

        @NotNull(message = "Médico é obrigatório")
        Long medicoId,

        @NotNull(message = "Paciente é obrigatório")
        Long pacienteId

) {
}
