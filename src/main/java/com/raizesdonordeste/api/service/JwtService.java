package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String CHAVE_SECRETA =
            "raizes-do-nordeste-chave-jwt-secreta-2026-seguranca-api";

    private static final long TEMPO_EXPIRACAO = 1000 * 60 * 60; // 1 hora

    private SecretKey getChave() {
        return Keys.hmacShaKeyFor(
                CHAVE_SECRETA.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String gerarToken(Usuario usuario) {

        Date agora = new Date();
        Date expiracao = new Date(
                agora.getTime() + TEMPO_EXPIRACAO
        );

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("perfil", usuario.getPerfil().name())
                .claim("usuarioId", usuario.getId())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChave())
                .compact();
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getChave())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public String extrairPerfil(String token) {
        return extrairClaims(token).get("perfil", String.class);
    }

    public Long extrairUsuarioId(String token) {
        Number usuarioId = extrairClaims(token)
                .get("usuarioId", Number.class);

        return usuarioId.longValue();
    }

    public boolean tokenValido(String token) {
        try {
            Claims claims = extrairClaims(token);

            return claims.getExpiration() != null
                    && claims.getExpiration().after(new Date());

        } catch (Exception e) {
            return false;
        }
    }
}
