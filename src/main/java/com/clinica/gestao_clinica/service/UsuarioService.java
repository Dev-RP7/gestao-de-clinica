package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.UsuarioAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.UsuarioCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.UsuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.mapper.UsuarioMapper;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioCadastroRequestDTO dados) {
        if (usuarioRepository.existsByEmail(dados.email())) {
            throw new ConflitoException("E-mail já cadastrado");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        usuario.setTelefone(dados.telefone());
        usuario.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        usuario.setAtivo(true);
        usuario = usuarioRepository.save(usuario);
        log.info("Administrador {} cadastrado", usuario.getId());
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponseDTO> listar(String nome, TipoUsuario tipo, Pageable pageable) {
        String filtroNome = (nome == null || nome.isBlank()) ? null : nome.trim();
        return usuarioRepository.filtrar(filtroNome, tipo, pageable).map(UsuarioMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return UsuarioMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoRequestDTO dados) {
        Usuario usuario = buscarEntidade(id);
        if (dados.nome() != null) {
            usuario.setNome(dados.nome());
        }
        if (dados.email() != null) {
            if (usuarioRepository.existsByEmailAndIdNot(dados.email(), id)) {
                throw new ConflitoException("E-mail já cadastrado");
            }
            usuario.setEmail(dados.email());
        }
        if (dados.telefone() != null) {
            usuario.setTelefone(dados.telefone());
        }
        log.info("Usuário {} atualizado", id);
        return UsuarioMapper.toResponse(usuario);
    }

    @Transactional
    public void desativar(Long id) {
        buscarEntidade(id).setAtivo(false);
        log.info("Usuário {} desativado", id);
    }

    private Usuario buscarEntidade(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }
}
