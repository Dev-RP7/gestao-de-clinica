// Pacote dos controllers.
package com.clinica.gestao_clinica.controller;

// DTOs, enum e service.
import com.clinica.gestao_clinica.dto.request.UsuarioAtualizacaoRequestDTO;
import com.clinica.gestao_clinica.dto.request.UsuarioCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.UsuarioResponseDTO;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.service.UsuarioService;
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
// Rotas começam com /usuarios (só administrador).
@RequestMapping("/usuarios")
// Controller de usuários.
public class UsuarioController {

    // Service de usuários.
    private final UsuarioService usuarioService;

    // Injeção de dependências.
    public UsuarioController(UsuarioService usuarioService) {
        // Guarda o service.
        this.usuarioService = usuarioService;
    }

    // POST /usuarios: cadastra um novo administrador.
    @PostMapping
    // Recebe o JSON validado.
    public ResponseEntity<UsuarioResponseDTO> cadastrar(@RequestBody @Valid UsuarioCadastroRequestDTO dados) {
        // Cadastra.
        UsuarioResponseDTO usuario = usuarioService.cadastrar(dados);
        // Monta a URL do novo recurso.
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(usuario.id()).toUri();
        // Devolve 201 Created.
        return ResponseEntity.created(location).body(usuario);
    }

    // GET /usuarios?nome=ana&tipo=MEDICO&page=0&size=10
    @GetMapping
    // Filtros opcionais.
    public ResponseEntity<Page<UsuarioResponseDTO>> listar(
            // Parte do nome.
            @RequestParam(required = false) String nome,
            // Perfil.
            @RequestParam(required = false) TipoUsuario tipo,
            // Paginação. Padrão: 10 por página, ordenado pelo nome.
            @ParameterObject @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        // Devolve 200 OK.
        return ResponseEntity.ok(usuarioService.listar(nome, tipo, pageable));
    }

    // GET /usuarios/{id}.
    @GetMapping("/{id}")
    // Busca pelo id.
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long id) {
        // Devolve 200 OK.
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    // PUT /usuarios/{id}.
    @PutMapping("/{id}")
    // Agora com @Valid, para validar o formato do e-mail.
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioAtualizacaoRequestDTO dados) {
        // Devolve 200 OK.
        return ResponseEntity.ok(usuarioService.atualizar(id, dados));
    }

    // DELETE /usuarios/{id}: desativa o usuário.
    @DeleteMapping("/{id}")
    // Desativa.
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        // Desativa.
        usuarioService.desativar(id);
        // Devolve 204 No Content.
        return ResponseEntity.noContent().build();
    }
}
