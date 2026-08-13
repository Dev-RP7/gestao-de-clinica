package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.mapper.ConsultaMapper;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import com.clinica.gestao_clinica.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;

    public ConsultaService(
            ConsultaRepository consultaRepository,
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository) {
        this.consultaRepository = consultaRepository;
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
    }

    public ConsultaResponseDTO cadastrar(ConsultaRequestDTO dto) {

        Medico medico = medicoRepository.findById(dto.medicoId())
                .orElseThrow(() -> new RuntimeException("Medico não encontrado"));

        Paciente paciente = pacienteRepository.findById(dto.pacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Consulta consulta = new Consulta();
        consulta.setDataConsulta(dto.dataConsulta());
        consulta.setStatusConsulta(dto.statusConsulta());
        consulta.setMotivo(dto.motivo());
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);

        consulta =  consultaRepository.save(consulta);

        return ConsultaMapper.toResponse(consulta);

    }

    public List<ConsultaResponseDTO> listarTodas() {

        return consultaRepository.findAll()
                .stream()
                .map(consulta -> new ConsultaResponseDTO(
                        consulta.getId(),
                        consulta.getDataConsulta(),
                        consulta.getStatusConsulta(),
                        consulta.getMedico().getUsuario().getId(),
                        consulta.getMedico().getUsuario().getNome(),
                        consulta.getPaciente().getUsuario().getId(),
                        consulta.getPaciente().getUsuario().getNome()
                ))
                .toList();
    }

    public ConsultaResponseDTO buscarConsultaPorId(Long id) {

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        return ConsultaMapper.toResponse(consulta);

    }

    public List<ConsultaResponseDTO> listarConsultasDoPaciente(Long pacienteId){

        return consultaRepository.findByPacienteId(pacienteId)
                .stream()
                .map(ConsultaMapper::toResponse)
                .toList();
    }

    public Consulta buscarPorId(Long id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }

    public List<ConsultaResponseDTO> buscarPorMedico(String nome) {

        List<Consulta> consultas = consultaRepository
                .findByMedico(nome);

        return consultas.stream()
                .map(ConsultaMapper::toResponse)
                .toList();
    }

    public List<ConsultaResponseDTO> buscarPorStatus(StatusConsulta status) {

        List<Consulta> consultas = consultaRepository.findByStatusConsulta(status);

        return consultas.stream()
                .map(ConsultaMapper::toResponse)
                .toList();

    }

    public List<ConsultaResponseDTO> buscarPorDataConsulta(LocalDate data) {

        LocalDateTime inicio = data.atStartOfDay();
        LocalDateTime fim = data.atTime(LocalTime.MAX);

        return consultaRepository
                .findByDataConsulta(inicio, fim)
                .stream()
                .map(ConsultaMapper::toResponse)
                .toList();
    }


    public ConsultaResponseDTO atualizar(Long id, ConsultaRequestDTO consultaAtualizada) {

        Consulta consulta = buscarPorId(id);

        consulta.setDataConsulta(consultaAtualizada.dataConsulta());
        consulta.setStatusConsulta(consultaAtualizada.statusConsulta());
        consulta.setMotivo(consultaAtualizada.motivo());

        return ConsultaMapper.toResponse(consulta);
    }

    public ConsultaResponseDTO cancelarConsulta(Long id) {

        Consulta consulta = buscarPorId(id);

        consulta.setStatusConsulta(StatusConsulta.CANCELADA);

        consulta = consultaRepository.save(consulta);

        return ConsultaMapper.toResponse(consulta);
    }

    public ConsultaResponseDTO confirmarConsulta(Long id) {

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new ResourceAccessException("Consulta não encontrada!"));

        consulta.setStatusConsulta(StatusConsulta.CONFIRMADA);

        consultaRepository.save(consulta);

        return ConsultaMapper.toResponse(consulta);
    }

    public ConsultaResponseDTO concluirConsulta(Long id) {

        Consulta consulta = buscarPorId(id);

        consulta.setStatusConsulta(StatusConsulta.REALIZADA);

        consulta = consultaRepository.save(consulta);

        return ConsultaMapper.toResponse(consulta);

    }
}
