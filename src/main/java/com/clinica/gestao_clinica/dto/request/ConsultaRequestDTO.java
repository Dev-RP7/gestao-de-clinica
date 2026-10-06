// Pacote dos DTOs de entrada (dados que chegam do cliente).
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Data e hora.
import java.time.LocalDateTime;

// DTO (Data Transfer Object) = objeto só para transportar dados. Usado para AGENDAR e REMARCAR consultas.
// O status não vem aqui: toda consulta nova começa AGENDADA, e as mudanças de status têm rotas próprias.
public record ConsultaRequestDTO(

        // Não pode ser nulo.
        @NotNull(message = "Data da consulta é obrigatória")
        // Precisa ser uma data/hora no futuro.
        @Future(message = "A consulta deve ser marcada para uma data futura")
        // Data e hora da consulta (formato JSON: "2026-12-01T14:30:00").
        LocalDateTime dataConsulta,

        // Não pode ser nulo, vazio nem só espaços.
        @NotBlank(message = "Motivo é obrigatório")
        // Motivo da consulta.
        String motivo,

        // Não pode ser nulo.
        @NotNull(message = "Médico é obrigatório")
        // Id do médico.
        Long medicoId,

        // Não pode ser nulo.
        @NotNull(message = "Paciente é obrigatório")
        // Id do paciente.
        Long pacienteId

) {
}
