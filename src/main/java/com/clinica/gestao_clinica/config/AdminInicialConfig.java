package com.clinica.gestao_clinica.config;

import com.clinica.gestao_clinica.entity.Usuario;
import com.clinica.gestao_clinica.enums.TipoUsuario;
import com.clinica.gestao_clinica.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
public class AdminInicialConfig {

    @Bean
    public ApplicationRunner criarAdministradorInicial(UsuarioRepository usuarioRepository,
                                                       PasswordEncoder passwordEncoder,
                                                       @Value("${app.admin.email:admin@clinica.com}") String email,
                                                       @Value("${app.admin.senha:admin123}") String senha) {
        return args -> {
            if (usuarioRepository.existsByTipoUsuario(TipoUsuario.ADMINISTRADOR)) {
                return;
            }

            Usuario admin = new Usuario();
            admin.setNome("Administrador");
            admin.setEmail(email);
            admin.setSenha(passwordEncoder.encode(senha));
            admin.setTelefone("00000000000");
            admin.setTipoUsuario(TipoUsuario.ADMINISTRADOR);
            admin.setAtivo(true);
            usuarioRepository.save(admin);

            log.warn("Administrador inicial criado com e-mail '{}'. Troque a senha padrão em produção!", email);
        };
    }
}
