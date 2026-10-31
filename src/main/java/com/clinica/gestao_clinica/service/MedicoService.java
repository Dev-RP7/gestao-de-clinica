package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.MedicoAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.MedicoCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.exception.ConflitoException;
import com.clinica.gestao_clinica.exception.RecursoNaoEncontradoException;
import com.clinica.gestao_clinica.mapper.MedicoMapper;
import com.clinica.gestao_clinica.repository.EspecialidadeRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadeRepository especialidadeRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoService(MedicoRepository medicoRepository,
                         UsuarioRepository usuarioRepository,
                         EspecialidadeRepository especialidadeRepository,
                         PasswordEncoder passwordEncoder) {
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.especialidadeRepository = especialidadeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public MedicoResponseDTO cadastrar(MedicoCadastroRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new ConflitoException("E-mail já cadastrado");
        }
        if (medicoRepository.existsByCrm(dto.crm())) {
            throw new ConflitoException("CRM já cadastrado");
        }
        Especialidade especialidade = buscarEspecialidade(dto.especialidadeId());

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setTelefone(dto.telefone());
        usuario.setTipoUsuario(TipoUsuario.MEDICO);
        usuario.setAtivo(true);
        usuario = usuarioRepository.save(usuario);

        Medico medico = new Medico();
        medico.setUsuario(usuario);
        medico.setCrm(dto.crm());
        medico.setEspecialidade(especialidade);
        medico = medicoRepository.save(medico);

        log.info("Médico {} cadastrado (CRM {})", medico.getId(), medico.getCrm());
        return MedicoMapper.toResponse(medico);
    }

    @Transactional(readOnly = true)
    public Page<MedicoResponseDTO> listar(String nome, Long especialidadeId, Pageable pageable) {
        return medicoRepository.filtrar(textoOuNulo(nome), especialidadeId, pageable).map(MedicoMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public MedicoResponseDTO buscarPorId(Long id) {
        return MedicoMapper.toResponse(buscarEntidade(id));
    }

    @Transactional
    public MedicoResponseDTO atualizar(Long id, MedicoAtualizacaoRequestDTO dto) {
        Medico medico = buscarEntidade(id);
        Usuario usuario = medico.getUsuario();

        if (dto.nome() != null) {
            usuario.setNome(dto.nome());
        }
        if (dto.email() != null) {
            if (usuarioRepository.existsByEmailAndIdNot(dto.email(), usuario.getId())) {
                throw new ConflitoException("E-mail já cadastrado");
            }
            usuario.setEmail(dto.email());
        }
        if (dto.telefone() != null) {
            usuario.setTelefone(dto.telefone());
        }
        if (dto.crm() != null) {
            if (medicoRepository.existsByCrmAndIdNot(dto.crm(), id)) {
                throw new ConflitoException("CRM já cadastrado");
            }
            medico.setCrm(dto.crm());
        }
        if (dto.especialidadeId() != null) {
            medico.setEspecialidade(buscarEspecialidade(dto.especialidadeId()));
        }

        log.info("Médico {} atualizado", id);
        return MedicoMapper.toResponse(medico);
    }

    @Transactional
    public void desativar(Long id) {
        Medico medico = buscarEntidade(id);
        medico.getUsuario().setAtivo(false);
        log.info("Médico {} desativado", id);
    }

    private Medico buscarEntidade(Long id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico", id));
    }

    private Especialidade buscarEspecialidade(Long id) {
        return especialidadeRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade", id));
    }

    private String textoOuNulo(String texto) {
        return (texto == null || texto.isBlank()) ? null : texto.trim();
    }
}
