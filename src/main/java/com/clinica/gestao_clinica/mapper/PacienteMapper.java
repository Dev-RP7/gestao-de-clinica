package com.clinica.gestao_clinica.mapper;

import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.dto.response.PacienteResponseDTO;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;

public class PacienteMapper {

    private PacienteMapper() {
    }

    public static Paciente toEntity(PacienteRequestDTO dto, Usuario usuario) {
        Paciente paciente = new Paciente();
        paciente.setUsuario(usuario);
        paciente.setCpf(dto.cpf());
        paciente.setRg(dto.rg());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setSexo(dto.sexo());
        paciente.setEndereco(dto.endereco());
        paciente.setTipoSanguineo(dto.tipoSanguineo());
        paciente.setAlergias(dto.alergias());
        paciente.setObservacao(dto.observacao());
        return paciente;
    }

    public static PacienteResponseDTO toResponse(Paciente paciente) {
        Usuario usuario = paciente.getUsuario();
        return new PacienteResponseDTO(
                paciente.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                paciente.getCpf(),
                paciente.getRg(),
                paciente.getDataNascimento(),
                paciente.getSexo(),
                paciente.getTipoSanguineo(),
                paciente.getEndereco(),
                paciente.getAlergias(),
                paciente.getObservacao()
        );
    }
}
