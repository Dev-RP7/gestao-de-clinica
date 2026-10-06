// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTOs.
import com.clinica.gestao_clinica.dto.request.UsuarioAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.UsuarioCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.UsuarioResponseDTO;
// Entidade e enum.
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Exceções.
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
// Mapper.
import com.clinica.gestao_clinica.mapper.UsuarioMapper;
// Repositório.
import com.clinica.gestao_clinica.repository.UsuarioRepository;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Criptografia de senha.
import org.springframework.security.crypto.password.PasswordEncoder;
// Service e transação.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Cria o "log".
@Slf4j
// O Spring gerencia esta classe.
@Service
// Regras de negócio dos usuários (rotas acessíveis só por administradores).
public class UsuarioService {

    // Repositório de usuários.
    private final UsuarioRepository usuarioRepository;
    // Codificador de senhas.
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependências.
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        // Guarda o repositório.
        this.usuarioRepository = usuarioRepository;
        // Guarda o codificador.
        this.passwordEncoder = passwordEncoder;
    }

    // Transação de escrita.
    @Transactional
    // Cadastra um novo ADMINISTRADOR.
    // (Antes, este método criava um usuário PACIENTE sem registro de paciente, o que deixava dados incompletos.)
    public UsuarioResponseDTO cadastrar(UsuarioCadastroRequestDTO dados) {
        // Regra: e-mail único.
        if (usuarioRepository.existsByEmail(dados.email())) {
            // Conflito.
            throw new ConflitoException("E-mail já cadastrado");
        }
        // Cria o usuário.
        Usuario usuario = new Usuario();
        // Nome.
        usuario.setNome(dados.nome());
        // E-mail.
        usuario.setEmail(dados.email());
        // Senha criptografada.
        usuario.setSenha(passwordEncoder.encode(dados.senha()));
        // Telefone.
        usuario.setTelefone(dados.telefone());
        // Perfil ADMINISTRADOR.
        usuario.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
        // Ativo.
        usuario.setAtivo(true);
        // Salva.
        usuario = usuarioRepository.save(usuario);
        // Registra no log.
        log.info("Administrador {} cadastrado", usuario.getId());
        // Devolve o DTO.
        return UsuarioMapper.toResponse(usuario);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Lista usuários ativos com filtros opcionais por nome e perfil.
    public Page<UsuarioResponseDTO> listar(String nome, TipoUsuario tipo, Pageable pageable) {
        // Trata texto vazio como "sem filtro".
        String filtroNome = (nome == null || nome.isBlank()) ? null : nome.trim();
        // Busca a página e converte cada item.
        return usuarioRepository.filtrar(filtroNome, tipo, pageable).map(UsuarioMapper::toResponse);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Busca um usuário pelo id.
    public UsuarioResponseDTO buscarPorId(Long id) {
        // Busca e converte.
        return UsuarioMapper.toResponse(buscarEntidade(id));
    }

    // Transação de escrita.
    @Transactional
    // Atualiza nome, e-mail e/ou telefone. Só os campos enviados são alterados.
    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizacaoRequestDTO dados) {
        // Busca o usuário.
        Usuario usuario = buscarEntidade(id);
        // Se veio nome, atualiza.
        if (dados.nome() != null) {
            // Novo nome.
            usuario.setNome(dados.nome());
        }
        // Se veio e-mail...
        if (dados.email() != null) {
            // ...verifica duplicidade.
            if (usuarioRepository.existsByEmailAndIdNot(dados.email(), id)) {
                // Conflito.
                throw new ConflitoException("E-mail já cadastrado");
            }
            // Novo e-mail.
            usuario.setEmail(dados.email());
        }
        // Se veio telefone, atualiza.
        if (dados.telefone() != null) {
            // Novo telefone.
            usuario.setTelefone(dados.telefone());
        }
        // Registra no log.
        log.info("Usuário {} atualizado", id);
        // Devolve o DTO.
        return UsuarioMapper.toResponse(usuario);
    }

    // Transação de escrita.
    @Transactional
    // Desativa o usuário (exclusão lógica).
    public void desativar(Long id) {
        // Busca e marca como inativo.
        buscarEntidade(id).setAtivo(false);
        // Registra no log.
        log.info("Usuário {} desativado", id);
    }

    // Busca o usuário ou lança 404.
    private Usuario buscarEntidade(Long id) {
        // Busca pelo id.
        return usuarioRepository.findById(id)
                // Se não existir, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário", id));
    }
}
