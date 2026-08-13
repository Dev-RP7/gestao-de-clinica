package com.clinica.gestao_clinica.dto.request;

import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PacienteRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        String senha,

        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotBlank(message = "RG é obrigatório")
        String rg,

        @NotNull(message = "O seu Sexo é obrigatório")
        Sexo sexo,

        TipoSanguineo tipoSanguineo,

        String alergias,

        String observacao,

        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDate dataNascimento,

        @NotBlank(message = "Seu endereço é obrigatório")
        String endereco

) {
}
