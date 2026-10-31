package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.ProntuarioAtualizadoRequestDTO;
import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
import com.clinica.gestao_clinica.service.ProntuarioService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/prontuarios")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @PostMapping
    public ResponseEntity<ProntuarioResponseDTO> criar(@RequestBody @Valid ProntuarioRequestDTO dto) {
        ProntuarioResponseDTO prontuario = prontuarioService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(prontuario.id()).toUri();
        return ResponseEntity.created(location).body(prontuario);
    }

    @GetMapping
    public ResponseEntity<Page<ProntuarioResponseDTO>> listar(
            @RequestParam(required = false) Long pacienteId,
            @RequestParam(required = false) Long medicoId,
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(prontuarioService.listar(pacienteId, medicoId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(prontuarioService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProntuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ProntuarioAtualizadoRequestDTO dto) {
        return ResponseEntity.ok(prontuarioService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        prontuarioService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
