package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.repository.PacienteRepository;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PacienteServiceTest {

    @Mock
    private PacienteRepository pacienteRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PacienteService pacienteService;

    @Test
    void cadastrarDeveCriptografarSenha() {
        when(pacienteRepository.existsByCpf("12345678901")).thenReturn(false);
        when(pacienteRepository.existsByRg("RG-1")).thenReturn(false);
        when(usuarioRepository.existsByEmail("maria@teste.com")).thenReturn(false);
        when(passwordEncoder.encode("segredo123")).thenReturn("HASH");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        when(pacienteRepository.save(any(Paciente.class))).thenAnswer(inv -> inv.getArgument(0));

        pacienteService.cadastrar(dto());

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        assertThat(captor.getValue().getSenha()).isEqualTo("HASH");
        assertThat(captor.getValue().getTipoUsuario()).isEqualTo(TipoUsuario.PACIENTE);
    }

    @Test
    void cadastrarComCpfDuplicadoDeveLancarConflito() {
        when(pacienteRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> pacienteService.cadastrar(dto()))
                .isInstanceOf(ConflitoException.class)
                .hasMessage("CPF já cadastrado");
        verify(usuarioRepository, never()).save(any());
    }

    private PacienteRequestDTO dto() {
        return new PacienteRequestDTO(
                "Maria",
                "maria@teste.com",
                "segredo123",
                "11988887777",
                "12345678901",
                "RG-1",
                Sexo.FEMININO,
                null,
                null,
                null,
                LocalDate.of(1995, 3, 10),
                "Rua B, 2"
        );
    }
}
