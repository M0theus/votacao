package com.msjava.camara_votacao.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msjava.camara_votacao.business.dto.VotacaoDetalhadaDTO;
import com.msjava.camara_votacao.business.services.VotacaoDetalhadaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/votacao/detalhada")
@RequiredArgsConstructor
public class VotacaoDetalhadaController {

    private final VotacaoDetalhadaService votacaoDetalhadaService;

    @GetMapping
    public ResponseEntity<?> buscarVotacaoPorData(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        
        VotacaoDetalhadaDTO resultado;
        
        if (data != null) {
            resultado = votacaoDetalhadaService.buscarVotacaoPorData(data);
        } else {
            resultado = votacaoDetalhadaService.buscarUltimaVotacao();
        }
        
        if (resultado == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(resultado);
    }
}
