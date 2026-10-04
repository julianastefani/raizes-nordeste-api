package com.raizesdonordeste.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception

                        // Usuário NÃO autenticado -> 401
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json;charset=UTF-8");

                            response.getWriter().write(
                                    "{\"status\":401," +
                                            "\"erro\":\"Unauthorized\"," +
                                            "\"mensagem\":\"Não autenticado.\"," +
                                            "\"path\":\"" + request.getRequestURI() + "\"}"
                            );
                        })

                        // Usuário autenticado, mas SEM permissão -> 403
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json;charset=UTF-8");

                            response.getWriter().write(
                                    "{\"status\":403," +
                                            "\"erro\":\"Forbidden\"," +
                                            "\"mensagem\":\"Acesso negado. Você não possui permissão para acessar este recurso.\"," +
                                            "\"path\":\"" + request.getRequestURI() + "\"}"
                            );
                        })
                )

                .authorizeHttpRequests(auth -> auth

                        // Login público
                        .requestMatchers("/auth/**").permitAll()

                        // Cadastro de usuário público
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        // Swagger público
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // Criar produto:
                        // somente GERENTE ou ADMIN
                        .requestMatchers(
                                HttpMethod.POST,
                                "/produtos"
                        ).hasAnyRole("GERENTE", "ADMIN")

                        // Todos os demais endpoints exigem autenticação
                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}