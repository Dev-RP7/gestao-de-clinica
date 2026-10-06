// Pacote dos services.
package com.clinica.gestao_clinica.service;

// DTOs.
import com.clinica.gestao_clinica.dto.request.ProntuarioAtualizadoRequestDTO;
import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
// Entidades e enum.
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.entity.Prontuario;
import com.clinica.gestao_clinica.enums.StatusConsulta;
// Exceções.
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.exception.RegraNegocioException;
// Mapper.
import com.clinica.gestao_clinica.mapper.ProntuarioMapper;
// Repositórios.
import com.clinica.gestao_clinica.repository.ConsultaRepository;
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
// Regras de negócio dos prontuários.
public class ProntuarioService {

    // Repositório de prontuários.
    private final ProntuarioRepository prontuarioRepository;
    // Repositório de consultas.
    private final ConsultaRepository consultaRepository;
    // Repositório de receitas.
    private final ReceitaRepository receitaRepository;

    // Injeção de dependências.
    public ProntuarioService(ProntuarioRepository prontuarioRepository,
                             ConsultaRepository consultaRepository,
                             ReceitaRepository receitaRepository) {
        // Guarda o repositório de prontuários.
        this.prontuarioRepository = prontuarioRepository;
        // Guarda o repositório de consultas.
        this.consultaRepository = consultaRepository;
        // Guarda o repositório de receitas.
        this.receitaRepository = receitaRepository;
    }

    // Transação de escrita.
    @Transactional
    // Cria o prontuário de uma consulta.
    public ProntuarioResponseDTO criar(ProntuarioRequestDTO dto) {
        // Busca a consulta ou lança 404.
        Consulta consulta = consultaRepository.findById(dto.consultaId())
                // Exceção se não existir.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", dto.consultaId()));
        // Regra: consulta cancelada não tem atendimento, logo não tem prontuário.
        if (consulta.getStatusConsulta() == StatusConsulta.CANCELADA) {
            // Regra violada.
            throw new RegraNegocioException("Não é possível criar prontuário para uma consulta cancelada");
        }
        // Regra: um prontuário por consulta.
        if (prontuarioRepository.existsByConsultaId(consulta.getId())) {
            // Conflito.
            throw new ConflitoException("Esta consulta já possui prontuário");
        }
        // Converte e salva.
        Prontuario prontuario = prontuarioRepository.save(ProntuarioMapper.toEntity(dto, consulta));
        // Registra no log.
        log.info("Prontuário {} criado para a consulta {}", prontuario.getId(), consulta.getId());
        // Devolve o DTO.
        return ProntuarioMapper.toResponse(prontuario);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Lista prontuários com filtros opcionais por paciente e médico.
    public Page<ProntuarioResponseDTO> listar(Long pacienteId, Long medicoId, Pageable pageable) {
        // Busca a página e converte cada item.
        return prontuarioRepository.filtrar(pacienteId, medicoId, pageable).map(ProntuarioMapper::toResponse);
    }

    // Transação de leitura.
    @Transactional(readOnly = true)
    // Busca um prontuário pelo id.
    public ProntuarioResponseDTO buscarPorId(Long id) {
        // Busca e converte.
        return ProntuarioMapper.toResponse(buscarEntidade(id));
    }

    // Transação de escrita.
    @Transactional
    // Atualiza os dados clínicos do prontuário.
    public ProntuarioResponseDTO atualizar(Long id, ProntuarioAtualizadoRequestDTO dto) {
        // Busca o prontuário.
        Prontuario prontuario = buscarEntidade(id);
        // Peso.
        prontuario.setPeso(dto.peso());
        // Altura.
        prontuario.setAltura(dto.altura());
        // Pressão.
        prontuario.setPressao(dto.pressao());
        // Temperatura.
        prontuario.setTemperatura(dto.temperatura());
        // Diagnóstico.
        prontuario.setDiagnostico(dto.diagnostico());
        // Tratamento.
        prontuario.setTratamento(dto.tratamento());
        // Observação.
        prontuario.setObservacao(dto.observacao());
        // Registra no log.
        log.info("Prontuário {} atualizado", id);
        // Devolve o DTO.
        return ProntuarioMapper.toResponse(prontuario);
    }

    // Transação de escrita.
    @Transactional
    // Exclui um prontuário.
    public void excluir(Long id) {
        // Busca o prontuário (404 se não existir; antes a mensagem dizia "Paciente não encontrado").
        Prontuario prontuario = buscarEntidade(id);
        // Regra: não podemos excluir um prontuário que tem receitas.
        if (receitaRepository.existsByProntuarioId(id)) {
            // Conflito.
            throw new ConflitoException("O prontuário possui receitas; exclua-as antes");
        }
        // Exclui.
        prontuarioRepository.delete(prontuario);
        // Registra no log.
        log.info("Prontuário {} excluído", id);
    }

    // Busca o prontuário ou lança 404.
    private Prontuario buscarEntidade(Long id) {
        // Busca pelo id.
        return prontuarioRepository.findById(id)
                // Se não existir, lança a exceção.
                .orElseThrow(() -> new RecursoNaoEncontradoException("Prontuário", id));
    }
}
