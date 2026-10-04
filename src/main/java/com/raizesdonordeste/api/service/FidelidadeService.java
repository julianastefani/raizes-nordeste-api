package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.exception.RecursoNaoEncontradoException;
import com.raizesdonordeste.api.model.Fidelidade;
import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.repository.FidelidadeRepository;
import com.raizesdonordeste.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FidelidadeService {

    private final FidelidadeRepository fidelidadeRepository;
    private final UsuarioRepository usuarioRepository;

    public FidelidadeService(
            FidelidadeRepository fidelidadeRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.fidelidadeRepository = fidelidadeRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Fidelidade aderir(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Usuário não encontrado.")
                );

        Fidelidade fidelidade = fidelidadeRepository
                .findByUsuarioId(usuarioId)
                .orElseGet(() -> {
                    Fidelidade nova = new Fidelidade();
                    nova.setUsuario(usuario);
                    nova.setPontos(0);
                    return nova;
                });

        fidelidade.setConsentimento(true);

        return fidelidadeRepository.save(fidelidade);
    }

    public Fidelidade consultar(Long usuarioId) {
        return fidelidadeRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Programa de fidelidade não encontrado para este usuário."
                        )
                );
    }

    @Transactional
    public Fidelidade adicionarPontos(Long usuarioId, Integer pontos) {

        if (pontos == null || pontos <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero."
            );
        }

        Fidelidade fidelidade = consultar(usuarioId);

        validarConsentimento(fidelidade);

        fidelidade.setPontos(fidelidade.getPontos() + pontos);

        return fidelidadeRepository.save(fidelidade);
    }

    @Transactional
    public Fidelidade resgatarPontos(Long usuarioId, Integer pontos) {

        if (pontos == null || pontos <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade de pontos deve ser maior que zero."
            );
        }

        Fidelidade fidelidade = consultar(usuarioId);

        validarConsentimento(fidelidade);

        if (fidelidade.getPontos() < pontos) {
            throw new IllegalArgumentException(
                    "Saldo de pontos insuficiente."
            );
        }

        fidelidade.setPontos(fidelidade.getPontos() - pontos);

        return fidelidadeRepository.save(fidelidade);
    }

    @Transactional
    public Fidelidade cancelarConsentimento(Long usuarioId) {

        Fidelidade fidelidade = consultar(usuarioId);

        fidelidade.setConsentimento(false);

        return fidelidadeRepository.save(fidelidade);
    }

    private void validarConsentimento(Fidelidade fidelidade) {
        if (!Boolean.TRUE.equals(fidelidade.getConsentimento())) {
            throw new IllegalArgumentException(
                    "O usuário não possui consentimento ativo para o programa de fidelidade."
            );
        }
    }
}