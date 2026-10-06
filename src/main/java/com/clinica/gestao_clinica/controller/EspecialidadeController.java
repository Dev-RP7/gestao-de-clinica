// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTOs e service.
import com.clinica.gestao_clinica.dto.request.EspecialidadeCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.EspecialidadeResponseDTO;
import com.clinica.gestao_clinica.service.EspecialidadeService;
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
// Rotas começam com /especialidades (antes era /especialidade, no singular).
@RequestMapping("/especialidades")
// Controller de especialidades.
public class EspecialidadeController {

    // Service de especialidades.
    private final EspecialidadeService especialidadeService;

    // Injeção de dependências.
    public EspecialidadeController(EspecialidadeService especialidadeService) {
        // Guarda o service.
        this.especialidadeService = especialidadeService;
    }

    // POST /especialidades (só administrador).
    @PostMapping
    // Recebe o JSON validado.
    public ResponseEntity<EspecialidadeResponseDTO> cadastrar(@Valid @RequestBody EspecialidadeCadastroRequestDTO dto) {
        // Cadastra.
        EspecialidadeResponseDTO especialidade = especialidadeService.cadastrar(dto);
        // Monta a URL do novo recurso.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(especialidade.id()).toUri();
        // Devolve 201 Created.
        return ResponseEntity.created(location).body(especialidade);
    }

    // GET /especialidades?nome=cardio&page=0&size=10
    @GetMapping
    // Filtro opcional por parte do nome (substitui a antiga rota /especialidade/nome/{nome}).
    public ResponseEntity<Page<EspecialidadeResponseDTO>> listar(
            // Parte do nome.
            @RequestParam(required = false) String nome,
            // Paginação. Padrão: 10 por página, em ordem alfabética.
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        // Devolve 200 OK com a página.
        return ResponseEntity.ok(especialidadeService.listar(nome, pageable));
    }

    // GET /especialidades/{id}.
    @GetMapping("/{id}")
    // Busca pelo id.
    public ResponseEntity<EspecialidadeResponseDTO> buscarPorId(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(especialidadeService.buscarPorId(id));
    }

    // PUT /especialidades/{id} (só administrador).
    @PutMapping("/{id}")
    // Renomeia a especialidade.
    public ResponseEntity<EspecialidadeResponseDTO> atualizar(@PathVariable Long id,
                                                              @Valid @RequestBody EspecialidadeCadastroRequestDTO dto) {
        // Devolve 200 OK.
        return ResponseEntity.ok(especialidadeService.atualizar(id, dto));
    }

    // DELETE /especialidades/{id} (só administrador).
    @DeleteMapping("/{id}")
    // Exclui a especialidade.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Exclui.
        especialidadeService.excluir(id);
        // Devolve 204 No Content.
        return ResponseEntity.noContent().build();
    }
}
