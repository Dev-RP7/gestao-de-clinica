// Pacote dos serviços de segurança.
package com.clinica.gestao_clinica.security.service;

// Repositório para buscar o usuário no banco.
import com.clinica.gestao_clinica.repository.UsuarioRepository;
// Interfaces e exceção do Spring Security.
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// Marca a classe como serviço do Spring.
import org.springframework.stereotype.Service;

// O Spring cria e gerencia esta classe.
@Service
// UserDetailsService é a interface que o Spring Security usa para "buscar o usuário pelo login".
public class CustomUserDetailsService implements UserDetailsService {

    // Repositório de usuários.
    private final UsuarioRepository usuarioRepository;

    // O Spring injeta o repositório pelo construtor.
    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        // Guarda o repositório no campo.
        this.usuarioRepository = usuarioRepository;
    }

    // Implementa o método da interface.
    @Override
    // Chamado pelo Spring no login (e pelo nosso filtro JWT) com o e-mail digitado.
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Busca o usuário pelo e-mail. Como Usuario implementa UserDetails, podemos devolvê-lo direto.
        return usuarioRepository.findByEmail(email)
                // Se não existir, lança a exceção que o Spring espera (vira "E-mail ou senha inválidos").
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }
}
