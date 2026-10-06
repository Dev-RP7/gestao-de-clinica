// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTOs e service.
import com.clinica.gestao_clinica.dto.request.ReceitaRequestDTO;
import com.clinica.gestao_clinica.dto.response.ReceitaResponseDTO;
import com.clinica.gestao_clinica.service.ReceitaService;
// Ativa a validação.
import jakarta.validation.Valid;
// Swagger: parâmetros de paginação separados.
import org.springdoc.core.annotations.ParameterObject;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
// Rotas começam com /receitas (antes era /receita). Acesso: médico ou administrador.
@RequestMapping("/receitas")
// Controller de receitas.
public class ReceitaController {

    // Service de receitas.
    private final ReceitaService receitaService;

    // Injeção de dependências.
    public ReceitaController(ReceitaService receitaService) {
        // Guarda o service.
        this.receitaService = receitaService;
    }

    // POST /receitas.
    @PostMapping
    // Recebe o JSON validado.
    public ResponseEntity<ReceitaResponseDTO> criar(@RequestBody @Valid ReceitaRequestDTO dto) {
        // Cria.
        ReceitaResponseDTO receita = receitaService.criar(dto);
        // Monta a URL do novo recurso.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(receita.id()).toUri();
        // Devolve 201 Created.
        return ResponseEntity.created(location).body(receita);
    }

    // GET /receitas?prontuarioId=4&page=0&size=10
    @GetMapping
    // Filtro opcional por prontuário.
    public ResponseEntity<Page<ReceitaResponseDTO>> listar(
            // Id do prontuário.
            @RequestParam(required = false) Long prontuarioId,
            // Paginação. Padrão: 10 por página, ordenado pelo id.
            @ParameterObject @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        // Devolve 200 OK.
        return ResponseEntity.ok(receitaService.listar(prontuarioId, pageable));
    }

    // GET /receitas/{id}.
    @GetMapping("/{id}")
    // Busca pelo id.
    public ResponseEntity<ReceitaResponseDTO> buscarPorId(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(receitaService.buscarPorId(id));
    }

    // PUT /receitas/{id}.
    @PutMapping("/{id}")
    // Atualiza a receita.
    public ResponseEntity<ReceitaResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ReceitaRequestDTO dto) {
        // Devolve 200 OK.
        return ResponseEntity.ok(receitaService.atualizar(id, dto));
    }

    // DELETE /receitas/{id}.
    @DeleteMapping("/{id}")
    // Exclui.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Exclui.
        receitaService.excluir(id);
        // Devolve 204 No Content.
        return ResponseEntity.noContent().build();
    }
}
