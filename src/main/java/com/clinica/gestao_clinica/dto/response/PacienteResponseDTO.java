package com.clinica.gestao_clinica.dto.response;

import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;

import java.time.LocalDate;

public record PacienteResponseDTO(

        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        String rg,
        LocalDate dataNascimento,
        Sexo sexo,
        TipoSanguineo tipoSanguineo,
        String endereco,
        String alergias,
        String observacao

) {
}
