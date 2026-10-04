package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.Fidelidade;
import com.raizesdonordeste.api.service.FidelidadeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fidelidade")
public class FidelidadeController {

    private final FidelidadeService fidelidadeService;

    public FidelidadeController(FidelidadeService fidelidadeService) {
        this.fidelidadeService = fidelidadeService;
    }

    @PostMapping("/usuario/{usuarioId}/aderir")
    public ResponseEntity<Fidelidade> aderir(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                fidelidadeService.aderir(usuarioId)
        );
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Fidelidade> consultar(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                fidelidadeService.consultar(usuarioId)
        );
    }

    @PatchMapping("/usuario/{usuarioId}/pontos/adicionar")
    public ResponseEntity<Fidelidade> adicionarPontos(
            @PathVariable Long usuarioId,
            @RequestParam Integer pontos
    ) {
        return ResponseEntity.ok(
                fidelidadeService.adicionarPontos(usuarioId, pontos)
        );
    }

    @PatchMapping("/usuario/{usuarioId}/pontos/resgatar")
    public ResponseEntity<Fidelidade> resgatarPontos(
            @PathVariable Long usuarioId,
            @RequestParam Integer pontos
    ) {
        return ResponseEntity.ok(
                fidelidadeService.resgatarPontos(usuarioId, pontos)
        );
    }

    @PatchMapping("/usuario/{usuarioId}/consentimento/cancelar")
    public ResponseEntity<Fidelidade> cancelarConsentimento(
            @PathVariable Long usuarioId
    ) {
        return ResponseEntity.ok(
                fidelidadeService.cancelarConsentimento(usuarioId)
        );
    }
}
