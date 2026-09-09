package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.EspecialidadeCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.EspecialidadeResponseDTO;
import com.clinica.gestao_clinica.service.EspecialidadeService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/especialidade")
public class EspecialidadeController {

    private final EspecialidadeService especialidadeService;

    public EspecialidadeController(EspecialidadeService especialidadeService) {
        this.especialidadeService = especialidadeService;
    }

    @PostMapping
    public ResponseEntity<EspecialidadeResponseDTO> cadastrar(
            @Valid @RequestBody EspecialidadeCadastroRequestDTO
            dto
            ) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(especialidadeService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<EspecialidadeResponseDTO>> listar() {

        List<EspecialidadeResponseDTO> especialidades = especialidadeService.listar();

        return ResponseEntity.ok(especialidades);
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<List<EspecialidadeResponseDTO>> listarPorNome(
            @PathVariable String nome
    ) {

        return ResponseEntity.ok(
                especialidadeService.buscarPorNome(nome)
        );
    }
}
