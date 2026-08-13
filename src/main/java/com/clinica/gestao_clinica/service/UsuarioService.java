package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.UsuarioAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.UsuarioCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.UsuarioResponseDTO;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.mapper.UsuarioMapper;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public UsuarioResponseDTO cadastrar(UsuarioCadastroRequestDTO dados) {

        Usuario usuario = new Usuario();

        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setSenha(dados.senha());
        usuario.setTelefone(dados.telefone());

        usuario.setTipoUsuario(TipoUsuario.PACIENTE);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return UsuarioMapper.toResponse(usuarioSalvo);
    }

    public List<UsuarioResponseDTO> listarUsuarios() {

        return usuarioRepository.findByAtivoTrue()
                .stream()
                .map(UsuarioMapper::toResponse)
                .toList();
    }

    public UsuarioResponseDTO buscarPorId(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        return UsuarioMapper.toResponse(usuario);
    }

    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoRequestDTO dados) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setNome(dados.nome());
        usuario.setEmail(dados.email());
        usuario.setTelefone(dados.telefone());

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return UsuarioMapper.toResponse(usuarioAtualizado);
    }

    public void desativar(Long id) {

        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        usuario.setAtivo(false);

        usuarioRepository.save(usuario);
    }
}
