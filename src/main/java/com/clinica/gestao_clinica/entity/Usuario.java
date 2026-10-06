// Pacote das entidades (classes que viram tabelas no banco).
package com.clinica.gestao_clinica.entity;

// Enum com os perfis de usuário.
import com.clinica.gestao_clinica.enums.TipoUsuario;
// Anotações do JPA (@Entity, @Id, @Column...).
import jakarta.persistence.*;
// Anotações do Lombok que geram código automaticamente.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
// Anotações do Hibernate que preenchem datas sozinhas.
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
// Classes do Spring Security que representam permissões e o usuário logado.
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

// Tipos de data/hora e lista do Java.
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

// Lombok: gera um getter para cada campo (getNome(), getEmail()...).
@Getter
// Lombok: gera um setter para cada campo (setNome(...), setEmail(...)...).
@Setter
// Lombok: gera um construtor vazio (o JPA exige isso).
@NoArgsConstructor
// Lombok: gera um construtor com todos os campos.
@AllArgsConstructor
// JPA: esta classe é uma entidade, ou seja, representa uma tabela.
@Entity
// JPA: o nome da tabela no banco é "usuario".
@Table(name = "usuario")
// "implements UserDetails" permite que o Spring Security use esta classe no login.
public class Usuario implements UserDetails {

    // Chave primária da tabela.
    @Id
    // O banco gera o id automaticamente (AUTO_INCREMENT no MySQL).
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador único do usuário.
    private Long id;

    // Coluna obrigatória (NOT NULL) com no máximo 100 caracteres.
    @Column(nullable = false, length = 100)
    // Nome completo.
    private String nome;

    // unique = true: não pode haver dois usuários com o mesmo e-mail.
    @Column(nullable = false, unique = true, length = 100)
    // E-mail, usado como login.
    private String email;

    // Coluna obrigatória. Guarda a senha CRIPTOGRAFADA (BCrypt), nunca a senha pura.
    @Column(nullable = false)
    // Senha criptografada.
    private String senha;

    // Telefone obrigatório com até 15 caracteres.
    @Column(nullable = false, length = 15)
    // Telefone de contato.
    private String telefone;

    // Salva o enum como texto ("MEDICO") em vez de número (0, 1, 2).
    @Enumerated(EnumType.STRING)
    // A coluna se chama tipo_usuario e é obrigatória.
    @Column(name = "tipo_usuario", nullable = false)
    // Perfil do usuário.
    private TipoUsuario tipoUsuario;

    // Coluna obrigatória.
    @Column(nullable = false)
    // Usamos "exclusão lógica": em vez de apagar, marcamos ativo = false.
    private Boolean ativo = true;

    // O Hibernate preenche com a data/hora atual quando o registro é criado.
    @CreationTimestamp
    // updatable = false: esta coluna nunca muda depois de criada.
    @Column(name = "data_criacao", nullable = false, updatable = false)
    // Data de criação do registro.
    private LocalDateTime dataCriacao;

    // O Hibernate atualiza com a data/hora atual a cada alteração.
    @UpdateTimestamp
    // Nome da coluna no banco.
    @Column(name = "data_atualizacao")
    // Data da última alteração.
    private LocalDateTime dataAtualizacao;

    // ===== Métodos exigidos pela interface UserDetails (Spring Security) =====

    // Implementa um método da interface UserDetails.
    @Override
    // Devolve as permissões (roles) do usuário.
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // O Spring espera o prefixo "ROLE_". Ex.: ADMINISTRADOR vira "ROLE_ADMINISTRADOR".
        return List.of(new SimpleGrantedAuthority("ROLE_" + tipoUsuario.name()));
    }

    // Implementa um método da interface UserDetails.
    @Override
    // O Spring chama este método para pegar a senha (criptografada) e comparar com a digitada.
    public String getPassword() {
        // Devolve a senha criptografada.
        return senha;
    }

    // Implementa um método da interface UserDetails.
    @Override
    // O "username" do Spring é o que o usuário digita no login. No nosso caso, o e-mail.
    public String getUsername() {
        // Devolve o e-mail.
        return email;
    }

    // Implementa um método da interface UserDetails.
    @Override
    // Usuários desativados (ativo = false) não conseguem fazer login.
    public boolean isEnabled() {
        // Boolean.TRUE.equals evita erro caso "ativo" esteja null.
        return Boolean.TRUE.equals(ativo);
    }
}
