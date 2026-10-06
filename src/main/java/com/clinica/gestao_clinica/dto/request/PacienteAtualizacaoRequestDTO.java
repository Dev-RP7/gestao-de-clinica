// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Enums usados.
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
// Anotações de validação.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

// Data.
import java.time.LocalDate;

// Dados para atualizar um paciente. Todos opcionais: só os campos preenchidos são alterados.
public record PacienteAtualizacaoRequestDTO(

        // Novo nome.
        String nome,

        // Se vier, precisa ter formato de e-mail.
        @Email(message = "Email inválido")
        // Novo e-mail.
        String email,

        // Máximo de 15 caracteres.
        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        // Novo telefone.
        String telefone,

        // Se vier, precisa ter pelo menos 6 caracteres.
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        // Nova senha.
        String senha,

        // Novo endereço.
        String endereco,

        // Se vier, precisa ser uma data no passado.
        @Past(message = "Data de nascimento deve estar no passado")
        // Nova data de nascimento.
        LocalDate dataNascimento,

        // Novo sexo.
        Sexo sexo,

        // Novo tipo sanguíneo.
        TipoSanguineo tipoSanguineo,

        // Novas alergias.
        String alergias,

        // Nova observação.
        String observacao

) {
}
