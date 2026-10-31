package com.clinica.gestao_clinica.mapper;

import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Usuario;

public class MedicoMapper {

    private MedicoMapper() {
    }

    public static MedicoResponseDTO toResponse(Medico medico) {
        Usuario usuario = medico.getUsuario();
        return new MedicoResponseDTO(
                medico.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                medico.getCrm(),
                medico.getEspecialidade().getNome()
        );
    }
}
