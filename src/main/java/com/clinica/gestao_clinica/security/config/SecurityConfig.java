// Pacote das configurações de segurança.
package com.clinica.gestao_clinica.security.config;

// Nossos componentes de segurança.
import com.clinica.gestao_clinica.security.filter.JwtAuthenticationFilter;
import com.clinica.gestao_clinica.security.handler.SecurityExceptionHandler;
import com.clinica.gestao_clinica.security.jwt.JwtService;
// Anotações de configuração do Spring.
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// Enum com os métodos HTTP (GET, POST...).
import org.springframework.http.HttpMethod;
// Classes de autenticação e configuração do Spring Security.
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
// Criptografia de senhas com BCrypt.
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
// Cadeia de filtros de segurança.
import org.springframework.security.web.SecurityFilterChain;
// Filtro padrão de login, usado como referência de posição para o nosso filtro JWT.
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// Indica que esta classe define beans (objetos) para o Spring.
@Configuration
// Configuração central de segurança: quem pode acessar o quê.
public class SecurityConfig {

    // Nomes dos perfis, para não repetir texto (e evitar erro de digitação).
    private static final String ADMIN = "ADMINISTRADOR";
    // Perfil de médico.
    private static final String MEDICO = "MEDICO";

    // Rotas do Swagger, que ficam públicas para facilitar os testes da API.
    private static final String[] ROTAS_SWAGGER = {
            // JSON com a especificação da API.
            "/v3/api-docs/**",
            // Página visual do Swagger.
            "/swagger-ui/**",
            // Atalho para a página do Swagger.
            "/swagger-ui.html"
    };

    // @Bean: o objeto devolvido fica disponível para o Spring usar.
    @Bean
    // Define a cadeia de filtros e as regras de acesso.
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserDetailsService userDetailsService,
                                                   SecurityExceptionHandler securityExceptionHandler) throws Exception {

        // Cria o nosso filtro JWT com as dependências de que ele precisa.
        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        // Começa a configurar.
        return http
                // Desliga o CSRF: ele protege sites com cookie de sessão; nossa API usa token no cabeçalho.
                .csrf(AbstractHttpConfigurer::disable)
                // Liga o CORS usando o bean CorsConfigurationSource (definido no CorsConfig).
                .cors(Customizer.withDefaults())
                // STATELESS: o servidor não guarda sessão; cada requisição traz seu próprio token.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Regras de autorização (lidas de cima para baixo; a primeira que bater vale).
                .authorizeHttpRequests(auth -> auth
                        // Login é público (senão ninguém conseguiria logar).
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        // Autocadastro de paciente é público.
                        .requestMatchers(HttpMethod.POST, "/pacientes").permitAll()
                        // Documentação Swagger é pública.
                        .requestMatchers(ROTAS_SWAGGER).permitAll()
                        // Rota interna de erro do Spring precisa ser liberada.
                        .requestMatchers("/error").permitAll()
                        // Gestão de usuários: só administrador.
                        .requestMatchers("/usuarios/**").hasRole(ADMIN)
                        // Relatórios: só administrador.
                        .requestMatchers("/relatorios/**").hasRole(ADMIN)
                        // Qualquer usuário logado pode CONSULTAR médicos e especialidades.
                        .requestMatchers(HttpMethod.GET, "/medicos/**", "/especialidades/**").authenticated()
                        // Criar/alterar/excluir médicos e especialidades: só administrador.
                        .requestMatchers("/medicos/**", "/especialidades/**").hasRole(ADMIN)
                        // Listar/ver pacientes: administrador ou médico.
                        .requestMatchers(HttpMethod.GET, "/pacientes/**").hasAnyRole(ADMIN, MEDICO)
                        // Alterar/excluir pacientes: só administrador.
                        .requestMatchers("/pacientes/**").hasRole(ADMIN)
                        // Marcar consulta como realizada: médico ou administrador.
                        .requestMatchers(HttpMethod.PATCH, "/consultas/*/concluir").hasAnyRole(ADMIN, MEDICO)
                        // Prontuários e receitas são dados sensíveis: médico ou administrador.
                        .requestMatchers("/prontuarios/**", "/receitas/**").hasAnyRole(ADMIN, MEDICO)
                        // Qualquer outra rota (ex.: /consultas) exige apenas estar logado.
                        .anyRequest().authenticated())
                // Define quem responde aos erros 401 e 403 (no nosso formato JSON padrão).
                .exceptionHandling(ex -> ex
                        // 401: não autenticado.
                        .authenticationEntryPoint(securityExceptionHandler)
                        // 403: sem permissão.
                        .accessDeniedHandler(securityExceptionHandler))
                // Coloca nosso filtro JWT antes do filtro padrão de login.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                // Constrói a configuração.
                .build();
    }

    // Disponibiliza o AuthenticationManager, usado pelo AuthService para verificar e-mail e senha.
    @Bean
    // O Spring monta o AuthenticationManager com o nosso UserDetailsService e o PasswordEncoder.
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        // Devolve o gerenciador já configurado.
        return configuration.getAuthenticationManager();
    }

    // Define como as senhas são criptografadas.
    @Bean
    // BCrypt é um algoritmo lento de propósito e com "sal" aleatório: o padrão recomendado para senhas.
    public PasswordEncoder passwordEncoder() {
        // Cria o codificador BCrypt.
        return new BCryptPasswordEncoder();
    }
}
