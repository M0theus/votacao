package com.msjava.camara_votacao.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.msjava.camara_votacao.business.dto.*;
import com.msjava.camara_votacao.business.services.UsuarioVotacaoService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/votacao")
@RequiredArgsConstructor
public class UsuarioVotacaoController {
    private final UsuarioVotacaoService usuarioVotacaoService;
    
    @PostMapping("/votar")
    public ResponseEntity<UsuarioVotacaoResponseDTO> votar(
            @RequestBody UsuarioVotacaoRequestDTO request) {  // Apenas @RequestBody
        return ResponseEntity.ok(usuarioVotacaoService.registrarVoto(request));
    }
    
    @PostMapping("/ausente/{usuarioId}")
    public ResponseEntity<UsuarioVotacaoResponseDTO> marcarAusente(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(usuarioVotacaoService.marcarAusente(usuarioId));
    }
    
    @GetMapping("/resultado")
    public ResponseEntity<ResultadoVotacaoDTO> obterResultado() {
        return ResponseEntity.ok(usuarioVotacaoService.obterResultado());
    }
    
    @GetMapping("/votos")
    public ResponseEntity<List<UsuarioVotacaoResponseDTO>> listarVotos() {
        return ResponseEntity.ok(usuarioVotacaoService.listarVotos());
    }

}