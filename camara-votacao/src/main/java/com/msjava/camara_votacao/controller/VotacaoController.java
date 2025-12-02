package com.msjava.camara_votacao.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.msjava.camara_votacao.business.dto.VotacaoRequestDTO;
import com.msjava.camara_votacao.business.dto.VotacaoResponseDTO;
import com.msjava.camara_votacao.business.services.VotacaoService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/votacoes")
@RequiredArgsConstructor
public class VotacaoController {
    private final VotacaoService votacaoService;
    
    @PostMapping("/criar")
    public ResponseEntity<VotacaoResponseDTO> criarVotacao(@RequestBody VotacaoRequestDTO request) {
        return ResponseEntity.ok(votacaoService.criarVotacao(request.getUsuarioId()));
    }
    
    @PostMapping("/encerrar")
    public ResponseEntity<VotacaoResponseDTO> encerrarVotacao(
            @RequestBody VotacaoRequestDTO request) {
        return ResponseEntity.ok(votacaoService.encerrarVotacao(request.getUsuarioId()));
    }
    
    @GetMapping
    public ResponseEntity<List<VotacaoResponseDTO>> listarTodas() {
        return ResponseEntity.ok(votacaoService.listarTodasVotacoes());
    }
    
    @GetMapping("/data")
    public ResponseEntity<List<VotacaoResponseDTO>> buscarPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime data) {
        return ResponseEntity.ok(votacaoService.buscarPorData(data));
    }
}