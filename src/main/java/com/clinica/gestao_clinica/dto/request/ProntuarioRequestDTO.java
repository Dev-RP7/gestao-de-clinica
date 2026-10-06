// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Anotações de validação.
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// Dados para CRIAR um prontuário para uma consulta.
public record ProntuarioRequestDTO(

        // Não pode ser nulo.
        @NotNull(message = "Consulta é obrigatória")
        // Id da consulta.
        Long consultaId,

        // Precisa ser maior que zero.
        @Positive(message = "Peso deve ser maior que zero")
        // Peso em kg.
        double peso,

        // Precisa ser maior que zero.
        @Positive(message = "Altura deve ser maior que zero")
        // Altura em metros.
        double altura,

        // Precisa ser maior que zero.
        @Positive(message = "Pressão deve ser maior que zero")
        // Pressão arterial.
        double pressao,

        // Precisa ser maior que zero.
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
