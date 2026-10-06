// Pacote dos DTOs de saída (dados que a API devolve).
package com.clinica.gestao_clinica.dto.response;

// Enum de status.
import com.clinica.gestao_clinica.enums.StatusConsulta;

// Data e hora.
import java.time.LocalDateTime;

// Dados de uma consulta devolvidos pela API. Usamos DTO em vez da entidade para controlar o que é exposto.
public record ConsultaResponseDTO(

        // Id da consulta.
        Long id,
        // Data e hora.
        LocalDateTime dataConsulta,
        // Situação atual.
        StatusConsulta statusConsulta,
        // Motivo da consulta.
        String motivo,
        // Id do médico.
        Long medicoId,
        // Nome do médico.
        String nomeMedico,
        // Id do paciente.
        Long pacienteId,
        // Nome do paciente.
        String nomePaciente

) {
}
