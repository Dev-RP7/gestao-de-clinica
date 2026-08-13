package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
import com.clinica.gestao_clinica.service.ReceitaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/receita")
public class ReceitaController {

    private final ReceitaService receitaService;

    public ReceitaController(ReceitaService receitaService) {
        this.receitaService = receitaService;
    }

    @PostMapping
    public ResponseEntity<ReceitaResponseDTO> criarReceita(
            @RequestBody @Valid ReceitaRequestDTO dto
    ) {

        ReceitaResponseDTO receita = receitaService.criarReceita(dto);

        return ResponseEntity.ok(receita);
    }

    @GetMapping
    public ResponseEntity<List<ReceitaResponseDTO>> listarReceitas() {

        return ResponseEntity.ok(receitaService.listarTodasReceitas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReceitaResponseDTO> buscarReceita(@PathVariable Long id) {

        return ResponseEntity.ok(receitaService.buscarPorId(id));

    }

    @GetMapping("/prontuario/{prontuarioId}")

    @PutMapping("/{id}")
    public ResponseEntity<ReceitaResponseDTO> atualizarReceita(
            @PathVariable Long id,
            @RequestBody @Valid ReceitaRequestDTO dto
    ) {

        return ResponseEntity.ok(receitaService.alterarReceita(id, dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarReceita(@PathVariable Long id) {

        receitaService.deletarReceita(id);
        return ResponseEntity.noContent().build();
    }
    /*

*/
}
