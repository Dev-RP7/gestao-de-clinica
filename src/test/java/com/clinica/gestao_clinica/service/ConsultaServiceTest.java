package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.exception.RegraNegocioException;
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import com.clinica.gestao_clinica.repository.PacienteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultaServiceTest {

    private static final LocalDateTime DATA = LocalDate.now().plusDays(2).atTime(9, 0);

    @Mock
    private ConsultaRepository consultaRepository;
    @Mock
    private MedicoRepository medicoRepository;
    @Mock
    private PacienteRepository pacienteRepository;

    @InjectMocks
    private ConsultaService consultaService;

    @Test
    void agendarDeveCriarConsultaAgendada() {
        Medico medico = medico(true);
        Paciente paciente = paciente(true);
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        when(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(eq(1L), eq(DATA), anyCollection())).thenReturn(false);
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocacao -> {
            Consulta c = invocacao.getArgument(0);
            c.setId(10L);
            return c;
        });

        ConsultaResponseDTO resposta = consultaService.agendar(new ConsultaRequestDTO(DATA, "Dor de cabeça", 1L, 2L));

        assertThat(resposta.id()).isEqualTo(10L);
        assertThat(resposta.statusConsulta()).isEqualTo(StatusConsulta.AGENDADA);
        assertThat(resposta.nomeMedico()).isEqualTo("Dra. Ana");
    }

    @Test
    void agendarComHorarioOcupadoDeveLancarConflito() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico(true)));
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente(true)));
        when(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(eq(1L), eq(DATA), anyCollection())).thenReturn(true);

        assertThatThrownBy(() -> consultaService.agendar(new ConsultaRequestDTO(DATA, "Retorno", 1L, 2L)))
                .isInstanceOf(ConflitoException.class);
        verify(consultaRepository, never()).save(any());
    }

    @Test
    void agendarComMedicoInexistenteDeveLancarNaoEncontrado() {
        when(medicoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> consultaService.agendar(new ConsultaRequestDTO(DATA, "Retorno", 99L, 2L)))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining("99");
    }

    @Test
    void agendarComMedicoDesativadoDeveLancarRegraNegocio() {
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico(false)));

        assertThatThrownBy(() -> consultaService.agendar(new ConsultaRequestDTO(DATA, "Retorno", 1L, 2L)))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void cancelarConsultaRealizadaDeveLancarRegraNegocio() {
        Consulta consulta = consulta(StatusConsulta.REALIZADA);
        when(consultaRepository.findById(5L)).thenReturn(Optional.of(consulta));

        assertThatThrownBy(() -> consultaService.cancelar(5L))
                .isInstanceOf(RegraNegocioException.class);
        assertThat(consulta.getStatusConsulta()).isEqualTo(StatusConsulta.REALIZADA);
    }

    @Test
    void confirmarConsultaAgendada() {
        Consulta consulta = consulta(StatusConsulta.AGENDADA);
        when(consultaRepository.findById(5L)).thenReturn(Optional.of(consulta));

        ConsultaResponseDTO resposta = consultaService.confirmar(5L);

        assertThat(resposta.statusConsulta()).isEqualTo(StatusConsulta.CONFIRMADA);
    }

    @Test
    void listarComPeriodoInvertidoDeveLancarRegraNegocio() {
        LocalDate inicio = LocalDate.now().plusDays(10);
        LocalDate fim = LocalDate.now();

        assertThatThrownBy(() -> consultaService.listar(null, null, null, inicio, fim, null))
                .isInstanceOf(RegraNegocioException.class);
    }

    private Medico medico(boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Dra. Ana");
        usuario.setAtivo(ativo);
        Medico medico = new Medico();
        medico.setId(1L);
        medico.setUsuario(usuario);
        medico.setEspecialidade(new Especialidade(1L, "Clínica Geral"));
        return medico;
    }

    private Paciente paciente(boolean ativo) {
        Usuario usuario = new Usuario();
        usuario.setNome("Carlos");
        usuario.setAtivo(ativo);
        Paciente paciente = new Paciente();
        paciente.setId(2L);
        paciente.setUsuario(usuario);
        return paciente;
    }

    private Consulta consulta(StatusConsulta status) {
        Consulta consulta = new Consulta();
        consulta.setId(5L);
        consulta.setMedico(medico(true));
        consulta.setPaciente(paciente(true));
        consulta.setDataConsulta(DATA);
        consulta.setMotivo("Rotina");
        consulta.setStatusConsulta(status);
        return consulta;
    }
}
