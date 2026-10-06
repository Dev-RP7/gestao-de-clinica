// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTOs.
import com.clinica.gestao_clinica.dto.request.MedicoAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.MedicoCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
// Entidades e enum.
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Exceções.
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
// Mapper.
import com.clinica.gestao_clinica.mapper.MedicoMapper;
// Repositórios.
import com.clinica.gestao_clinica.repository.EspecialidadeRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
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
// Regras de negócio dos médicos.
public class MedicoService {

    // Repositório de médicos.
    private final MedicoRepository medicoRepository;
    // Repositório de usuários.
    private final UsuarioRepository usuarioRepository;
    // Repositório de especialidades.
    private final EspecialidadeRepository especialidadeRepository;
    // Codificador de senhas (BCrypt).
    private final PasswordEncoder passwordEncoder;

    // Injeção de dependências.
    public MedicoService(MedicoRepository medicoRepository,
                         UsuarioRepository usuarioRepository,
                         EspecialidadeRepository especialidadeRepository,
                         PasswordEncoder passwordEncoder) {
        // Guarda o repositório de médicos.
        this.medicoRepository = medicoRepository;
        // Guarda o repositório de usuários.
        this.usuarioRepository = usuarioRepository;
        // Guarda o repositório de especialidades.
        this.especialidadeRepository = especialidadeRepository;
        // Guarda o codificador de senhas.
        this.passwordEncoder = passwordEncoder;
    }

    // Transação: usuário e médico são salvos juntos (se um falhar, nenhum é salvo).
    @Transactional
    // Cadastra um médico.
    public MedicoResponseDTO cadastrar(MedicoCadastroRequestDTO dto) {
        // Regra: e-mail único.
        if (usuarioRepository.existsByEmail(dto.email())) {
            // Conflito.
            throw new ConflitoException("E-mail já cadastrado");
        }
        // Regra: CRM único.
        if (medicoRepository.existsByCrm(dto.crm())) {
            // Conflito.
            throw new ConflitoException("CRM já cadastrado");
        }
        // Busca a especialidade.
        Especialidade especialidade = buscarEspecialidade(dto.especialidadeId());

        // Cria o usuário do médico.
        Usuario usuario = new Usuario();
        // Nome.
        usuario.setNome(dto.nome());
        // E-mail.
        usuario.setEmail(dto.email());
        // Senha criptografada (antes era salva em texto puro).
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        // Telefone.
        usuario.setTelefone(dto.telefone());
        // Perfil MEDICO.
        usuario.setTipoUsuario(TipoUsuario.MEDICO);
        // Ativo.
        usuario.setAtivo(true);
        // Salva o usuário.
        usuario = usuarioRepository.save(usuario);

        // Cria o médico.
        Medico medico = new Medico();
        // Liga ao usuário.
        medico.setUsuario(usuario);
        // CRM.
        medico.setCrm(dto.crm());
        // Especialidade.
        medico.setEspecialidade(especialidade);
        // Salva o médico.
        medico = medicoRepository.save(medico);

        // Registra no log.
        log.info("Médico {} cadastrado (CRM {})", medico.getId(), medico.getCrm());
        // Devolve o DTO.
        return MedicoMapper.toResponse(medico);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Lista médicos ativos com filtros opcionais.
    public Page<MedicoResponseDTO> listar(String nome, Long especialidadeId, Pageable pageable) {
        // Busca a página e converte cada item.
        return medicoRepository.filtrar(textoOuNulo(nome), especialidadeId, pageable).map(MedicoMapper::toResponse);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Busca um médico pelo id.
    public MedicoResponseDTO buscarPorId(Long id) {
        // Busca e converte.
        return MedicoMapper.toResponse(buscarEntidade(id));
    }

    // Transação de escrita.
    @Transactional
    // Atualiza um médico. Só os campos enviados são alterados.
    public MedicoResponseDTO atualizar(Long id, MedicoAtualizacaoRequestDTO dto) {
        // Busca o médico.
        Medico medico = buscarEntidade(id);
        // Pega o usuário dele.
        Usuario usuario = medico.getUsuario();

        // Se veio nome...
        if (dto.nome() != null) {
            // ...atualiza.
            usuario.setNome(dto.nome());
        }
        // Se veio e-mail...
        if (dto.email() != null) {
            // ...verifica se outro usuário já usa este e-mail.
            if (usuarioRepository.existsByEmailAndIdNot(dto.email(), usuario.getId())) {
                // Conflito.
                throw new ConflitoException("E-mail já cadastrado");
            }
            // Atualiza.
            usuario.setEmail(dto.email());
        }
        // Se veio telefone...
        if (dto.telefone() != null) {
            // ...atualiza.
            usuario.setTelefone(dto.telefone());
        }
        // Se veio CRM...
        if (dto.crm() != null) {
            // ...verifica se outro médico já usa este CRM.
            if (medicoRepository.existsByCrmAndIdNot(dto.crm(), id)) {
                // Conflito.
                throw new ConflitoException("CRM já cadastrado");
            }
            // Atualiza.
            medico.setCrm(dto.crm());
        }
        // Se veio especialidade (antes, não enviar dava erro)...
        if (dto.especialidadeId() != null) {
            // ...busca e atualiza.
            medico.setEspecialidade(buscarEspecialidade(dto.especialidadeId()));
        }

        // Registra no log (o Hibernate grava as mudanças no fim da transação).
        log.info("Médico {} atualizado", id);
        // Devolve o DTO.
        return MedicoMapper.toResponse(medico);
    }

    // Transação de escrita.
    @Transactional
    // Desativa o médico (exclusão lógica: o histórico de consultas é mantido).
    public void desativar(Long id) {
        // Busca o médico.
        Medico medico = buscarEntidade(id);
        // Marca o usuário como inativo (ele não consegue mais fazer login).
        medico.getUsuario().setAtivo(false);
        // Registra no log.
        log.info("Médico {} desativado", id);
    }

    // Busca o médico ou lança 404.
    private Medico buscarEntidade(Long id) {
        // Busca pelo id.
        return medicoRepository.findById(id)
                // Se não existir, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico", id));
    }

    // Busca a especialidade ou lança 404.
    private Especialidade buscarEspecialidade(Long id) {
        // Busca pelo id.
        return especialidadeRepository.findById(id)
                // Se não existir, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade", id));
    }

    // Converte texto vazio ("" ou "   ") em null, para o filtro ser ignorado na consulta.
    private String textoOuNulo(String texto) {
        // Se for nulo ou só espaços, devolve null; senão, devolve sem espaços nas pontas.
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
