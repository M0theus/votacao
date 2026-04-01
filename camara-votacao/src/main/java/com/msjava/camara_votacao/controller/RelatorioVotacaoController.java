package com.msjava.camara_votacao.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msjava.camara_votacao.business.dto.RelatorioVotacaoCompletoDTO;
import com.msjava.camara_votacao.business.services.RelatorioVotacaoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/relatorio-votacao")
@RequiredArgsConstructor
public class RelatorioVotacaoController {

    private final RelatorioVotacaoService relatorioVotacaoService;

    // ==================== ENDPOINTS JSON ====================
    
    // 1. Buscar última votação (JSON)
    @GetMapping("/ultima")
    public ResponseEntity<RelatorioVotacaoCompletoDTO> buscarUltimaVotacaoJson() {
        RelatorioVotacaoCompletoDTO relatorio = relatorioVotacaoService.buscarUltimaVotacao();
        if (relatorio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(relatorio);
    }

    // 2. Buscar votação por ID (JSON)
    @GetMapping("/{votacaoId}")
    public ResponseEntity<RelatorioVotacaoCompletoDTO> buscarVotacaoPorIdJson(@PathVariable Integer votacaoId) {
        RelatorioVotacaoCompletoDTO relatorio = relatorioVotacaoService.buscarVotacaoPorId(votacaoId);
        if (relatorio == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(relatorio);
    }

    // 3. Buscar votações por data (JSON)
    @GetMapping("/data")
    public ResponseEntity<List<RelatorioVotacaoCompletoDTO>> buscarVotacoesPorDataJson(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<RelatorioVotacaoCompletoDTO> relatorios = relatorioVotacaoService.buscarVotacoesPorData(data);
        if (relatorios.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(relatorios);
    }

    // ==================== ENDPOINTS PDF ====================
    
    // 1. Gerar PDF da última votação
    @GetMapping("/ultima/pdf")
    public ResponseEntity<byte[]> gerarPdfUltimaVotacao() {
        byte[] pdf = relatorioVotacaoService.gerarPdfUltimaVotacao();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"relatorio-ultima-votacao.pdf\"");
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    // 2. Gerar PDF por ID da votação
    @GetMapping("/{votacaoId}/pdf")
    public ResponseEntity<byte[]> gerarPdfPorId(@PathVariable Integer votacaoId) {
        byte[] pdf = relatorioVotacaoService.gerarPdfPorId(votacaoId);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                String.format("attachment; filename=\"relatorio-votacao-%d.pdf\"", votacaoId));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }

    // 3. Gerar PDF por data
    @GetMapping("/data/pdf")
    public ResponseEntity<byte[]> gerarPdfPorData(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        byte[] pdf = relatorioVotacaoService.gerarPdfPorData(data);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, 
                String.format("attachment; filename=\"relatorio-votacoes-%s.pdf\"", data));
        
        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}