// Pacote dos testes de repositório.
package com.clinica.gestao_clinica.repository;

// Entidades e enums usados para montar os dados de teste.
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Anotações do JUnit.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
// Injeção de dependências no teste.
import org.springframework.beans.factory.annotation.Autowired;
// Teste "fatiado" de JPA: sobe só entidades e repositórios, com banco em memória.
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
// Perfil de teste.
import org.springframework.test.context.ActiveProfiles;

// Datas e lista.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

// AssertJ: verificações legíveis, como assertThat(x).isEqualTo(y).
import static org.assertj.core.api.Assertions.assertThat;

// Sobe só a camada JPA. Cada teste roda numa transação que é desfeita no final (o banco volta limpo).
@DataJpaTest
// Usa o application-test.properties.
@ActiveProfiles("test")
// Testes das consultas JPQL do ConsultaRepository.
class ConsultaRepositoryTest {

    // Data/hora fixa no futuro, usada como base nos testes.
    private static final LocalDateTime AMANHA_10H = LocalDate.now().plusDays(1).atTime(10, 0);

    // O Spring injeta os repositórios reais.
    @Autowired
    // Repositório testado.
    private ConsultaRepository consultaRepository;
    // Injeção.
    @Autowired
    // Repositório auxiliar.
    private UsuarioRepository usuarioRepository;
    // Injeção.
    @Autowired
    // Repositório auxiliar.
    private EspecialidadeRepository especialidadeRepository;
    // Injeção.
    @Autowired
    // Repositório auxiliar.
    private MedicoRepository medicoRepository;
    // Injeção.
    @Autowired
    // Repositório auxiliar.
    private PacienteRepository pacienteRepository;

    // Dois médicos e um paciente criados antes de cada teste.
    private Medico medicoA;
    // Segundo médico.
    private Medico medicoB;
    // Paciente.
    private Paciente paciente;

    // @BeforeEach: roda antes de CADA teste, preparando os dados.
    @BeforeEach
    // Cria médicos, paciente e consultas.
    void prepararDados() {
        // Cria e salva uma especialidade.
        Especialidade cardiologia = especialidadeRepository.save(new Especialidade(null, "Cardiologia"));
        // Cria o médico A.
        medicoA = criarMedico("Dra. Ana", "ana@teste.com", "CRM-1", cardiologia);
        // Cria o médico B.
        medicoB = criarMedico("Dr. Bruno", "bruno@teste.com", "CRM-2", cardiologia);
        // Cria o paciente.
        paciente = criarPaciente();

        // Consulta 1: médico A, amanhã às 10h, AGENDADA.
        criarConsulta(medicoA, AMANHA_10H, StatusConsulta.AGENDADA);
        // Consulta 2: médico A, daqui a 5 dias, CANCELADA.
        criarConsulta(medicoA, AMANHA_10H.plusDays(4), StatusConsulta.CANCELADA);
        // Consulta 3: médico B, amanhã às 11h, CONFIRMADA.
        criarConsulta(medicoB, AMANHA_10H.plusHours(1), StatusConsulta.CONFIRMADA);
    }

    // Marca como teste.
    @Test
    // Sem filtros, todas as consultas devem vir.
    void filtrarSemFiltrosDeveTrazerTodas() {
        // Executa a busca com todos os filtros nulos.
        Page<Consulta> pagina = consultaRepository.filtrar(null, null, null, null, null, PageRequest.of(0, 10));
        // Verifica que vieram as 3 consultas.
        assertThat(pagina.getTotalElements()).isEqualTo(3);
    }

    // Marca como teste.
    @Test
    // Filtrar por médico deve trazer só as consultas dele.
    void filtrarPorMedico() {
        // Busca as consultas do médico A.
        Page<Consulta> pagina = consultaRepository.filtrar(medicoA.getId(), null, null, null, null, PageRequest.of(0, 10));
        // O médico A tem 2 consultas.
        assertThat(pagina.getTotalElements()).isEqualTo(2);
        // Todas devem ser do médico A.
        assertThat(pagina.getContent()).allMatch(c -> c.getMedico().getId().equals(medicoA.getId()));
    }

    // Marca como teste.
    @Test
    // Filtros combinados: médico A + status AGENDADA.
    void filtrarPorMedicoEStatus() {
        // Busca.
        Page<Consulta> pagina = consultaRepository.filtrar(medicoA.getId(), null, StatusConsulta.AGENDADA, null, null, PageRequest.of(0, 10));
        // Só uma consulta atende aos dois filtros.
        assertThat(pagina.getTotalElements()).isEqualTo(1);
    }

