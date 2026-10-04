package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.enums.PerfilUsuario;
import com.raizesdonordeste.api.exception.RecursoNaoEncontradoException;
import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            BCryptPasswordEncoder passwordEncoder,
            AuditoriaService auditoriaService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    public Usuario cadastrar(Usuario usuario) {

        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        // Cadastro público sempre cria um CLIENTE.
        // O perfil enviado pelo usuário é ignorado.
        usuario.setPerfil(PerfilUsuario.CLIENTE);

        String senhaCriptografada =
                passwordEncoder.encode(usuario.getSenha());

        usuario.setSenha(senhaCriptografada);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario anonimizar(Long usuarioId) {

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Usuário não encontrado."
                        )
                );

        usuario.setNome("USUARIO_ANONIMIZADO");

        usuario.setEmail(
                "anonimo_"
                        + usuario.getId()
                        + "_"
                        + UUID.randomUUID()
                        + "@anonimizado.local"
        );

        // Invalida a senha anterior.
        usuario.setSenha(
                passwordEncoder.encode(
                        UUID.randomUUID().toString()
                )
        );

        Usuario usuarioAnonimizado =
                usuarioRepository.save(usuario);

        auditoriaService.registrar(
                "ANONIMIZAR_USUARIO",
                "USUARIO",
                usuarioAnonimizado.getId()
        );

        return usuarioAnonimizado;
    }
}