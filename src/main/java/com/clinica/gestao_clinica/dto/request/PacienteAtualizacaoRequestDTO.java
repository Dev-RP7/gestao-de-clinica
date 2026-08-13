package com.clinica.gestao_clinica.dto.request;

import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record PacienteAtualizacaoRequestDTO(

        String nome,

        @Email(message = "Email inválido")
        String email,

        String telefone,

        String senha,

        String endereco,

        LocalDate dataNascimento,

        Sexo sexo,

        TipoSanguineo tipoSanguineo,

        String alergias,

        String observacao





) {
}
