// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTOs e entidade.
import com.clinica.gestao_clinica.dto.request.EspecialidadeCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.EspecialidadeResponseDTO;
import com.clinica.gestao_clinica.entity.Especialidade;
// Exceções.
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
// Mapper.
import com.clinica.gestao_clinica.mapper.EspecialidadeMapper;
// Repositórios.
import com.clinica.gestao_clinica.repository.EspecialidadeRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Service e transação.
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Cria o "log".
@Slf4j
// O Spring gerencia esta classe.
@Service
// Regras de negócio das especialidades.
public class EspecialidadeService {

    // Repositório de especialidades.
    private final EspecialidadeRepository especialidadeRepository;
    // Repositório de médicos (para saber se a especialidade está em uso).
    private final MedicoRepository medicoRepository;

    // Injeção de dependências.
    public EspecialidadeService(EspecialidadeRepository especialidadeRepository, MedicoRepository medicoRepository) {
        // Guarda o repositório de especialidades.
        this.especialidadeRepository = especialidadeRepository;
        // Guarda o repositório de médicos.
        this.medicoRepository = medicoRepository;
    }

    // Transação de escrita.
    @Transactional
    // Cadastra uma especialidade.
    public EspecialidadeResponseDTO cadastrar(EspecialidadeCadastroRequestDTO dto) {
        // Regra: não pode haver nomes repetidos.
        if (especialidadeRepository.existsByNomeIgnoreCase(dto.nome().trim())) {
            // Conflito (409).
            throw new ConflitoException("Especialidade '" + dto.nome() + "' já cadastrada");
        }
        // Converte o DTO e salva.
        Especialidade especialidade = especialidadeRepository.save(EspecialidadeMapper.toEntity(dto));
        // Registra no log.
        log.info("Especialidade {} cadastrada: {}", especialidade.getId(), especialidade.getNome());
        // Devolve o DTO.
        return EspecialidadeMapper.toResponse(especialidade);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Lista especialidades, com filtro opcional por parte do nome.
    public Page<EspecialidadeResponseDTO> listar(String nome, Pageable pageable) {
        // Se não veio filtro, busca todas; senão, busca as que contêm o texto.
        Page<Especialidade> pagina = (nome == null || nome.isBlank())
                // Sem filtro.
                ? especialidadeRepository.findAll(pageable)
                // Com filtro.
                : especialidadeRepository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        // Converte cada item em DTO.
        return pagina.map(EspecialidadeMapper::toResponse);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Busca uma especialidade pelo id.
    public EspecialidadeResponseDTO buscarPorId(Long id) {
        // Busca e converte.
        return EspecialidadeMapper.toResponse(buscarEntidade(id));
    }

    // Transação de escrita.
    @Transactional
    // Renomeia uma especialidade.
    public EspecialidadeResponseDTO atualizar(Long id, EspecialidadeCadastroRequestDTO dto) {
        // Busca a especialidade.
        Especialidade especialidade = buscarEntidade(id);
        // Remove espaços extras do novo nome.
        String novoNome = dto.nome().trim();
        // Se o nome mudou e o novo nome já existe em outra especialidade...
        if (!especialidade.getNome().equalsIgnoreCase(novoNome)
                // ...verificando no banco...
                && especialidadeRepository.existsByNomeIgnoreCase(novoNome)) {
            // ...é conflito.
            throw new ConflitoException("Especialidade '" + novoNome + "' já cadastrada");
        }
        // Altera o nome (o Hibernate salva no final da transação).
        especialidade.setNome(novoNome);
        // Registra no log.
        log.info("Especialidade {} renomeada para {}", id, novoNome);
        // Devolve o DTO.
        return EspecialidadeMapper.toResponse(especialidade);
    }

    // Transação de escrita.
    @Transactional
    // Exclui uma especialidade.
    public void excluir(Long id) {
        // Busca a especialidade (lança 404 se não existir).
        Especialidade especialidade = buscarEntidade(id);
        // Regra: não podemos excluir uma especialidade usada por algum médico.
        if (medicoRepository.existsByEspecialidadeId(id)) {
            // Conflito (409).
            throw new ConflitoException("Especialidade em uso por médicos; não pode ser excluída");
        }
        // Exclui do banco.
        especialidadeRepository.delete(especialidade);
        // Registra no log.
        log.info("Especialidade {} excluída", id);
    }

    // Busca a entidade ou lança 404.
    private Especialidade buscarEntidade(Long id) {
        // Busca pelo id.
        return especialidadeRepository.findById(id)
                // Se não existir, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade", id));
    }
}
