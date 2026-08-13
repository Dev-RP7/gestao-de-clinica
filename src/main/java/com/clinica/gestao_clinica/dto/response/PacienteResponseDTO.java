package com.clinica.gestao_clinica.dto.response;

import java.time.LocalDate;

public record PacienteResponseDTO(

        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        LocalDate dataNacimento,
        String endereco

) {
}
