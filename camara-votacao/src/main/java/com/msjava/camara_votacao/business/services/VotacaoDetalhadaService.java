package com.msjava.camara_votacao.business.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.msjava.camara_votacao.business.dto.VotacaoDetalhadaDTO;
import com.msjava.camara_votacao.business.dto.VotoUsuarioDTO;
import com.msjava.camara_votacao.infrastructure.entitys.UsuarioVotacao;
import com.msjava.camara_votacao.infrastructure.entitys.Votacao;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioVotacaoRepository;
import com.msjava.camara_votacao.infrastructure.repository.VotacaoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VotacaoDetalhadaService {
    private final VotacaoRepository votacaoRepository;
    private final UsuarioVotacaoRepository usuarioVotacaoRepository;

    public VotacaoDetalhadaDTO buscarVotacaoPorData(LocalDate data) {
        log.info("Buscando votação do dia: {}", data);

        // Define o início e fim do dia
        LocalDateTime inicioDoDia = data.atStartOfDay();
        LocalDateTime fimDoDia = data.atTime(LocalTime.MAX);

        // Busca votação que foi criada ou ocorreu nesse dia
        Votacao votacao = votacaoRepository.findByDataCriacaoBetween(inicioDoDia, fimDoDia)
                .stream()
                .findFirst()
                .orElse(null);

        if (votacao == null) {
            log.warn("Nenhuma votação encontrada para a data: {}", data);
            return null;
        }

        // Busca todos os votos dessa votação
        List<UsuarioVotacao> votos = usuarioVotacaoRepository.findByVotacaoId(votacao.getId());

        // Converte para DTO
        List<VotoUsuarioDTO> votosDTO = votos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

        // Conta os votos por tipo
        Map<String, Integer> contagemVotos = contarVotos(votos);

        // Monta o resultado
        return VotacaoDetalhadaDTO.builder()
                .votacaoId(votacao.getId())
                .dataCriacao(votacao.getDataCriacao())
                .dataEncerramento(votacao.getDataEncerramento())
                .votacaoAtiva(votacao.getVotacaoAtiva())
                .totalVotos(votos.size())
                .contagemVotos(contagemVotos)
                .votos(votosDTO)
                .build();
    }

    public VotacaoDetalhadaDTO buscarUltimaVotacao() {
        log.info("Buscando última votação");

        Votacao votacao = votacaoRepository.findTopByOrderByIdDesc();
        
        if (votacao == null) {
            log.warn("Nenhuma votação encontrada");
            return null;
        }

        List<UsuarioVotacao> votos = usuarioVotacaoRepository.findByVotacaoId(votacao.getId());

        List<VotoUsuarioDTO> votosDTO = votos.stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());

        Map<String, Integer> contagemVotos = contarVotos(votos);

        return VotacaoDetalhadaDTO.builder()
                .votacaoId(votacao.getId())
                .dataCriacao(votacao.getDataCriacao())
                .dataEncerramento(votacao.getDataEncerramento())
                .votacaoAtiva(votacao.getVotacaoAtiva())
                .totalVotos(votos.size())
                .contagemVotos(contagemVotos)
                .votos(votosDTO)
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
}
