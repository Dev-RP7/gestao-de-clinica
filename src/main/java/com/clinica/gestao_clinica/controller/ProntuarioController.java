package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
import com.clinica.gestao_clinica.service.ProntuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/prontuario")
public class ProntuarioController {

    private final ProntuarioService prontuarioService;

    public ProntuarioController(ProntuarioService prontuarioService) {
        this.prontuarioService = prontuarioService;
    }

    @PostMapping
    public ResponseEntity<ProntuarioResponseDTO> criar(
            @RequestBody @Valid ProntuarioRequestDTO dto
    ) {

        ProntuarioResponseDTO prontuario = prontuarioService.criar(dto);

        return ResponseEntity.ok(prontuario);

    }

    @GetMapping
    public ResponseEntity<List<ProntuarioResponseDTO>> buscarTodos() {

        return ResponseEntity.ok(prontuarioService.buscarTodos());

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProntuarioResponseDTO> buscarPorId(@PathVariable Long id) {

        return ResponseEntity.ok(prontuarioService.buscarPorId(id));

    }
/*

    @GetMapping("/consulta/{consultaId}")

    @GetMapping("/paciente/{pacienteId}")
*/

    @PutMapping("/{id}")
    public ResponseEntity<ProntuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid ProntuarioRequestDTO dto
    ) {

        return ResponseEntity.ok(prontuarioService.atualizar(id, dto));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ProntuarioResponseDTO> deletar(@PathVariable Long id) {

        prontuarioService.deletarProntuario(id);
        return ResponseEntity.noContent().build();
    }
 /*

*/
}
