package com.clinica.gestao_clinica.dto.response;

import com.clinica.gestao_clinica.enums.TipoUsuario;

public record LoginResponseDTO(

        String token,
        String tipo,
        long expiraEmSegundos,
        String nome,
        TipoUsuario tipoUsuario

) {
}
