// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

// Dados para criar ou alterar uma receita. Na alteração, o prontuarioId é ignorado (a receita não muda de prontuário).
public record ReceitaRequestDTO(

        // Não pode ser nulo.
        @NotNull(message = "Prontuário é obrigatório")
        // Id do prontuário.
        Long prontuarioId,

        // Não pode ser vazio.
        @NotBlank(message = "Medicamento é obrigatório")
        // Nome do medicamento.
        String medicamento,

        // Não pode ser vazio.
        @NotBlank(message = "Dosagem é obrigatória")
        // Dose.
        String dosagem,

        // Não pode ser vazio.
        @NotBlank(message = "Frequência é obrigatória")
        // Frequência.
        String frequencia,

        // Não pode ser vazio.
        @NotBlank(message = "Duração é obrigatória")
        // Duração.
        String duracao,

        // Não pode ser vazio.
        @NotBlank(message = "Observação é obrigatória")
        // Observação.
        String observacao

) {
}
