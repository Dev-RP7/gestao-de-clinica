package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ConsultaRepositoryTest {

    private static final LocalDateTime AMANHA_10H = LocalDate.now().plusDays(1).atTime(10, 0);

    @Autowired
    private ConsultaRepository consultaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EspecialidadeRepository especialidadeRepository;
    @Autowired
    private MedicoRepository medicoRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    private Medico medicoA;
    private Medico medicoB;
    private Paciente paciente;

    @BeforeEach
    void prepararDados() {
        Especialidade cardiologia = especialidadeRepository.save(new Especialidade(null, "Cardiologia"));
        medicoA = criarMedico("Dra. Ana", "ana@teste.com", "CRM-1", cardiologia);
        medicoB = criarMedico("Dr. Bruno", "bruno@teste.com", "CRM-2", cardiologia);
        paciente = criarPaciente();

        criarConsulta(medicoA, AMANHA_10H, StatusConsulta.AGENDADA);
        criarConsulta(medicoA, AMANHA_10H.plusDays(4), StatusConsulta.CANCELADA);
        criarConsulta(medicoB, AMANHA_10H.plusHours(1), StatusConsulta.CONFIRMADA);
    }

    @Test
    void filtrarSemFiltrosDeveTrazerTodas() {
        Page<Consulta> pagina = consultaRepository.filtrar(null, null, null, null, null, PageRequest.of(0, 10));
        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    @Test
    void filtrarPorMedico() {
        Page<Consulta> pagina = consultaRepository.filtrar(medicoA.getId(), null, null, null, null, PageRequest.of(0, 10));
        assertThat(pagina.getTotalElements()).isEqualTo(2);
        assertThat(pagina.getContent()).allMatch(c -> c.getMedico().getId().equals(medicoA.getId()));
    }

    @Test
    void filtrarPorMedicoEStatus() {
        Page<Consulta> pagina = consultaRepository.filtrar(medicoA.getId(), null, StatusConsulta.AGENDADA, null, null, PageRequest.of(0, 10));
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    @Test
    void filtrarPorPeriodo() {
        LocalDateTime inicio = AMANHA_10H.toLocalDate().atStartOfDay();
        LocalDateTime fim = AMANHA_10H.toLocalDate().atTime(23, 59);
        Page<Consulta> pagina = consultaRepository.filtrar(null, null, null, inicio, fim, PageRequest.of(0, 10));
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    @Test
    void filtrarDevePaginar() {
        Page<Consulta> pagina = consultaRepository.filtrar(null, null, null, null, null, PageRequest.of(0, 2));
        assertThat(pagina.getContent()).hasSize(2);
        assertThat(pagina.getTotalElements()).isEqualTo(3);
        assertThat(pagina.getTotalPages()).isEqualTo(2);
    }

    @Test
    void deveDetectarHorarioOcupado() {
        List<StatusConsulta> ocupam = List.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA);
        assertThat(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(medicoA.getId(), AMANHA_10H, ocupam)).isTrue();
        assertThat(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(medicoA.getId(), AMANHA_10H.plusDays(4), ocupam)).isFalse();
    }

    private Medico criarMedico(String nome, String email, String crm, Especialidade especialidade) {
        Usuario usuario = usuarioRepository.save(novoUsuario(nome, email, TipoUsuario.MEDICO));
        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setCrm(crm);
        medico.setEspecialidade(especialidade);
        return medicoRepository.save(medico);
    }

    private Paciente criarPaciente() {
        Usuario usuario = usuarioRepository.save(novoUsuario("Carlos", "carlos@teste.com", TipoUsuario.PACIENTE));
        Paciente p = new Paciente();
        p.setUsuario(usuario);
        p.setCpf("12345678901");
        p.setRg("RG-1");
        p.setDataNascimento(LocalDate.of(1990, 1, 1));
        p.setSexo(Sexo.MASCULINO);
        p.setEndereco("Rua A, 1");
        return pacienteRepository.save(p);
    }

    private Usuario novoUsuario(String nome, String email, TipoUsuario tipo) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha("senha");
        usuario.setTelefone("11999999999");
        usuario.setTipoUsuario(tipo);
        usuario.setAtivo(true);
        return usuario;
    }

    private void criarConsulta(Medico medico, LocalDateTime data, StatusConsulta status) {
        Consulta consulta = new Consulta();
        consulta.setMedico(medico);
        consulta.setPaciente(paciente);
        consulta.setDataConsulta(data);
        consulta.setStatusConsulta(status);
        consulta.setMotivo("Rotina");
        consultaRepository.save(consulta);
    }
}
