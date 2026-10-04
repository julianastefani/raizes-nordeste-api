package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.Auditoria;
import com.raizesdonordeste.api.service.AuditoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auditoria")
public class AuditoriaController {

    private final AuditoriaService auditoriaService;

    public AuditoriaController(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @GetMapping
    public ResponseEntity<List<Auditoria>> listar() {
        return ResponseEntity.ok(
                auditoriaService.listar()
        );
    }

    @GetMapping("/recurso/{recurso}")
    public ResponseEntity<List<Auditoria>> listarPorRecurso(
            @PathVariable String recurso
    ) {
        return ResponseEntity.ok(
                auditoriaService.listarPorRecurso(recurso)
        );
    }
}