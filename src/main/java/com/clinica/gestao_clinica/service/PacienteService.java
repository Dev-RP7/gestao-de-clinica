package com.clinica.gestao_clinica.service;

import com.clinica.gestao_clinica.dto.request.PacienteAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.dto.response.PacienteResponseDTO;
import com.clinica.gestao_clinica.entity.Paciente;
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.repository.PacienteRepository;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.Collections;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;
    private final UsuarioRepository usuarioRepository;

    public PacienteService(
            PacienteRepository pacienteRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.pacienteRepository = pacienteRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public PacienteResponseDTO cadastrar(PacienteRequestDTO dto) {

        if (pacienteRepository.existsBycpf(dto.cpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        Usuario usuario = new Usuario();

        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(dto.senha());
        usuario.setTelefone(dto.telefone());

        usuario.setTipoUsuario(TipoUsuario.PACIENTE);
        usuario.setAtivo(true);

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        Paciente paciente = new Paciente();

        paciente.setUsuario(usuarioSalvo);
        paciente.setCpf(dto.cpf());
        paciente.setRg(dto.rg());
        paciente.setDataNascimento(dto.dataNascimento());
        paciente.setSexo(dto.sexo());
        paciente.setEndereco(dto.endereco());
        paciente.setTipoSanguineo(dto.tipoSanguineo());
        paciente.setAlergias(dto.alergias());
        paciente.setObservacao(dto.observacao());

        Paciente pacienteSalvo = pacienteRepository.save(paciente);

        return new PacienteResponseDTO(
                pacienteSalvo.getId(),
                usuarioSalvo.getNome(),
                usuarioSalvo.getEmail(),
                usuarioSalvo.getTelefone(),
                pacienteSalvo.getCpf(),

                pacienteSalvo.getDataNascimento(),
                pacienteSalvo.getEndereco()
        );
    }

    public List<PacienteResponseDTO> listarTodos() {

        return pacienteRepository.findAll()
                .stream()
                .filter(paciente -> paciente.getUsuario().getAtivo())
                .map(paciente -> new PacienteResponseDTO(
                        paciente.getId(),
                        paciente.getUsuario().getNome(),
                        paciente.getUsuario().getEmail(),
                        paciente.getUsuario().getTelefone(),
                        paciente.getCpf(),
                        paciente.getDataNascimento(),
                        paciente.getEndereco()
                ))
                .toList();

    }

    public PacienteResponseDTO buscarPorId(Long id) {

        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        return new PacienteResponseDTO(
                paciente.getId(),
                paciente.getUsuario().getNome(),
                paciente.getUsuario().getEmail(),
                paciente.getUsuario().getTelefone(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.getEndereco()
        );
    }

    public PacienteResponseDTO atualizar(Long id, PacienteAtualizacaoRequestDTO dados) {

        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Usuario usuario = paciente.getUsuario();

        if (dados.nome() != null) {
            usuario.setNome(dados.nome());
        }

        if (dados.email() != null) {
            usuario.setEmail(dados.email());
        }

        if (dados.senha() != null) {
            usuario.setSenha(dados.senha());
        }

        if (dados.telefone() != null) {
            usuario.setTelefone(dados.telefone());
        }

        if (dados.endereco() != null) {
            paciente.setEndereco(dados.endereco());
        }

        if (dados.dataNascimento() != null) {
            paciente.setDataNascimento(dados.dataNascimento());
        }

        if (dados.sexo() != null) {
            paciente.setSexo(dados.sexo());
        }

        if (dados.tipoSanguineo() != null) {
            paciente.setTipoSanguineo(dados.tipoSanguineo());
        }

        if (dados.alergias() != null) {
            paciente.setAlergias(dados.alergias());
        }

        if (dados.observacao() != null) {
            paciente.setObservacao(dados.observacao());
        }

        usuarioRepository.save(usuario);
        pacienteRepository.save(paciente);

        return new PacienteResponseDTO(
                paciente.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getTelefone(),
                paciente.getCpf(),
                paciente.getDataNascimento(),
                paciente.getEndereco()
        );
    }

    public void desativar(Long id) {

        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        Usuario usuario = paciente.getUsuario();

        usuario.setAtivo(false);

        pacienteRepository.save(paciente);
    }
}
