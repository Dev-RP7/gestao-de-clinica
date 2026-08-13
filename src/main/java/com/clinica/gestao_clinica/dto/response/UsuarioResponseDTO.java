package com.clinica.gestao_clinica.dto.response;

import com.clinica.gestao_clinica.enums.TipoUsuario;

public record UsuarioResponseDTO(

    Long id,

    String nome,

    String email,

    String telefone,

    TipoUsuario tipoUsuario,

    Boolean ativo

) {
}
