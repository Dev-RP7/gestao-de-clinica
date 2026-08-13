package com.clinica.gestao_clinica.mapper;

import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;

public class ConsultaMapper {

    private ConsultaMapper() {
    }

    public static Consulta toEntity(
            ConsultaRequestDTO dto,
            Medico medico,
            Paciente paciente) {

        Consulta consulta = new Consulta();

        consulta.setDataConsulta(dto.dataConsulta());
        consulta.setStatusConsulta(dto.statusConsulta());
        consulta.setMotivo(dto.motivo());
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);

        return consulta;
    }

    public static ConsultaResponseDTO toResponse(Consulta consulta) {

        return new ConsultaResponseDTO(
                consulta.getId(),
                consulta.getDataConsulta(),
                consulta.getStatusConsulta(),
                consulta.getMedico().getId(),
                consulta.getMedico().getUsuario().getNome(),
                consulta.getPaciente().getId(),
                consulta.getPaciente().getUsuario().getNome()
        );
    }
}
