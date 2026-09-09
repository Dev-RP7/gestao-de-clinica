package com.clinica.gestao_clinica.controller;

import com.clinica.gestao_clinica.dto.response.RelatorioConsultaResponseDTO;
import com.clinica.gestao_clinica.service.RelatorioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService relatorioService;

    public RelatorioController(
            RelatorioService relatorioService
    ) {
        this.relatorioService = relatorioService;
    }

    @GetMapping("/consultas")
    public ResponseEntity<RelatorioConsultaResponseDTO> relatorioConsultas() {

        return ResponseEntity.ok(
                relatorioService.gerarRelatorio()
        );
    }
}
