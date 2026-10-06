// Pacote dos mappers.
package com.clinica.gestao_clinica.mapper;

// DTO e entidades.
import com.clinica.gestao_clinica.dto.response.MedicoResponseDTO;
import com.clinica.gestao_clinica.entity.Medico;
import com.clinica.gestao_clinica.entity.Usuario;

// Conversões de Medico. Antes, o mesmo "new MedicoResponseDTO(...)" estava repetido em vários métodos do service.
public class MedicoMapper {

    // Impede instanciar a classe.
    private MedicoMapper() {
    }

    // Converte a entidade Medico (e o Usuario ligado a ela) em DTO de resposta.
    public static MedicoResponseDTO toResponse(Medico medico) {
        // Guarda o usuário numa variável para deixar o código mais curto.
        Usuario usuario = medico.getUsuario();
        // Monta o record.
        return new MedicoResponseDTO(
                // Id do médico.
                medico.getId(),
                // Nome (do usuário).
                usuario.getNome(),
                // E-mail (do usuário).
                usuario.getEmail(),
                // Telefone (do usuário).
                usuario.getTelefone(),
                // CRM.
                medico.getCrm(),
                // Nome da especialidade.
                medico.getEspecialidade().getNome()
        );
    }
}
