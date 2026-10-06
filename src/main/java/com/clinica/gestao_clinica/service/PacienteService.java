// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTOs.
import com.clinica.gestao_clinica.dto.request.PacienteAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.dto.response.PacienteResponseDTO;
// Entidades e enum.
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Exceções.
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
// Mapper.
import com.clinica.gestao_clinica.mapper.PacienteMapper;
// Repositórios.
import com.clinica.gestao_clinica.repository.PacienteRepository;
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
// Regras de negócio dos pacientes.
public class PacienteService {

    // Repositório de pacientes.
    private final PacienteRepository pacienteRepository;
    // Repositório de usuários.
    private final UsuarioRepository usuarioRepository;
    // Codificador de senhas.
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependências.
    public PacienteService(PacienteRepository pacienteRepository,
                           UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder) {
        // Guarda o repositório de pacientes.
        this.pacienteRepository = pacienteRepository;
        // Guarda o repositório de usuários.
        this.usuarioRepository = usuarioRepository;
        // Guarda o codificador de senhas.
        this.passwordEncoder = passwordEncoder;
    }

    // Transação: usuário e paciente são salvos juntos.
    @Transactional
    // Cadastra um paciente.
    public PacienteResponseDTO cadastrar(PacienteRequestDTO dto) {
        // Regra: CPF único.
        if (pacienteRepository.existsByCpf(dto.cpf())) {
            // Conflito.
            throw new ConflitoException("CPF já cadastrado");
        }
        // Regra: RG único.
        if (pacienteRepository.existsByRg(dto.rg())) {
            // Conflito.
            throw new ConflitoException("RG já cadastrado");
        }
        // Regra: e-mail único.
        if (usuarioRepository.existsByEmail(dto.email())) {
            // Conflito.
            throw new ConflitoException("E-mail já cadastrado");
        }

        // Cria o usuário do paciente.
        Usuario usuario = new Usuario();
        // Nome.
        usuario.setNome(dto.nome());
        // E-mail.
        usuario.setEmail(dto.email());
        // Senha criptografada.
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        // Telefone.
        usuario.setTelefone(dto.telefone());
        // Perfil PACIENTE.
        usuario.setTipoUsuario(TipoUsuario.PACIENTE);
        // Ativo.
        usuario.setAtivo(true);
        // Salva o usuário.
        usuario = usuarioRepository.save(usuario);

        // Cria e salva o paciente ligado ao usuário.
        Paciente paciente = pacienteRepository.save(PacienteMapper.toEntity(dto, usuario));

        // Registra no log (sem dados sensíveis como CPF).
        log.info("Paciente {} cadastrado", paciente.getId());
        // Devolve o DTO.
        return PacienteMapper.toResponse(paciente);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Lista pacientes ativos com filtros opcionais.
    public Page<PacienteResponseDTO> listar(String nome, String cpf, Pageable pageable) {
        // Busca a página e converte cada item.
        return pacienteRepository.filtrar(textoOuNulo(nome), textoOuNulo(cpf), pageable).map(PacienteMapper::toResponse);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Busca um paciente pelo id.
    public PacienteResponseDTO buscarPorId(Long id) {
        // Busca e converte (antes, a mensagem de erro dizia "Médico não encontrado").
        return PacienteMapper.toResponse(buscarEntidade(id));
    }

    // Transação de escrita.
    @Transactional
    // Atualiza um paciente. Só os campos enviados são alterados.
    public PacienteResponseDTO atualizar(Long id, PacienteAtualizacaoRequestDTO dados) {
        // Busca o paciente.
        Paciente paciente = buscarEntidade(id);
        // Pega o usuário dele.
        Usuario usuario = paciente.getUsuario();

        // Se veio nome, atualiza.
        if (dados.nome() != null) {
            // Novo nome.
            usuario.setNome(dados.nome());
        }
        // Se veio e-mail...
        if (dados.email() != null) {
            // ...verifica duplicidade com outros usuários.
            if (usuarioRepository.existsByEmailAndIdNot(dados.email(), usuario.getId())) {
                // Conflito.
                throw new ConflitoException("E-mail já cadastrado");
            }
            // Novo e-mail.
            usuario.setEmail(dados.email());
        }
        // Se veio senha...
        if (dados.senha() != null) {
            // ...criptografa antes de salvar.
            usuario.setSenha(passwordEncoder.encode(dados.senha()));
        }
        // Se veio telefone, atualiza.
        if (dados.telefone() != null) {
            // Novo telefone.
            usuario.setTelefone(dados.telefone());
        }
        // Se veio endereço, atualiza.
        if (dados.endereco() != null) {
            // Novo endereço.
            paciente.setEndereco(dados.endereco());
        }
        // Se veio data de nascimento, atualiza.
        if (dados.dataNascimento() != null) {
            // Nova data.
            paciente.setDataNascimento(dados.dataNascimento());
        }
        // Se veio sexo, atualiza.
        if (dados.sexo() != null) {
            // Novo sexo.
            paciente.setSexo(dados.sexo());
        }
        // Se veio tipo sanguíneo, atualiza.
        if (dados.tipoSanguineo() != null) {
            // Novo tipo.
            paciente.setTipoSanguineo(dados.tipoSanguineo());
        }
        // Se vieram alergias, atualiza.
        if (dados.alergias() != null) {
            // Novas alergias.
            paciente.setAlergias(dados.alergias());
        }
        // Se veio observação, atualiza.
        if (dados.observacao() != null) {
            // Nova observação.
            paciente.setObservacao(dados.observacao());
        }

        // Registra no log.
        log.info("Paciente {} atualizado", id);
        // Devolve o DTO.
        return PacienteMapper.toResponse(paciente);
    }

    // Transação de escrita.
    @Transactional
    // Desativa o paciente (exclusão lógica).
    public void desativar(Long id) {
        // Busca o paciente e marca o usuário como inativo.
        buscarEntidade(id).getUsuario().setAtivo(false);
        // Registra no log.
        log.info("Paciente {} desativado", id);
    }

    // Busca o paciente ou lança 404.
    private Paciente buscarEntidade(Long id) {
        // Busca pelo id.
        return pacienteRepository.findById(id)
                // Se não existir, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente", id));
    }

    // Converte texto vazio em null, para o filtro ser ignorado.
    private String textoOuNulo(String texto) {
        // Nulo ou só espaços vira null; senão, tira espaços das pontas.
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
