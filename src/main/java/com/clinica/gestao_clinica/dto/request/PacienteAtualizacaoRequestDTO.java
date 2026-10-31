package com.clinica.gestao_clinica.dto.request;

import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteAtualizacaoRequestDTO(

        String nome,

        @Email(message = "Email inválido")
        String email,

        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        String telefone,

        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        String senha,

        String endereco,

        @Past(message = "Data de nascimento deve estar no passado")
        LocalDate dataNascimento,

        Sexo sexo,

        TipoSanguineo tipoSanguineo,

        String alergias,

        String observacao

) {
}
