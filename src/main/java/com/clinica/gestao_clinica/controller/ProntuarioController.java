// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTOs e service.
import com.clinica.gestao_clinica.dto.request.ProntuarioAtualizadoRequestDTO;
import com.clinica.gestao_clinica.dto.request.ProntuarioRequestDTO;
import com.clinica.gestao_clinica.dto.response.ProntuarioResponseDTO;
import com.clinica.gestao_clinica.service.ProntuarioService;
// Ativa a validação.
import jakarta.validation.Valid;
// Swagger: parâmetros de paginação separados.
import org.springdoc.core.annotations.ParameterObject;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
// Resposta HTTP.
import org.springframework.http.ResponseEntity;
// Anotações de rota.
import org.springframework.web.bind.annotation.*;
// Monta a URL do recurso criado.
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

// URI.
import java.net.URI;

// Controller REST.
@RestController
// Rotas começam com /prontuarios (antes era /prontuario). Acesso: médico ou administrador.
@RequestMapping("/prontuarios")
// Controller de prontuários.
public class ProntuarioController {

    // Service de prontuários.
    private final ProntuarioService prontuarioService;

    // Injeção de dependências.
    public ProntuarioController(ProntuarioService prontuarioService) {
        // Guarda o service.
        this.prontuarioService = prontuarioService;
    }

    // POST /prontuarios.
    @PostMapping
    // Recebe o JSON validado.
    public ResponseEntity<ProntuarioResponseDTO> criar(@RequestBody @Valid ProntuarioRequestDTO dto) {
        // Cria.
        ProntuarioResponseDTO prontuario = prontuarioService.criar(dto);
        // Monta a URL do novo recurso.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(prontuario.id()).toUri();
        // Devolve 201 Created.
        return ResponseEntity.created(location).body(prontuario);
    }

    // GET /prontuarios?pacienteId=3&medicoId=1&page=0&size=10
    @GetMapping
    // Filtros opcionais.
    public ResponseEntity<Page<ProntuarioResponseDTO>> listar(
            // Id do paciente.
            @RequestParam(required = false) Long pacienteId,
            // Id do médico.
            @RequestParam(required = false) Long medicoId,
            // Paginação. Padrão: 10 por página, mais recentes primeiro.
            @ParameterObject @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        // Devolve 200 OK.
        return ResponseEntity.ok(prontuarioService.listar(pacienteId, medicoId, pageable));
    }

    // GET /prontuarios/{id}.
    @GetMapping("/{id}")
    // Busca pelo id.
    public ResponseEntity<ProntuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(prontuarioService.buscarPorId(id));
    }

    // PUT /prontuarios/{id}.
    @PutMapping("/{id}")
    // Usa o DTO de atualização, que não tem consultaId.
    public ResponseEntity<ProntuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ProntuarioAtualizadoRequestDTO dto) {
        // Devolve 200 OK.
        return ResponseEntity.ok(prontuarioService.atualizar(id, dto));
    }

    // DELETE /prontuarios/{id}.
    @DeleteMapping("/{id}")
    // Exclui.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Exclui.
        prontuarioService.excluir(id);
        // Devolve 204 No Content.
        return ResponseEntity.noContent().build();
    }
}
