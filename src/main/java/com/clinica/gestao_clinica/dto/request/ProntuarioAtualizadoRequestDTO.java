// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

// Dados para ATUALIZAR um prontuário. Não tem consultaId: o prontuário não muda de consulta.
public record ProntuarioAtualizadoRequestDTO(

        // Maior que zero.
        @Positive(message = "Peso deve ser maior que zero")
        // Peso em kg.
        double peso,

        // Maior que zero.
        @Positive(message = "Altura deve ser maior que zero")
        // Altura em metros.
        double altura,

        // Maior que zero.
        @Positive(message = "Pressão deve ser maior que zero")
        // Pressão arterial.
        double pressao,

        // Maior que zero.
        @Positive(message = "Temperatura deve ser maior que zero")
        // Temperatura em °C.
        double temperatura,

        // Não pode ser vazio.
        @NotBlank(message = "Diagnóstico é obrigatório")
        // Diagnóstico.
        String diagnostico,

        // Não pode ser vazio.
        @NotBlank(message = "Tratamento é obrigatório")
        // Tratamento.
        String tratamento,

        // Não pode ser vazio.
        @NotBlank(message = "Observação é obrigatória")
        // Observação.
        String observacao

) {
}
