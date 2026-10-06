// Pacote dos testes de service.
package com.clinica.gestao_clinica.service;

// DTOs, entidades e enums.
import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.StatusConsulta;
// Exceções esperadas.
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.exception.RegraNegocioException;
// Repositórios (serão "mocks", ou seja, objetos falsos).
import com.clinica.gestao_clinica.repository.ConsultaRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import com.clinica.gestao_clinica.repository.PacienteRepository;
// JUnit.
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// Mockito: biblioteca para criar objetos falsos e controlar o que eles devolvem.
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

// Datas e Optional.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

// Verificações do AssertJ.
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Funções do Mockito (any, when, verify, never...).
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Liga o Mockito ao JUnit. Não sobe o Spring: o teste é rápido e isola só a regra de negócio.
@ExtendWith(MockitoExtension.class)
// Testes unitários do ConsultaService.
class ConsultaServiceTest {

    // Data futura usada nos testes.
    private static final LocalDateTime DATA = LocalDate.now().plusDays(2).atTime(9, 0);

    // @Mock cria um repositório falso: ele não acessa banco, só devolve o que mandarmos.
    @Mock
    // Mock do repositório de consultas.
    private ConsultaRepository consultaRepository;
    // Cria um mock.
    @Mock
    // Mock do repositório de médicos.
    private MedicoRepository medicoRepository;
    // Cria um mock.
    @Mock
    // Mock do repositório de pacientes.
    private PacienteRepository pacienteRepository;

    // @InjectMocks cria o service de verdade, passando os mocks acima no construtor.
    @InjectMocks
    // Classe testada.
    private ConsultaService consultaService;

