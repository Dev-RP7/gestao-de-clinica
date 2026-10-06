// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Dados de um prontuário devolvidos pela API.
public record ProntuarioResponseDTO(

        // Id do prontuário.
        Long id,
        // Id da consulta.
        Long consultaId,
        // Peso.
        double peso,
        // Altura.
        double altura,
        // Pressão.
        double pressao,
        // Temperatura.
        double temperatura,
        // Diagnóstico.
        String diagnostico,
        // Tratamento.
        String tratamento,
        // Observação.
        String observacao

) {
}
