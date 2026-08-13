package com.clinica.gestao_clinica.controller;


import com.clinica.gestao_clinica.dto.request.PacienteAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.dto.response.PacienteResponseDTO;
import com.clinica.gestao_clinica.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService pacienteService;

    public PacienteController(PacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    @PostMapping
    public ResponseEntity<PacienteResponseDTO> cadastrar(
            @RequestBody @Valid PacienteRequestDTO dto
    ) {

        PacienteResponseDTO paciente = pacienteService.cadastrar(dto);

        return ResponseEntity.ok(paciente);

    }

    @GetMapping
    public ResponseEntity<List<PacienteResponseDTO>> listarTodos() {

        return ResponseEntity.ok(pacienteService.listarTodos());

    }

    @GetMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> buscarPacientePorId(@PathVariable Long id) {

        return ResponseEntity.ok(pacienteService.buscarPorId(id));

    }
/*

    @GetMapping("/cpf/{cpf}")

    @GetMapping("/nome/{nome}")

    @GetMapping("/{id}/consultas")

    @GetMapping("/{id}/prontuario")
*/

    @PutMapping("/{id}")
    public ResponseEntity<PacienteResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid PacienteAtualizacaoRequestDTO dto
    ) {

        return ResponseEntity.ok(pacienteService.atualizar(id, dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        pacienteService.desativar(id);
        return ResponseEntity.noContent().build();
    }

}