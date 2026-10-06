// Pacote das entidades.
package com.clinica.gestao_clinica.entity;

// Anotações do JPA.
import jakarta.persistence.*;
// Anotações do Lombok (o "*" importa todas).
import lombok.*;

// Classes de lista do Java.
import java.util.ArrayList;
import java.util.List;

// Esta classe é uma tabela.
@Entity
// Nome da tabela.
@Table(name = "medico")
// Gera os getters.
@Getter
// Gera os setters.
@Setter
// Gera o construtor vazio.
@NoArgsConstructor
// Gera o construtor completo.
@AllArgsConstructor
// O médico guarda só os dados profissionais. Nome, e-mail e senha ficam no Usuario ligado a ele.
public class Medico {

    // Chave primária.
    @Id
    // Gerada pelo banco.
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Identificador do médico.
    private Long id;

    // Relação 1 para 1: cada médico tem exatamente um usuário (e vice-versa).
    @OneToOne
    // A coluna usuario_id guarda o id do usuário (chave estrangeira). unique impede dois médicos no mesmo usuário.
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    // Dados de login e contato do médico.
    private Usuario usuario;

    // CRM é obrigatório e único.
    @Column(nullable = false, unique = true)
    // Registro no Conselho Regional de Medicina.
    private String crm;

    // Relação muitos para 1: muitos médicos podem ter a mesma especialidade.
    @ManyToOne
    // A coluna especialidade_id guarda o id da especialidade.
    @JoinColumn(name = "especialidade_id", nullable = false)
    // Especialidade do médico.
    private Especialidade especialidade;

    // Relação 1 para muitos: um médico tem várias consultas. "mappedBy" diz que quem guarda a chave é Consulta.medico.
    @OneToMany(mappedBy = "medico")
    // Evita loop infinito caso alguém gere toString() (médico -> consultas -> médico -> ...).
    @ToString.Exclude
    // Lista de consultas do médico (carregada só quando acessada: LAZY).
    private List<Consulta> consultas = new ArrayList<>();

}
