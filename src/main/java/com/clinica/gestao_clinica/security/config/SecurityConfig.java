package com.clinica.gestao_clinica.security.config;

import com.clinica.gestao_clinica.security.filter.JwtAuthenticationFilter;
import com.clinica.gestao_clinica.security.handler.SecurityExceptionHandler;
import com.clinica.gestao_clinica.security.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final String ADMIN = "ADMINISTRADOR";
    private static final String MEDICO = "MEDICO";

    private static final String[] ROTAS_SWAGGER = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtService jwtService,
                                                   UserDetailsService userDetailsService,
                                                   SecurityExceptionHandler securityExceptionHandler) throws Exception {

        JwtAuthenticationFilter jwtFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);

        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/pacientes").permitAll()
                        .requestMatchers(ROTAS_SWAGGER).permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/usuarios/**").hasRole(ADMIN)
                        .requestMatchers("/relatorios/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/medicos/**", "/especialidades/**").authenticated()
                        .requestMatchers("/medicos/**", "/especialidades/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/pacientes/**").hasAnyRole(ADMIN, MEDICO)
                        .requestMatchers("/pacientes/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/consultas/*/concluir").hasAnyRole(ADMIN, MEDICO)
                        .requestMatchers("/prontuarios/**", "/receitas/**").hasAnyRole(ADMIN, MEDICO)
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(securityExceptionHandler)
                        .accessDeniedHandler(securityExceptionHandler))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
