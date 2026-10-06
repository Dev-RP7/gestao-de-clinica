// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTOs e service.
import com.clinica.gestao_clinica.dto.request.MedicoAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.MedicoCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
import com.clinica.gestao_clinica.service.MedicoService;
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
// Rotas começam com /medicos.
@RequestMapping("/medicos")
// Controller de médicos.
public class MedicoController {

    // Service de médicos.
    private final MedicoService medicoService;

    // Injeção de dependências.
    public MedicoController(MedicoService medicoService) {
        // Guarda o service.
        this.medicoService = medicoService;
    }

    // POST /medicos (só administrador).
    @PostMapping
    // Recebe o JSON validado.
    public ResponseEntity<MedicoResponseDTO> cadastrar(@RequestBody @Valid MedicoCadastroRequestDTO dados) {
        // Cadastra.
        MedicoResponseDTO medico = medicoService.cadastrar(dados);
        // Monta a URL do novo recurso.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(medico.id()).toUri();
        // Devolve 201 Created (antes devolvia 200).
        return ResponseEntity.created(location).body(medico);
    }

    // GET /medicos?nome=ana&especialidadeId=2&page=0&size=10
    @GetMapping
    // Filtros opcionais.
    public ResponseEntity<Page<MedicoResponseDTO>> listar(
            // Parte do nome.
            @RequestParam(required = false) String nome,
            // Id da especialidade.
            @RequestParam(required = false) Long especialidadeId,
            // Paginação. Padrão: 10 por página, ordenado pelo nome do usuário.
            @ParameterObject @PageableDefault(size = 10, sort = "usuario.nome") Pageable pageable) {
        // Devolve 200 OK.
        return ResponseEntity.ok(medicoService.listar(nome, especialidadeId, pageable));
    }

    // GET /medicos/{id}.
    @GetMapping("/{id}")
    // Busca pelo id.
    public ResponseEntity<MedicoResponseDTO> buscarPorId(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(medicoService.buscarPorId(id));
    }

    // PUT /medicos/{id} (só administrador).
    @PutMapping("/{id}")
    // Agora com @Valid (antes as validações deste DTO eram ignoradas).
    public ResponseEntity<MedicoResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid MedicoAtualizacaoRequestDTO dados) {
        // Devolve 200 OK.
        return ResponseEntity.ok(medicoService.atualizar(id, dados));
    }

    // DELETE /medicos/{id} (só administrador): desativa o médico.
    @DeleteMapping("/{id}")
    // Desativa.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Desativa.
        medicoService.desativar(id);
        // Devolve 204 No Content.
        return ResponseEntity.noContent().build();
    }
}
