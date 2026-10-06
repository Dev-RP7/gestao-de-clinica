// Pacote dos repositórios.
package com.clinica.gestao_clinica.repository;

// Entidade e enum.
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Paginação.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
// Interface base e anotação de consulta.
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// Liga parâmetros.
import org.springframework.data.repository.query.Param;

// Optional: "caixa" que pode ter um valor ou estar vazia (evita NullPointerException).
import java.util.Optional;

// Repositório da entidade Usuario.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Lista usuários ATIVOS, com filtros opcionais por parte do nome e por perfil.
    @Query("""
            select u from Usuario u
            where u.ativo = true
              and (:nome is null or lower(u.nome) like lower(concat('%', :nome, '%')))
              and (:tipo is null or u.tipoUsuario = :tipo)
            """)
    // Devolve uma página de usuários.
    Page<Usuario> filtrar(
            // Parte do nome.
            @Param("nome") String nome,
            // Perfil.
            @Param("tipo") TipoUsuario tipo,
            // Paginação.
            Pageable pageable);

    // Busca pelo e-mail (usado no login).
    Optional<Usuario> findByEmail(String email);

    // Verifica se o e-mail já está em uso.
    boolean existsByEmail(String email);

    // Verifica se o e-mail está em uso por OUTRO usuário (usado na atualização).
    boolean existsByEmailAndIdNot(String email, Long id);

    // Verifica se existe pelo menos um usuário de determinado perfil (usado para criar o admin inicial).
    boolean existsByTipoUsuario(TipoUsuario tipoUsuario);

}
