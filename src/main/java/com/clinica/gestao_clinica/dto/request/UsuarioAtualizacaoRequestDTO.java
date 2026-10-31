package com.clinica.gestao_clinica.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record UsuarioAtualizacaoRequestDTO(

        String nome,

        @Email(message = "Email inválido")
        String email,

        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        String telefone

) {
}
