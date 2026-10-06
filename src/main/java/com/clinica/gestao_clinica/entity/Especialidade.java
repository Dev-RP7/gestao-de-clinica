// Pacote das entidades.
package com.clinica.gestao_clinica.entity;

// Anotações do JPA.
import jakarta.persistence.*;
// Anotações do Lombok.
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Esta classe é uma tabela no banco.
@Entity
// O nome da tabela é "especialidade".
@Table(name = "especialidade")
// Gera os getters.
@Getter
// Gera os setters.
@Setter
// Gera o construtor vazio (exigido pelo JPA).
@NoArgsConstructor
// Gera o construtor com todos os campos.
@AllArgsConstructor
// Especialidade médica (Cardiologia, Pediatria...).
public class Especialidade {

    // Chave primária.
    @Id
    // Gerada automaticamente pelo banco.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador da especialidade.
    private Long id;

    // Obrigatório e único: não pode haver duas "Cardiologia".
    @Column(nullable = false, unique = true)
    // Nome da especialidade.
    private String nome;

}
