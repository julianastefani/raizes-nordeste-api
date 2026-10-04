package com.raizesdonordeste.api.config;

import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.repository.UsuarioRepository;
import com.raizesdonordeste.api.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UsuarioRepository usuarioRepository
    ) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");

        // Não existe token
        if (authorizationHeader == null
                || !authorizationHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7);

        // TESTE TEMPORÁRIO
        System.out.println("=== TESTE JWT ===");
        System.out.println("TOKEN RECEBIDO: SIM");

        boolean tokenValido = jwtService.tokenValido(token);

        System.out.println("TOKEN VALIDO: " + tokenValido);

        if (!tokenValido) {
            System.out.println("JWT REJEITADO PELO JwtService");
            System.out.println("=================");
            filterChain.doFilter(request, response);
            return;
        }

        String email = jwtService.extrairEmail(token);

        System.out.println("EMAIL DO TOKEN: " + email);

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElse(null);

        if (usuario == null) {
            System.out.println("USUARIO NAO ENCONTRADO");
        } else {
            System.out.println("USUARIO ENCONTRADO: " + usuario.getEmail());
            System.out.println("PERFIL: " + usuario.getPerfil());
        }

        if (usuario != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            SimpleGrantedAuthority autoridade =
                    new SimpleGrantedAuthority(
                            "ROLE_" + usuario.getPerfil().name()
                    );

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            usuario.getEmail(),
                            null,
                            List.of(autoridade)
                    );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authentication);

            System.out.println("AUTENTICACAO CRIADA");
            System.out.println("AUTORIDADE: " + autoridade.getAuthority());
        }

        System.out.println("=================");

        filterChain.doFilter(request, response);
    }
}