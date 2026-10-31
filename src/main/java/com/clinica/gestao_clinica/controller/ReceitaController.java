package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
import com.clinica.gestao_clinica.service.ReceitaService;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/receitas")
public class ReceitaController {

    private final ReceitaService receitaService;

    public ReceitaController(ReceitaService receitaService) {
        this.receitaService = receitaService;
    }

    @PostMapping
    public ResponseEntity<ReceitaResponseDTO> criar(@RequestBody @Valid ReceitaRequestDTO dto) {
        ReceitaResponseDTO receita = receitaService.criar(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(receita.id()).toUri();
        return ResponseEntity.created(location).body(receita);
    }

    @GetMapping
    public ResponseEntity<Page<ReceitaResponseDTO>> listar(
            @RequestParam(required = false) Long prontuarioId,
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(receitaService.listar(prontuarioId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(receitaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ReceitaRequestDTO dto) {
        return ResponseEntity.ok(receitaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        receitaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
