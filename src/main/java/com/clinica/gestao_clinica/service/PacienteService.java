package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.PacienteAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.dto.response.PacienteResponseDTO;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.mapper.PacienteMapper;
import com.clinica.gestao_clinica.repository.PacienteRepository;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteService(PacienteRepository pacienteRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public PacienteResponseDTO cadastrar(PacienteRequestDTO dto) {
        if (pacienteRepository.existsByCpf(dto.cpf())) {
            throw new ConflitoException("CPF já cadastrado");
        }
        if (pacienteRepository.existsByRg(dto.rg())) {
            throw new ConflitoException("RG já cadastrado");
        }
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new ConflitoException("E-mail já cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setTelefone(dto.telefone());
        usuario.setTipoUsuario(TipoUsuario.PACIENTE);
        usuario.setAtivo(true);
        usuario = usuarioRepository.save(usuario);

        Paciente paciente = pacienteRepository.save(PacienteMapper.toEntity(dto, usuario));

        log.info("Paciente {} cadastrado", paciente.getId());
        return PacienteMapper.toResponse(paciente);
    }

    @Transactional(readOnly = true)
    public Page<PacienteResponseDTO> listar(String nome, String cpf, Pageable pageable) {
        return pacienteRepository.filtrar(textoOuNulo(nome), textoOuNulo(cpf), pageable).map(PacienteMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public PacienteResponseDTO buscarPorId(Long id) {
        return PacienteMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public PacienteResponseDTO atualizar(Long id, PacienteAtualizacaoRequestDTO dados) {
        Paciente paciente = buscarEntidade(id);
        Usuario usuario = paciente.getUsuario();

        if (dados.nome() != null) {
            usuario.setNome(dados.nome());
        }
        if (dados.email() != null) {
            if (usuarioRepository.existsByEmailAndIdNot(dados.email(), usuario.getId())) {
                throw new ConflitoException("E-mail já cadastrado");
            }
            usuario.setEmail(dados.email());
        }
        if (dados.senha() != null) {
            usuario.setSenha(passwordEncoder.encode(dados.senha()));
        }
        if (dados.telefone() != null) {
            usuario.setTelefone(dados.telefone());
        }
        if (dados.endereco() != null) {
            paciente.setEndereco(dados.endereco());
        }
        if (dados.dataNascimento() != null) {
            paciente.setDataNascimento(dados.dataNascimento());
        }
        if (dados.sexo() != null) {
            paciente.setSexo(dados.sexo());
        }
        if (dados.tipoSanguineo() != null) {
            paciente.setTipoSanguineo(dados.tipoSanguineo());
        }
        if (dados.alergias() != null) {
            paciente.setAlergias(dados.alergias());
        }
        if (dados.observacao() != null) {
            paciente.setObservacao(dados.observacao());
        }

        log.info("Paciente {} atualizado", id);
        return PacienteMapper.toResponse(paciente);
    }

    @Transactional
    public void desativar(Long id) {
        buscarEntidade(id).getUsuario().setAtivo(false);
        log.info("Paciente {} desativado", id);
    }

    private Paciente buscarEntidade(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente", id));
    }

    private String textoOuNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