    // Marca como teste.
    @Test
    // Caminho feliz: agendar com médico e paciente válidos e horário livre.
    void agendarDeveCriarConsultaAgendada() {
        // Cria um médico ativo de exemplo.
        Medico medico = medico(true);
        // Cria um paciente ativo de exemplo.
        Paciente paciente = paciente(true);
        // "Quando" buscarem o médico 1, devolva o médico de exemplo.
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico));
        // Quando buscarem o paciente 2, devolva o paciente de exemplo.
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente));
        // O horário está livre.
        when(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(eq(1L), eq(DATA), anyCollection())).thenReturn(false);
        // Ao salvar, devolve a própria consulta com id 10 (simulando o banco).
        when(consultaRepository.save(any(Consulta.class))).thenAnswer(invocacao -> {
            // Pega a consulta que foi passada ao save.
            Consulta c = invocacao.getArgument(0);
            // Define o id como o banco faria.
            c.setId(10L);
            // Devolve a consulta.
            return c;
        });

        // Executa o método testado.
        ConsultaResponseDTO resposta = consultaService.agendar(new ConsultaRequestDTO(DATA, "Dor de cabeça", 1L, 2L));

        // O id deve ser o "gerado pelo banco".
        assertThat(resposta.id()).isEqualTo(10L);
        // Toda consulta nova começa AGENDADA.
        assertThat(resposta.statusConsulta()).isEqualTo(StatusConsulta.AGENDADA);
        // O nome do médico deve vir do usuário dele.
        assertThat(resposta.nomeMedico()).isEqualTo("Dra. Ana");
    }

    // Marca como teste.
    @Test
    // Horário ocupado deve gerar ConflitoException e nada deve ser salvo.
    void agendarComHorarioOcupadoDeveLancarConflito() {
        // Médico encontrado.
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico(true)));
        // Paciente encontrado.
        when(pacienteRepository.findById(2L)).thenReturn(Optional.of(paciente(true)));
        // Horário OCUPADO.
        when(consultaRepository.existsByMedicoIdAndDataConsultaAndStatusConsultaIn(eq(1L), eq(DATA), anyCollection())).thenReturn(true);

        // Verifica que chamar o método lança a exceção esperada.
        assertThatThrownBy(() -> consultaService.agendar(new ConsultaRequestDTO(DATA, "Retorno", 1L, 2L)))
                // Do tipo ConflitoException.
                .isInstanceOf(ConflitoException.class);
        // Garante que o save NUNCA foi chamado.
        verify(consultaRepository, never()).save(any());
    }

    // Marca como teste.
    @Test
    // Médico inexistente deve gerar 404.
    void agendarComMedicoInexistenteDeveLancarNaoEncontrado() {
        // Qualquer busca de médico devolve vazio.
        when(medicoRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Verifica a exceção.
        assertThatThrownBy(() -> consultaService.agendar(new ConsultaRequestDTO(DATA, "Retorno", 99L, 2L)))
                // Tipo esperado.
                .isInstanceOf(RecursoNaoEncontradoException.class)
                // A mensagem deve citar o id.
                .hasMessageContaining("99");
    }

    // Marca como teste.
    @Test
    // Médico desativado não pode receber consultas.
    void agendarComMedicoDesativadoDeveLancarRegraNegocio() {
        // Médico encontrado, mas INATIVO.
        when(medicoRepository.findById(1L)).thenReturn(Optional.of(medico(false)));

        // Verifica a exceção.
        assertThatThrownBy(() -> consultaService.agendar(new ConsultaRequestDTO(DATA, "Retorno", 1L, 2L)))
                // Tipo esperado.
                .isInstanceOf(RegraNegocioException.class);
    }

    // Marca como teste.
    @Test
    // Uma consulta já realizada não pode ser cancelada.
    void cancelarConsultaRealizadaDeveLancarRegraNegocio() {
        // Cria uma consulta com status REALIZADA.
        Consulta consulta = consulta(StatusConsulta.REALIZADA);
        // Quando buscarem a consulta 5, devolve essa.
        when(consultaRepository.findById(5L)).thenReturn(Optional.of(consulta));

        // Verifica a exceção.
        assertThatThrownBy(() -> consultaService.cancelar(5L))
                // Tipo esperado.
                .isInstanceOf(RegraNegocioException.class);
        // O status não pode ter mudado.
        assertThat(consulta.getStatusConsulta()).isEqualTo(StatusConsulta.REALIZADA);
    }

    // Marca como teste.
    @Test
    // Confirmar uma consulta agendada muda o status para CONFIRMADA.
    void confirmarConsultaAgendada() {
        // Consulta AGENDADA.
        Consulta consulta = consulta(StatusConsulta.AGENDADA);
        // Retornada pelo mock.
        when(consultaRepository.findById(5L)).thenReturn(Optional.of(consulta));

        // Executa.
        ConsultaResponseDTO resposta = consultaService.confirmar(5L);

        // O status deve ter mudado.
        assertThat(resposta.statusConsulta()).isEqualTo(StatusConsulta.CONFIRMADA);
    }

    // Marca como teste.
    @Test
    // Período invertido (início depois do fim) deve ser recusado.
    void listarComPeriodoInvertidoDeveLancarRegraNegocio() {
        // Data inicial.
        LocalDate inicio = LocalDate.now().plusDays(10);
        // Data final ANTES da inicial.
        LocalDate fim = LocalDate.now();

        // Verifica a exceção.
        assertThatThrownBy(() -> consultaService.listar(null, null, null, inicio, fim, null))
                // Tipo esperado.
                .isInstanceOf(RegraNegocioException.class);
    }

    // ===== Métodos auxiliares que montam objetos de exemplo =====

    // Cria um médico com id 1, ativo ou não.
    private Medico medico(boolean ativo) {
        // Cria o usuário do médico.
        Usuario usuario = new Usuario();
        // Nome.
        usuario.setNome("Dra. Ana");
        // Ativo/inativo conforme o parâmetro.
        usuario.setAtivo(ativo);
        // Cria o médico.
        Medico medico = new Medico();
        // Id.
        medico.setId(1L);
        // Usuário.
        medico.setUsuario(usuario);
        // Especialidade.
        medico.setEspecialidade(new Especialidade(1L, "Clínica Geral"));
        // Devolve.
        return medico;
    }

    // Cria um paciente com id 2, ativo ou não.
    private Paciente paciente(boolean ativo) {
        // Cria o usuário do paciente.
        Usuario usuario = new Usuario();
        // Nome.
        usuario.setNome("Carlos");
        // Ativo/inativo.
        usuario.setAtivo(ativo);
        // Cria o paciente.
        Paciente paciente = new Paciente();
        // Id.
        paciente.setId(2L);
        // Usuário.
        paciente.setUsuario(usuario);
        // Devolve.
        return paciente;
    }

    // Cria uma consulta de exemplo com o status informado.
    private Consulta consulta(StatusConsulta status) {
        // Cria a consulta.
        Consulta consulta = new Consulta();
        // Id.
        consulta.setId(5L);
        // Médico.
        consulta.setMedico(medico(true));
        // Paciente.
        consulta.setPaciente(paciente(true));
        // Data.
        consulta.setDataConsulta(DATA);
        // Motivo.
        consulta.setMotivo("Rotina");
        // Status.
        consulta.setStatusConsulta(status);
        // Devolve.
        return consulta;
    }
}
