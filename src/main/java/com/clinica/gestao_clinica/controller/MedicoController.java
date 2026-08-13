package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.MedicoCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
import com.clinica.gestao_clinica.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping
    public ResponseEntity<MedicoResponseDTO> cadastrar(
            @RequestBody @Valid MedicoCadastroRequestDTO dados
    ) {

        MedicoResponseDTO medico = medicoService.cadastrar(dados);

        return ResponseEntity.ok(medico);

    }

    @GetMapping
    public ResponseEntity<List<MedicoResponseDTO>> listar() {

        List<MedicoResponseDTO> medicos = medicoService.listarMedicos();

        return ResponseEntity.ok(medicos);

    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {

        MedicoResponseDTO medico = medicoService.buscarPorId(id);

        return ResponseEntity.ok(medico);

    }
/*

    @GetMapping("/crm/{crm}")

    @GetMapping("/especialidade/{id}")

    @GetMapping("/{id}/consultas")
*/

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody MedicoAtualizacaoRequestDTO dados
    ) {

        MedicoResponseDTO medico = medicoService.atualizar(id, dados);

        return ResponseEntity.ok(medico);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        medicoService.desativar(id);

        return ResponseEntity.noContent().build();
    }







}
