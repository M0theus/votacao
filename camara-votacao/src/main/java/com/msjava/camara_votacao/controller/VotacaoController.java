package com.msjava.camara_votacao.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msjava.camara_votacao.business.dto.ResultadoVotacaoDTO;
import com.msjava.camara_votacao.business.dto.VotacaoRequestDTO;
import com.msjava.camara_votacao.business.dto.VotacaoResponseDTO;
import com.msjava.camara_votacao.business.services.VotacaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/votacao")
@RequiredArgsConstructor
public class VotacaoController {
    private final VotacaoService votacaoService;

    @PostMapping("/votar")
    public ResponseEntity<VotacaoResponseDTO> votar(@RequestBody VotacaoRequestDTO request) {
        VotacaoResponseDTO response = votacaoService.registrarVoto(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/ausente/{usuarioId}")
    public ResponseEntity<VotacaoResponseDTO> marcarAusente(@PathVariable Integer usuarioId) {
        VotacaoResponseDTO response = votacaoService.marcarAusente(usuarioId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/resultado")
    public ResponseEntity<ResultadoVotacaoDTO> obterResultado() {
        ResultadoVotacaoDTO resultado = votacaoService.obterResultado();
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/votos")
    public ResponseEntity<List<VotacaoResponseDTO>> listarVotos() {
        List<VotacaoResponseDTO> votos = votacaoService.listarVotos();
        return ResponseEntity.ok(votos);
    }

    @PostMapping("/finalizar")
    public ResponseEntity<String> finalizarVotacao() {
        votacaoService.finalizarVotacao();
        return ResponseEntity.ok("Votação finalizada");
    }

    @PostMapping("/zerar")
    public ResponseEntity<String> zerarVotacao() {
        votacaoService.zerarVotacao();
        return ResponseEntity.ok("Votação zerada");
    }

}
