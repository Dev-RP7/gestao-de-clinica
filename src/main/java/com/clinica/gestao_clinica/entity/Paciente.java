// Pacote das entidades.
package com.clinica.gestao_clinica.entity;

// Conversor que salva o tipo sanguíneo como "A+", "O-"...
import com.clinica.gestao_clinica.converter.TipoSanguineoConverter;
// Enums usados pelo paciente.
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
// Anotações do JPA.
import jakarta.persistence.*;
// Anotações do Lombok.
import lombok.*;
// Preenche a data de criação automaticamente.
import org.hibernate.annotations.CreationTimestamp;

// Tipos de data e lista.
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Gera os getters.
@Getter
// Gera os setters.
@Setter
// Gera o construtor vazio.
@NoArgsConstructor
// Gera o construtor completo.
@AllArgsConstructor
// Esta classe é uma tabela.
@Entity
// Nome da tabela.
@Table(name = "paciente")
// O paciente guarda os dados pessoais/médicos. Nome, e-mail e senha ficam no Usuario ligado a ele.
public class Paciente {

    // Chave primária.
    @Id
    // Gerada pelo banco.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador do paciente.
    private Long id;

    // Cada paciente tem um usuário.
    @OneToOne
    // Chave estrangeira para a tabela usuario. unique impede dois pacientes no mesmo usuário.
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    // Dados de login e contato do paciente.
    private Usuario usuario;

    // CPF obrigatório, único, com 11 dígitos (sem pontos e traço).
    @Column(nullable = false, unique = true, length = 11)
    // CPF do paciente.
    private String cpf;

    // RG obrigatório e único.
    @Column(nullable = false, unique = true, length = 20)
    // RG do paciente.
    private String rg;

    // Data de nascimento obrigatória.
    @Column(nullable = false)
    // LocalDate guarda só a data (sem hora).
    private LocalDate dataNascimento;

    // Salva o enum como texto.
    @Enumerated(EnumType.STRING)
    // Obrigatório.
    @Column(nullable = false)
    // Sexo do paciente.
    private Sexo sexo;

    // Obrigatório.
    @Column(nullable = false)
    // Endereço completo.
    private String endereco;

    // Nome da coluna no banco.
    @Column(name = "tipo_sanguineo")
    // Usa o conversor para gravar "A+" em vez de "A_POSITIVO".
    @Convert(converter = TipoSanguineoConverter.class)
    // Tipo sanguíneo (opcional).
    private TipoSanguineo tipoSanguineo;

    // TEXT permite textos longos.
    @Column(columnDefinition = "TEXT")
    // Alergias conhecidas (opcional).
    private String alergias;

    // O Hibernate preenche com a data/hora atual quando o paciente é cadastrado.
    @CreationTimestamp
    // Coluna data_criacao, que não muda depois de criada.
    @Column(name = "data_criacao", updatable = false)
    // Data de cadastro do paciente.
    private LocalDateTime dataCriacao;

    // Texto longo.
    @Column(columnDefinition = "TEXT")
    // Observações gerais (opcional).
    private String observacao;

    // Um paciente tem várias consultas. Quem guarda a chave é Consulta.paciente.
    @OneToMany(mappedBy = "paciente")
    // Evita loop infinito no toString().
    @ToString.Exclude
    // Lista de consultas do paciente.
    private List<Consulta> consultas = new ArrayList<>();
}
