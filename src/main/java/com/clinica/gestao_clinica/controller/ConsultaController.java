// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTOs, enum e service.
import com.clinica.gestao_clinica.dto.request.ConsultaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ConsultaResponseDTO;
import com.clinica.gestao_clinica.enums.StatusConsulta;
import com.clinica.gestao_clinica.service.ConsultaService;
// Ativa a validação.
import jakarta.validation.Valid;
// Faz o Swagger mostrar page, size e sort como parâmetros separados.
import org.springdoc.core.annotations.ParameterObject;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
// Converte texto da URL ("2026-12-01") em LocalDate.
import org.springframework.format.annotation.DateTimeFormat;
// Resposta HTTP.
import org.springframework.http.ResponseEntity;
// Anotações de rota (o "*" importa todas: @GetMapping, @PostMapping...).
import org.springframework.web.bind.annotation.*;
// Ajuda a montar a URL do recurso criado.
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// URI e data.
import java.net.URI;
import java.time.LocalDate;

// Controller REST.
@RestController
// Rotas começam com /consultas.
@RequestMapping("/consultas")
// Controller de consultas.
public class ConsultaController {

    // Service de consultas.
    private final ConsultaService consultaService;

    // Injeção de dependências.
    public ConsultaController(ConsultaService consultaService) {
        // Guarda o service.
        this.consultaService = consultaService;
    }

    // POST /consultas: agenda uma consulta.
    @PostMapping
    // Recebe o JSON validado.
    public ResponseEntity<ConsultaResponseDTO> agendar(@Valid @RequestBody ConsultaRequestDTO dto) {
        // Agenda a consulta.
        ConsultaResponseDTO consulta = consultaService.agendar(dto);
        // Monta a URL do novo recurso, ex.: http://localhost:8080/consultas/15.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(consulta.id()).toUri();
        // Devolve 201 Created com o cabeçalho Location e a consulta no corpo.
        return ResponseEntity.created(location).body(consulta);
    }

    // GET /consultas?medicoId=1&status=AGENDADA&dataInicio=2026-12-01&page=0&size=10&sort=dataConsulta,desc
    @GetMapping
    // Todos os filtros são opcionais (required = false).
    public ResponseEntity<Page<ConsultaResponseDTO>> listar(
            // Filtro por médico.
            @RequestParam(required = false) Long medicoId,
            // Filtro por paciente.
            @RequestParam(required = false) Long pacienteId,
            // Filtro por status (o Spring converte o texto no enum).
            @RequestParam(required = false) StatusConsulta status,
            // Data inicial no formato ISO (aaaa-mm-dd).
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            // Data final no formato ISO.
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            // Paginação. Padrão: 10 por página, ordenado pela data da consulta.
            @ParameterObject @PageableDefault(size = 10, sort = "dataConsulta") Pageable pageable) {
        // Busca a página e devolve 200 OK.
        return ResponseEntity.ok(consultaService.listar(medicoId, pacienteId, status, dataInicio, dataFim, pageable));
    }

    // GET /consultas/{id}: o {id} da URL vira o parâmetro "id".
    @GetMapping("/{id}")
    // @PathVariable lê o valor do caminho.
    public ResponseEntity<ConsultaResponseDTO> buscarPorId(@PathVariable Long id) {
        // Devolve 200 OK com a consulta.
        return ResponseEntity.ok(consultaService.buscarPorId(id));
    }

    // PUT /consultas/{id}: remarca a consulta.
    @PutMapping("/{id}")
    // Recebe o id e o JSON validado.
    public ResponseEntity<ConsultaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ConsultaRequestDTO dto) {
        // Devolve 200 OK com a consulta atualizada.
        return ResponseEntity.ok(consultaService.atualizar(id, dto));
    }

    // PATCH /consultas/{id}/confirmar: PATCH = alteração parcial (aqui, só o status).
    @PatchMapping("/{id}/confirmar")
    // Confirma a consulta.
    public ResponseEntity<ConsultaResponseDTO> confirmar(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(consultaService.confirmar(id));
    }

    // PATCH /consultas/{id}/concluir (só médico ou administrador, ver SecurityConfig).
    @PatchMapping("/{id}/concluir")
    // Conclui a consulta.
    public ResponseEntity<ConsultaResponseDTO> concluir(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(consultaService.concluir(id));
    }

    // PATCH /consultas/{id}/cancelar.
    @PatchMapping("/{id}/cancelar")
    // Cancela a consulta.
    public ResponseEntity<ConsultaResponseDTO> cancelar(@PathVariable Long id) {
        // Devolve 200 OK com a consulta cancelada.
        return ResponseEntity.ok(consultaService.cancelar(id));
    }

    // DELETE /consultas/{id}: também cancela (não apaga do banco, para manter o histórico).
    @DeleteMapping("/{id}")
    // Cancela a consulta.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Cancela.
        consultaService.cancelar(id);
        // Devolve 204 No Content (sucesso sem corpo).
        return ResponseEntity.noContent().build();
    }
}
