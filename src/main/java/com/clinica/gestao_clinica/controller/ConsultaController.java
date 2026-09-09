package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.entity.Consulta;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.service.ConsultaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consultas")
public class ConsultaController {

    private final ConsultaService consultaService;

    public ConsultaController(ConsultaService consultaService) {
        this.consultaService = consultaService;
    }


    @PostMapping
    public ResponseEntity<ConsultaResponseDTO> cadastrar(
            @Valid @RequestBody ConsultaRequestDTO dto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(consultaService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<ConsultaResponseDTO>> buscarTodas() {

        return ResponseEntity.ok(consultaService.listarTodas());

    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(consultaService.buscarConsultaPorId(id));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> atualizarConsulta(
            @PathVariable Long id,
            @RequestBody @Valid ConsultaRequestDTO consulta
    ) {

        return ResponseEntity.ok(consultaService.atualizar(id, consulta));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> deletarConsulta(
            @PathVariable Long id
    ) {
        consultaService.cancelarConsulta(id);
        return ResponseEntity.noContent().build();
    }
}
