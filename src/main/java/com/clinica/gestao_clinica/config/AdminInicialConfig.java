// Pacote das configurações gerais.
package com.clinica.gestao_clinica.config;

// Entidade, enum e repositório de usuário.
import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
// Lombok: cria o campo "log".
import lombok.extern.slf4j.Slf4j;
// Lê valores do application.properties.
import org.springframework.beans.factory.annotation.Value;
// Interface de código que roda logo depois que a aplicação sobe.
import org.springframework.boot.ApplicationRunner;
// Anotações de configuração.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Criptografia de senha.
import org.springframework.security.crypto.password.PasswordEncoder;

// Cria o "log".
@Slf4j
// Esta classe define beans.
@Configuration
// Problema: só administradores criam usuários, mas quem cria o PRIMEIRO administrador?
// Solução: ao iniciar, se não existir nenhum administrador, criamos um com os dados do application.properties.
public class AdminInicialConfig {

    // Bean do tipo ApplicationRunner: o Spring executa o método run() quando a aplicação termina de subir.
    @Bean
    // Recebe o repositório, o codificador de senha e os dados do admin (com valores padrão após os ":").
    public ApplicationRunner criarAdministradorInicial(UsuarioRepository usuarioRepository,
                                                       PasswordEncoder passwordEncoder,
                                                       @Value("${app.admin.email:admin@clinica.com}") String email,
                                                       @Value("${app.admin.senha:admin123}") String senha) {
        // Lambda: o código abaixo é o corpo do método run().
        return args -> {
            // Se já existe algum administrador, não faz nada.
            if (usuarioRepository.existsByTipoUsuario(TipoUsuario.ADMINISTRADOR)) {
                // Sai do método.
                return;
            }

            // Cria um usuário novo.
            Usuario admin = new Usuario();
            // Nome fixo.
            admin.setNome("Administrador");
            // E-mail configurado.
            admin.setEmail(email);
            // Senha criptografada com BCrypt.
            admin.setSenha(passwordEncoder.encode(senha));
            // Telefone fictício (o campo é obrigatório).
            admin.setTelefone("00000000000");
            // Perfil de administrador.
            admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            // Usuário ativo.
            admin.setAtivo(true);
            // Salva no banco.
            usuarioRepository.save(admin);

            // Avisa no log, lembrando de trocar a senha padrão.
            log.warn("Administrador inicial criado com e-mail '{}'. Troque a senha padrão em produção!", email);
        };
    }
}
