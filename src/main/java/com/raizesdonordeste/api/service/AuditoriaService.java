package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.model.Auditoria;
import com.raizesdonordeste.api.repository.AuditoriaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(
            String acao,
            String recurso,
            Long recursoId
    ) {
        Auditoria auditoria = new Auditoria();

        auditoria.setAcao(acao);
        auditoria.setRecurso(recurso);
        auditoria.setRecursoId(recursoId);
        auditoria.setUsuario(obterUsuarioAutenticado());

        auditoriaRepository.save(auditoria);
    }

    public List<Auditoria> listar() {
        return auditoriaRepository.findAllByOrderByDataHoraDesc();
    }

    public List<Auditoria> listarPorRecurso(String recurso) {
        return auditoriaRepository
                .findByRecursoOrderByDataHoraDesc(recurso);
    }

    private String obterUsuarioAutenticado() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            return "SISTEMA";
        }

        return authentication.getName();
    }
}
