package com.msjava.camara_votacao.business.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.msjava.camara_votacao.business.dto.RelatorioVotacaoCompletoDTO;
import com.msjava.camara_votacao.business.dto.VotoUsuarioDTO;
import com.msjava.camara_votacao.infrastructure.entitys.Usuario;
import com.msjava.camara_votacao.infrastructure.entitys.UsuarioVotacao;
import com.msjava.camara_votacao.infrastructure.entitys.Votacao;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioRepository;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioVotacaoRepository;
import com.msjava.camara_votacao.infrastructure.repository.VotacaoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RelatorioVotacaoService {

    private final VotacaoRepository votacaoRepository;
    private final UsuarioVotacaoRepository usuarioVotacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PdfGeneratorService pdfGeneratorService;

    // 1. Buscar última votação
    public RelatorioVotacaoCompletoDTO buscarUltimaVotacao() {
        log.info("Buscando última votação");
        
        Votacao votacao = votacaoRepository.findTopByOrderByIdDesc();
        if (votacao == null) {
            log.warn("Nenhuma votação encontrada");
            return null;
        }
        
        return montarRelatorio(votacao);
    }

    // 2. Buscar votação por ID específico
    public RelatorioVotacaoCompletoDTO buscarVotacaoPorId(Integer votacaoId) {
        log.info("Buscando votação ID: {}", votacaoId);
        
        Votacao votacao = votacaoRepository.findById(votacaoId).orElse(null);
        if (votacao == null) {
            log.warn("Votação não encontrada com ID: {}", votacaoId);
            return null;
        }
        
        return montarRelatorio(votacao);
    }

    // 3. Buscar todas as votações de um dia específico
    public List<RelatorioVotacaoCompletoDTO> buscarVotacoesPorData(LocalDate data) {
        log.info("Buscando votações do dia: {}", data);
        
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.atTime(LocalTime.MAX);
        
        List<Votacao> votoes = votacaoRepository.findByDataCriacaoBetween(inicioDoDia, fimDoDia);
        
        if (votoes.isEmpty()) {
            log.warn("Nenhuma votação encontrada para a data: {}", data);
            return List.of();
        }
        
        return votoes.stream()
                .map(this::montarRelatorio)
                .collect(Collectors.toList());
    }

    // Método auxiliar para montar o relatório completo
    private RelatorioVotacaoCompletoDTO montarRelatorio(Votacao votacao) {
        // Busca todos os votos da votação
        List<UsuarioVotacao> votos = usuarioVotacaoRepository.findByVotacaoId(votacao.getId());
        
        // Busca todos os usuários cadastrados
        List<Usuario> todosUsuarios = usuarioRepository.findAll();
        
        // Converte para DTO
        List<VotoUsuarioDTO> votosDTO = votos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
        
        // Conta os votos por tipo
        Map<String, Integer> contagemVotos = contarVotos(votos);
        
        // Calcula percentuais
        Map<String, Double> percentualVotos = calcularPercentuais(contagemVotos, votos.size());
        
        // Monta o resultado
        return RelatorioVotacaoCompletoDTO.builder()
                .votacaoId(votacao.getId())
                .dataCriacao(votacao.getDataCriacao())
                .dataEncerramento(votacao.getDataEncerramento())
                .votacaoAtiva(votacao.getVotacaoAtiva())
                .totalVotos(votos.size())
                .totalUsuarios(todosUsuarios.size())
                .contagemVotos(contagemVotos)
                .percentualVotos(percentualVotos)
                .votos(votosDTO)
                .dataGeracao(LocalDateTime.now())
                .build();
    }

    private VotoUsuarioDTO converterParaDTO(UsuarioVotacao usuarioVotacao) {
        return VotoUsuarioDTO.builder()
                .usuarioId(usuarioVotacao.getUsuario().getId())
                .usuarioNome(usuarioVotacao.getUsuario().getNome())
                .usuarioPartido(usuarioVotacao.getUsuario().getPartido())
                .voto(usuarioVotacao.getVoto())
                .dataVoto(usuarioVotacao.getDataVoto())
                .build();
    }

    private Map<String, Integer> contarVotos(List<UsuarioVotacao> votos) {
        Map<String, Integer> contagem = new HashMap<>();
        contagem.put("SIM", 0);
        contagem.put("NAO", 0);
        contagem.put("ABSTENCAO", 0);
        contagem.put("AUSENTE", 0);

        for (UsuarioVotacao voto : votos) {
            String tipo = voto.getVoto().name();
            contagem.put(tipo, contagem.getOrDefault(tipo, 0) + 1);
        }

        return contagem;
    }

    private Map<String, Double> calcularPercentuais(Map<String, Integer> contagem, int totalVotos) {
        Map<String, Double> percentuais = new HashMap<>();
        
        if (totalVotos == 0) {
            percentuais.put("SIM", 0.0);
            percentuais.put("NAO", 0.0);
            percentuais.put("ABSTENCAO", 0.0);
            percentuais.put("AUSENTE", 0.0);
            return percentuais;
        }
        
        for (Map.Entry<String, Integer> entry : contagem.entrySet()) {
            double percentual = (entry.getValue() * 100.0) / totalVotos;
            percentuais.put(entry.getKey(), Math.round(percentual * 10) / 10.0); // 1 casa decimal
        }
        
        return percentuais;
    }

    // Métodos para gerar PDF
    public byte[] gerarPdfUltimaVotacao() {
        RelatorioVotacaoCompletoDTO relatorio = buscarUltimaVotacao();
        if (relatorio == null) {
            throw new RuntimeException("Nenhuma votação encontrada");
        }
        return pdfGeneratorService.gerarPdfCompleto(relatorio);
    }

    public byte[] gerarPdfPorId(Integer votacaoId) {
        RelatorioVotacaoCompletoDTO relatorio = buscarVotacaoPorId(votacaoId);
        if (relatorio == null) {
            throw new RuntimeException("Votação não encontrada com ID: " + votacaoId);
        }
        return pdfGeneratorService.gerarPdfCompleto(relatorio);
    }

    public byte[] gerarPdfPorData(LocalDate data) {
        List<RelatorioVotacaoCompletoDTO> relatorios = buscarVotacoesPorData(data);
        if (relatorios.isEmpty()) {
            throw new RuntimeException("Nenhuma votação encontrada para a data: " + data);
        }
        return pdfGeneratorService.gerarPdfMultiplo(relatorios, data);
    }
}
