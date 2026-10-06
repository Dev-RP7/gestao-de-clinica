// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTOs.
import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
// Entidades.
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.entity.Receita;
// Exceção.
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
// Mapper.
import com.clinica.gestao_clinica.mapper.ReceitaMapper;
// Repositórios.
import com.clinica.gestao_clinica.repository.ProntuarioRepository;
import com.clinica.gestao_clinica.repository.ReceitaRepository;
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
// Regras de negócio das receitas.
public class ReceitaService {

    // Repositório de receitas.
    private final ReceitaRepository receitaRepository;
    // Repositório de prontuários.
    private final ProntuarioRepository prontuarioRepository;

    // Injeção de dependências.
    public ReceitaService(ReceitaRepository receitaRepository, ProntuarioRepository prontuarioRepository) {
        // Guarda o repositório de receitas.
        this.receitaRepository = receitaRepository;
        // Guarda o repositório de prontuários.
        this.prontuarioRepository = prontuarioRepository;
    }

    // Transação de escrita.
    @Transactional
    // Cria uma receita ligada a um prontuário.
    public ReceitaResponseDTO criar(ReceitaRequestDTO dto) {
        // Busca o prontuário ou lança 404.
        Prontuario prontuario = prontuarioRepository.findById(dto.prontuarioId())
                // Exceção se não existir.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prontuário", dto.prontuarioId()));
        // Converte (já ligando ao prontuário) e salva.
        Receita receita = receitaRepository.save(ReceitaMapper.toEntity(dto, prontuario));
        // Registra no log.
        log.info("Receita {} criada no prontuário {}", receita.getId(), prontuario.getId());
        // Devolve o DTO.
        return ReceitaMapper.toResponse(receita);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Lista receitas; se vier prontuarioId, só as daquele prontuário.
    public Page<ReceitaResponseDTO> listar(Long prontuarioId, Pageable pageable) {
        // Escolhe a consulta conforme o filtro.
        Page<Receita> pagina = prontuarioId == null
                // Sem filtro: todas.
                ? receitaRepository.findAll(pageable)
                // Com filtro: só do prontuário.
                : receitaRepository.findByProntuarioId(prontuarioId, pageable);
        // Converte cada item.
        return pagina.map(ReceitaMapper::toResponse);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Busca uma receita pelo id.
    public ReceitaResponseDTO buscarPorId(Long id) {
        // Busca e converte.
        return ReceitaMapper.toResponse(buscarEntidade(id));
    }

    // Transação de escrita.
    @Transactional
    // Altera os dados da receita (o prontuário não muda).
    public ReceitaResponseDTO atualizar(Long id, ReceitaRequestDTO dto) {
        // Busca a receita.
        Receita receita = buscarEntidade(id);
        // Medicamento.
        receita.setMedicamento(dto.medicamento());
        // Dosagem.
        receita.setDosagem(dto.dosagem());
        // Frequência.
        receita.setFrequencia(dto.frequencia());
        // Duração.
        receita.setDuracao(dto.duracao());
        // Observação.
        receita.setObservacao(dto.observacao());
        // Registra no log.
        log.info("Receita {} atualizada", id);
        // Devolve o DTO.
        return ReceitaMapper.toResponse(receita);
    }

    // Transação de escrita.
    @Transactional
    // Exclui uma receita.
    public void excluir(Long id) {
        // Busca (404 se não existir) e exclui.
        receitaRepository.delete(buscarEntidade(id));
        // Registra no log.
        log.info("Receita {} excluída", id);
    }

    // Busca a receita ou lança 404.
    private Receita buscarEntidade(Long id) {
        // Busca pelo id.
        return receitaRepository.findById(id)
                // Se não existir, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Receita", id));
    }
}
