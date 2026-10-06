// Pacote dos DTOs de entrada.
package com.clinica.gestao_clinica.dto.request;

// Enums usados.
import com.clinica.gestao_clinica.enums.Sexo;
import com.clinica.gestao_clinica.enums.TipoSanguineo;
// Anotações de validação.
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// Data.
import java.time.LocalDate;

// Dados para cadastrar um paciente (cria também o Usuario dele). Esta rota é pública (autocadastro).
public record PacienteRequestDTO(

        // Não pode ser vazio.
        @NotBlank(message = "Nome é obrigatório")
        // Nome completo.
        String nome,

        // Não pode ser vazio.
        @NotBlank(message = "Email é obrigatório")
        // Formato de e-mail.
        @Email(message = "Email inválido")
        // E-mail (usado no login).
        String email,

        // Não pode ser vazio.
        @NotBlank(message = "Senha é obrigatória")
        // Mínimo de 6 caracteres.
        @Size(min = 6, message = "Senha deve ter pelo menos 6 caracteres")
        // Senha.
        String senha,

        // Não pode ser vazio.
        @NotBlank(message = "Telefone é obrigatório")
        // Máximo de 15 caracteres.
        @Size(max = 15, message = "Telefone deve ter no máximo 15 caracteres")
        // Telefone.
        String telefone,

        // Não pode ser vazio.
        @NotBlank(message = "CPF é obrigatório")
        // Expressão regular: exatamente 11 dígitos (\\d = dígito, {11} = 11 vezes).
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 dígitos, sem pontos ou traço")
        // CPF.
        String cpf,

        // Não pode ser vazio.
        @NotBlank(message = "RG é obrigatório")
        // RG.
        String rg,

        // Não pode ser nulo.
        @NotNull(message = "Sexo é obrigatório")
        // Sexo (MASCULINO, FEMININO ou OUTRO).
        Sexo sexo,

        // Tipo sanguíneo (opcional).
        TipoSanguineo tipoSanguineo,

        // Alergias (opcional).
        String alergias,

        // Observação (opcional).
        String observacao,

        // Não pode ser nula.
        @NotNull(message = "Data de nascimento é obrigatória")
        // Precisa estar no passado.
        @Past(message = "Data de nascimento deve estar no passado")
        // Data de nascimento (formato JSON: "1990-05-20").
        LocalDate dataNascimento,

        // Não pode ser vazio.
        @NotBlank(message = "Endereço é obrigatório")
        // Endereço.
        String endereco

) {
}
