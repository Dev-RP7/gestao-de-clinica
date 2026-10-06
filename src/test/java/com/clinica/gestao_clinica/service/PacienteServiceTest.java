// Pacote dos testes de service.
package com.clinica.gestao_clinica.service;

// DTOs, entidades e enums.
import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Exceção esperada.
import com.clinica.gestao_clinica.exception.ConflitoException;
// Repositórios (mocks).
import com.clinica.gestao_clinica.repository.PacienteRepository;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
// JUnit.
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
// Mockito.
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// Codificador de senha (também será mock).
import org.springframework.security.crypto.password.PasswordEncoder;

// Data.
import java.time.LocalDate;

// AssertJ.
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
// Mockito.
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// Liga o Mockito ao JUnit.
@ExtendWith(MockitoExtension.class)
// Testes unitários do PacienteService.
class PacienteServiceTest {

    // Mock do repositório de pacientes.
    @Mock
    // Repositório falso.
    private PacienteRepository pacienteRepository;
    // Mock do repositório de usuários.
    @Mock
    // Repositório falso.
    private UsuarioRepository usuarioRepository;
    // Mock do codificador de senhas.
    @Mock
    // Codificador falso.
    private PasswordEncoder passwordEncoder;

    // Cria o service real com os mocks.
    @InjectMocks
    // Classe testada.
    private PacienteService pacienteService;

    // Marca como teste.
    @Test
    // A senha deve ser salva criptografada, nunca em texto puro.
    void cadastrarDeveCriptografarSenha() {
        // Nenhum dado duplicado.
        when(pacienteRepository.existsByCpf("12345678901")).thenReturn(false);
        // RG livre.
        when(pacienteRepository.existsByRg("RG-1")).thenReturn(false);
        // E-mail livre.
        when(usuarioRepository.existsByEmail("maria@teste.com")).thenReturn(false);
        // O codificador transforma "segredo123" em "HASH".
        when(passwordEncoder.encode("segredo123")).thenReturn("HASH");
        // O save do usuário devolve o próprio usuário.
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        // O save do paciente devolve o próprio paciente.
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        // Executa o cadastro.
        pacienteService.cadastrar(dto());

        // ArgumentCaptor "captura" o objeto que foi passado para o save.
        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        // Verifica que o save foi chamado e captura o argumento.
        verify(usuarioRepository).save(captor.capture());
        // A senha salva deve ser o HASH, e não "segredo123".
        assertThat(captor.getValue().getSenha()).isEqualTo("HASH");
        // O perfil deve ser PACIENTE.
        assertThat(captor.getValue().getTipoUsuario()).isEqualTo(TipoUsuario.PACIENTE);
    }

    // Marca como teste.
    @Test
    // CPF repetido deve gerar conflito e nada deve ser salvo.
    void cadastrarComCpfDuplicadoDeveLancarConflito() {
        // O CPF já existe.
        when(pacienteRepository.existsByCpf("12345678901")).thenReturn(true);

        // Verifica a exceção.
        assertThatThrownBy(() -> pacienteService.cadastrar(dto()))
                // Tipo esperado.
                .isInstanceOf(ConflitoException.class)
                // Mensagem esperada.
                .hasMessage("CPF já cadastrado");
        // Nenhum usuário foi salvo.
        verify(usuarioRepository, never()).save(any());
    }

    // Monta um DTO de cadastro válido.
    private PacienteRequestDTO dto() {
        // Cria o record com todos os campos na ordem declarada.
        return new PacienteRequestDTO(
                // Nome.
                "Maria",
                // E-mail.
                "maria@teste.com",
                // Senha.
                "segredo123",
                // Telefone.
                "11988887777",
                // CPF.
                "12345678901",
                // RG.
                "RG-1",
                // Sexo.
                Sexo.FEMININO,
                // Tipo sanguíneo (opcional).
                null,
                // Alergias (opcional).
                null,
                // Observação (opcional).
                null,
                // Data de nascimento.
                LocalDate.of(1995, 3, 10),
                // Endereço.
                "Rua B, 2"
        );
    }
}
