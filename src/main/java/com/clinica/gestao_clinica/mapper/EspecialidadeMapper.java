// Pacote dos mappers.
package com.clinica.gestao_clinica.mapper;

// DTOs e entidade.
import com.clinica.gestao_clinica.dto.request.EspecialidadeCadastroRequestDTO;
import com.clinica.gestao_clinica.dto.response.EspecialidadeResponseDTO;
import com.clinica.gestao_clinica.entity.Especialidade;

// Conversões de Especialidade.
public class EspecialidadeMapper {

    // Impede instanciar a classe.
    private EspecialidadeMapper() {
    }

    // Cria a entidade a partir do DTO de cadastro (antes recebia o DTO de resposta, por engano).
    public static Especialidade toEntity(EspecialidadeCadastroRequestDTO dto) {
        // Cria a especialidade vazia.
        Especialidade especialidade = new Especialidade();
        // Copia o nome, tirando espaços extras do começo e do fim.
        especialidade.setNome(dto.nome().trim());
        // Devolve a entidade.
        return especialidade;
    }

    // Converte a entidade em DTO de resposta.
    public static EspecialidadeResponseDTO toResponse(Especialidade especialidade) {
        // Cria o record com id e nome.
        return new EspecialidadeResponseDTO(
                // Id.
                especialidade.getId(),
                // Nome.
                especialidade.getNome()
        );
    }
}
