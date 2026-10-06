// Pacote dos DTOs de saída.
package com.clinica.gestao_clinica.dto.response;

// Dados de uma especialidade devolvidos pela API.
public record EspecialidadeResponseDTO(

        // Id da especialidade.
        Long id,
        // Nome da especialidade.
        String nome

) {
}
