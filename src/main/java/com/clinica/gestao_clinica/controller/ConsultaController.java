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

    @GetMapping("/paciente/{id}")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorPaciente(
            @PathVariable Long pacienteId){

        return ResponseEntity.ok(
                consultaService.listarConsultasDoPaciente(pacienteId)
        );
    }

    @GetMapping("/medico/{nome}")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorMedico(
            @PathVariable String nome
    ) {

        return ResponseEntity.ok(
                consultaService.buscarPorMedico(nome)
        );

    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorStatus(
            @PathVariable StatusConsulta status
    ) {

        return ResponseEntity.ok(
                consultaService.buscarPorStatus(status)
        );
    }

    @GetMapping("/data/{data}")
    public ResponseEntity<List<ConsultaResponseDTO>> listarPorData(
            @PathVariable LocalDate data
    ) {

        return ResponseEntity.ok(
                consultaService.buscarPorDataConsulta(data)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> atualizarConsulta(
            @PathVariable Long id,
            @RequestBody @Valid ConsultaRequestDTO consulta
    ) {

        return ResponseEntity.ok(consultaService.atualizar(id, consulta));

    }

    @DeleteMapping("/paciente/{pacienteId}")
    public ResponseEntity<ConsultaResponseDTO> cancelarConsulta(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                consultaService.cancelarConsulta(id)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ConsultaResponseDTO> deletarConsulta(
            @PathVariable Long id
    ) {
        consultaService.cancelarConsulta(id);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<ConsultaResponseDTO> confirmarConsulta(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                consultaService.confirmarConsulta(id)
        );
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<ConsultaResponseDTO> concluirConsulta(
            @PathVariable Long id
    ) {

        return  ResponseEntity.ok(
                consultaService.concluirConsulta(id)
        );
    }



}
