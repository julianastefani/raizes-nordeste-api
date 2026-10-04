package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.exception.RecursoNaoEncontradoException;
import com.raizesdonordeste.api.model.Promocao;
import com.raizesdonordeste.api.repository.PromocaoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PromocaoService {

    private final PromocaoRepository promocaoRepository;

    public PromocaoService(PromocaoRepository promocaoRepository) {
        this.promocaoRepository = promocaoRepository;
    }

    public Promocao criar(Promocao promocao) {

        if (!promocao.getDataFim().isAfter(promocao.getDataInicio())) {
            throw new IllegalArgumentException(
                    "A data final deve ser posterior à data inicial."
            );
        }

        promocao.setAtiva(true);

        return promocaoRepository.save(promocao);
    }

    public List<Promocao> listar() {
        return promocaoRepository.findAll();
    }

    public Promocao buscarPorId(Long id) {
        return promocaoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Promoção não encontrada."
                        )
                );
    }

    public List<Promocao> listarAtivas() {
        LocalDateTime agora = LocalDateTime.now();

        return promocaoRepository.findByAtivaTrue()
                .stream()
                .filter(promocao ->
                        !agora.isBefore(promocao.getDataInicio())
                                && !agora.isAfter(promocao.getDataFim())
                )
                .toList();
    }

    public Promocao alterarStatus(Long id, Boolean ativa) {

        Promocao promocao = buscarPorId(id);

        promocao.setAtiva(ativa);

        return promocaoRepository.save(promocao);
    }
}