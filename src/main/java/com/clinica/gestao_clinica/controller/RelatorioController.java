// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTO e service.
import com.clinica.gestao_clinica.dto.response.RelatorioConsultaResponseDTO;
import com.clinica.gestao_clinica.service.RelatorioService;
// Resposta HTTP.
import org.springframework.http.ResponseEntity;
// Anotações de rota.
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Controller REST.
@RestController
// Rotas começam com /relatorios (só administrador).
@RequestMapping("/relatorios")
// Controller de relatórios.
public class RelatorioController {

    // Service de relatórios.
    private final RelatorioService relatorioService;

    // Injeção de dependências.
    public RelatorioController(RelatorioService relatorioService) {
        // Guarda o service.
        this.relatorioService = relatorioService;
    }

    // GET /relatorios/consultas.
    @GetMapping("/consultas")
    // Gera o relatório de consultas por status.
    public ResponseEntity<RelatorioConsultaResponseDTO> relatorioConsultas() {
        // Devolve 200 OK.
        return ResponseEntity.ok(relatorioService.gerarRelatorio());
    }
}
