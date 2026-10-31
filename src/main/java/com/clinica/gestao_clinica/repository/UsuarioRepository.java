package com.clinica.gestao_clinica.repository;

import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    @Query("""
            select u from Usuario u
            where u.ativo = true
              and (:nome is null or lower(u.nome) like lower(concat('%', :nome, '%')))
              and (:tipo is null or u.tipoUsuario = :tipo)
            """)
    Page<Usuario> filtrar(
            @Param("nome") String nome,
            @Param("tipo") TipoUsuario tipo,
            Pageable pageable);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    boolean existsByTipoUsuario(TipoUsuario tipoUsuario);

}
