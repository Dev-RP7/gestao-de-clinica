package com.clinica.gestao_clinica.dto.request;

import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteRequestDTO(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String senha,

        @NotBlank(message = "Telefone é obrigatório")
        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        String telefone,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos, sem pontos ou traço")
        String cpf,

        @NotBlank(message = "RG é obrigatório")
        String rg,

        @NotNull(message = "Sexo é obrigatório")
        Sexo sexo,

        TipoSanguineo tipoSanguineo,

        String alergias,

        String observacao,

        @NotNull(message = "Data de nascimento é obrigatória")
        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        @NotBlank(message = "Endereço é obrigatório")
        String endereco

) {
}
