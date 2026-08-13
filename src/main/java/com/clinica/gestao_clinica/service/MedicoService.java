package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.MedicoCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.entity.Especialidade;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.repository.EspecialidadeRepository;
import com.clinica.gestao_clinica.repository.MedicoRepository;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository medicoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EspecialidadeRepository especialidadeRepository;

    public MedicoService(
            MedicoRepository medicoRepository,
            UsuarioRepository usuarioRepository,
            EspecialidadeRepository especialidadeRepository
    ) {
        this.medicoRepository = medicoRepository;
        this.usuarioRepository = usuarioRepository;
        this.especialidadeRepository = especialidadeRepository;
    }

    public MedicoResponseDTO cadastrar(MedicoCadastroRequestDTO dto) {

        Especialidade especialidade = especialidadeRepository.findById(dto.especialidadeId())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setTelefone(dto.telefone());

        usuario.setTipoUsuario(TipoUsuario.MEDICO);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        Medico medico = new Medico();

        medico.setUsuario(usuarioSalvo);
        medico.setCrm(dto.crm());
        medico.setEspecialidade(especialidade);

        Medico medicoSalvo = medicoRepository.save(medico);

        return new MedicoResponseDTO(
                medicoSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getTelefone(),
                medicoSalvo.getCrm(),
                especialidade.getNome()
        );
    }

    public List<MedicoResponseDTO> listarMedicos() {

        return medicoRepository.findByUsuarioAtivoTrue()
                .stream()
                .filter(medico -> medico.getUsuario().getAtivo())
                .map(medico -> new MedicoResponseDTO(
                        medico.getId(),
                        medico.getUsuario().getNome(),
                        medico.getUsuario().getEmail(),
                        medico.getUsuario().getTelefone(),
                        medico.getCrm(),
                        medico.getEspecialidade().getNome()
                ))
                .toList();
    }

    public MedicoResponseDTO buscarPorId(Long id) {

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        return new MedicoResponseDTO(
                medico.getId(),
                medico.getUsuario().getNome(),
                medico.getUsuario().getEmail(),
                medico.getUsuario().getTelefone(),
                medico.getCrm(),
                medico.getEspecialidade().getNome()
        );
    }

    public MedicoResponseDTO atualizar(Long id, MedicoAtualizacaoRequestDTO dto) {

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Usuario usuario = medico.getUsuario();

        if (dto.nome() != null) {
            usuario.setNome(dto.nome());
        }

        if (dto.email() != null) {
            usuario.setEmail(dto.email());
        }

        if (dto.telefone() != null) {
            usuario.setTelefone(dto.telefone());
        }

        Especialidade especialidade = especialidadeRepository.findById(dto.especialidadeId())
                .orElseThrow(() -> new RuntimeException("Especialidade não encontrada"));

        medico.setCrm(dto.crm());
        medico.setEspecialidade(especialidade);

        usuarioRepository.save(usuario);
        Medico medicoAtualizado = medicoRepository.save(medico);

        return new MedicoResponseDTO(
                medicoAtualizado.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                medicoAtualizado.getCrm(),
                medicoAtualizado.getEspecialidade().getNome()
        );
    }

    public void desativar(Long id) {

        Medico medico = medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Usuario usuario = medico.getUsuario();

        usuario.setAtivo(false);

        medicoRepository.save(medico);
    }
}
