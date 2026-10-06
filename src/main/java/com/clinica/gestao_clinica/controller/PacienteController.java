// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTOs e service.
import com.clinica.gestao_clinica.dto.request.PacienteAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.PacienteRequestDTO;
import com.clinica.gestao_clinica.dto.response.PacienteResponseDTO;
import com.clinica.gestao_clinica.service.PacienteService;
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
// Rotas começam com /pacientes.
@RequestMapping("/pacientes")
// Controller de pacientes.
public class PacienteController {

    // Service de pacientes.
    private final PacienteService pacienteService;

    // Injeção de dependências.
    public PacienteController(PacienteService pacienteService) {
        // Guarda o service.
        this.pacienteService = pacienteService;
    }

    // POST /pacientes (rota pública: autocadastro).
    @PostMapping
    // Recebe o JSON validado.
    public ResponseEntity<PacienteResponseDTO> cadastrar(@RequestBody @Valid PacienteRequestDTO dto) {
        // Cadastra.
        PacienteResponseDTO paciente = pacienteService.cadastrar(dto);
        // Monta a URL do novo recurso.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(paciente.id()).toUri();
        // Devolve 201 Created.
        return ResponseEntity.created(location).body(paciente);
    }

    // GET /pacientes?nome=maria&cpf=12345678901&page=0&size=10 (administrador ou médico).
    @GetMapping
    // Filtros opcionais.
    public ResponseEntity<Page<PacienteResponseDTO>> listar(
            // Parte do nome.
            @RequestParam(required = false) String nome,
            // CPF exato.
            @RequestParam(required = false) String cpf,
            // Paginação. Padrão: 10 por página, ordenado pelo nome.
            @ParameterObject @PageableDefault(size = 10, sort = "usuario.nome") Pageable pageable) {
        // Devolve 200 OK.
        return ResponseEntity.ok(pacienteService.listar(nome, cpf, pageable));
    }

    // GET /pacientes/{id} (administrador ou médico).
    @GetMapping("/{id}")
    // Busca pelo id.
    public ResponseEntity<PacienteResponseDTO> buscarPorId(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(pacienteService.buscarPorId(id));
    }

    // PUT /pacientes/{id} (só administrador).
    @PutMapping("/{id}")
    // Atualiza os campos enviados.
    public ResponseEntity<PacienteResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid PacienteAtualizacaoRequestDTO dto) {
        // Devolve 200 OK.
        return ResponseEntity.ok(pacienteService.atualizar(id, dto));
    }

    // DELETE /pacientes/{id} (só administrador): desativa o paciente.
    @DeleteMapping("/{id}")
    // Desativa.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Desativa.
        pacienteService.desativar(id);
        // Devolve 204 No Content.
        return ResponseEntity.noContent().build();
    }

}
