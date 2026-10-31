package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.MedicoAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.MedicoCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
import com.clinica.gestao_clinica.service.MedicoService;
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
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService medicoService;

    public MedicoController(MedicoService medicoService) {
        this.medicoService = medicoService;
    }

    @PostMapping
    public ResponseEntity<MedicoResponseDTO> cadastrar(@RequestBody @Valid MedicoCadastroRequestDTO dados) {
        MedicoResponseDTO medico = medicoService.cadastrar(dados);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(medico.id()).toUri();
        return ResponseEntity.created(location).body(medico);
    }

    @GetMapping
    public ResponseEntity<Page<MedicoResponseDTO>> listar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) Long especialidadeId,
            @ParameterObject @PageableDefault(size = 10, sort = "usuario.nome") Pageable pageable) {
        return ResponseEntity.ok(medicoService.listar(nome, especialidadeId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(medicoService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MedicoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid MedicoAtualizacaoRequestDTO dados) {
        return ResponseEntity.ok(medicoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        medicoService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
