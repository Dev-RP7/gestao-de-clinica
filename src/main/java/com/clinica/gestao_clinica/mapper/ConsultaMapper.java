// Pacote dos mappers (conversores entre entidade e DTO).
package com.clinica.gestao_clinica.mapper;

// DTOs e entidades envolvidos.
import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
// Enum de status.
import com.clinica.gestao_clinica.enums.StatusConsulta;

// Classe utilitária: só tem métodos estáticos, então não precisa ser instanciada.
public class ConsultaMapper {

    // Construtor privado impede que alguém faça "new ConsultaMapper()".
    private ConsultaMapper() {
    }

    // Cria uma entidade Consulta a partir do DTO e das entidades já buscadas no banco.
    public static Consulta toEntity(ConsultaRequestDTO dto, Medico medico, Paciente paciente) {
        // Cria a consulta vazia.
        Consulta consulta = new Consulta();
        // Copia a data.
        consulta.setDataConsulta(dto.dataConsulta());
        // Toda consulta nova começa como AGENDADA.
        consulta.setStatusConsulta(StatusConsulta.AGENDADA);
        // Copia o motivo.
        consulta.setMotivo(dto.motivo());
        // Liga o médico.
        consulta.setMedico(medico);
        // Liga o paciente.
        consulta.setPaciente(paciente);
        // Devolve a entidade pronta para salvar.
        return consulta;
    }

    // Converte a entidade em DTO de resposta.
    public static ConsultaResponseDTO toResponse(Consulta consulta) {
        // Cria o record passando os valores na ordem dos campos.
        return new ConsultaResponseDTO(
                // Id da consulta.
                consulta.getId(),
                // Data.
                consulta.getDataConsulta(),
                // Status.
                consulta.getStatusConsulta(),
                // Motivo.
                consulta.getMotivo(),
                // Id do MÉDICO (antes, a listagem devolvia por engano o id do usuário).
                consulta.getMedico().getId(),
                // Nome do médico (fica no Usuario dele).
                consulta.getMedico().getUsuario().getNome(),
                // Id do paciente.
                consulta.getPaciente().getId(),
                // Nome do paciente.
                consulta.getPaciente().getUsuario().getNome()
        );
    }
}