    // Marca como teste.
    @Test
    // Filtro por período: só o dia de amanhã.
    void filtrarPorPeriodo() {
        // Início: amanhã às 00:00.
        LocalDateTime inicio = AMANHA_10H.toLocalDate().atStartOfDay();
        // Fim: amanhã às 23:59.
        LocalDateTime fim = AMANHA_10H.toLocalDate().atTime(23, 59);
        // Busca.
        Page<Consulta> pagina = consultaRepository.filtrar(null, null, null, inicio, fim, PageRequest.of(0, 10));
        // Amanhã há 2 consultas (10h e 11h).
        assertThat(pagina.getTotalElements()).isEqualTo(2);
    }

    // Marca como teste.
    @Test
    // A paginação deve respeitar o tamanho pedido.
    void filtrarDevePaginar() {
        // Pede páginas de 2 itens.
        Page<Consulta> pagina = consultaRepository.filtrar(null, null, null, null, null, PageRequest.of(0, 2));
        // A página tem 2 itens...
        assertThat(pagina.getContent()).hasSize(2);
        // ...o total continua 3...
        assertThat(pagina.getTotalElements()).isEqualTo(3);
        // ...e são necessárias 2 páginas.
        assertThat(pagina.getTotalPages()).isEqualTo(2);
    }

    // Marca como teste.
    @Test
    // Detecção de horário ocupado: só conta consultas AGENDADAS ou CONFIRMADAS.
    void deveDetectarHorarioOcupado() {
        // Status que ocupam o horário.
        List<StatusConsulta> ocupam = List.of(StatusConsulta.AGENDADA, StatusConsulta.CONFIRMADA);
        // Amanhã às 10h o médico A está ocupado.
        assertThat(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(medicoA.getId(), AMANHA_10H, ocupam)).isTrue();
        // Daqui a 5 dias a consulta foi cancelada, então o horário está livre.
        assertThat(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(medicoA.getId(), AMANHA_10H.plusDays(4), ocupam)).isFalse();
    }

    // ===== Métodos auxiliares para criar os dados =====

    // Cria um usuário e um médico ligados.
    private Medico criarMedico(String nome, String email, String crm, Especialidade especialidade) {
        // Salva o usuário.
        Usuario usuario = usuarioRepository.save(novoUsuario(nome, email, TipoUsuario.MEDICO));
        // Cria o médico.
        Medico medico = new Medico();
        // Liga o usuário.
        medico.setUsuario(usuario);
        // CRM.
        medico.setCrm(crm);
        // Especialidade.
        medico.setEspecialidade(especialidade);
        // Salva e devolve.
        return medicoRepository.save(medico);
    }

    // Cria um usuário e um paciente ligados.
    private Paciente criarPaciente() {
        // Salva o usuário.
        Usuario usuario = usuarioRepository.save(novoUsuario("Carlos", "carlos@teste.com", TipoUsuario.PACIENTE));
        // Cria o paciente.
        Paciente p = new Paciente();
        // Liga o usuário.
        p.setUsuario(usuario);
        // CPF.
        p.setCpf("12345678901");
        // RG.
        p.setRg("RG-1");
        // Data de nascimento.
        p.setDataNascimento(LocalDate.of(1990, 1, 1));
        // Sexo.
        p.setSexo(Sexo.MASCULINO);
        // Endereço.
        p.setEndereco("Rua A, 1");
        // Salva e devolve.
        return pacienteRepository.save(p);
    }

    // Cria um objeto Usuario (sem salvar).
    private Usuario novoUsuario(String nome, String email, TipoUsuario tipo) {
        // Cria o usuário vazio.
        Usuario usuario = new Usuario();
        // Nome.
        usuario.setNome(nome);
        // E-mail.
        usuario.setEmail(email);
        // Senha qualquer (este teste não faz login).
        usuario.setSenha("senha");
        // Telefone.
        usuario.setTelefone("11999999999");
        // Perfil.
        usuario.setTipoUsuario(tipo);
        // Ativo.
        usuario.setAtivo(true);
        // Devolve.
        return usuario;
    }

    // Cria e salva uma consulta.
    private void criarConsulta(Medico medico, LocalDateTime data, StatusConsulta status) {
        // Cria a consulta vazia.
        Consulta consulta = new Consulta();
        // Médico.
        consulta.setMedico(medico);
        // Paciente.
        consulta.setPaciente(paciente);
        // Data.
        consulta.setDataConsulta(data);
        // Status.
        consulta.setStatusConsulta(status);
        // Motivo.
        consulta.setMotivo("Rotina");
        // Salva.
        consultaRepository.save(consulta);
    }
}
