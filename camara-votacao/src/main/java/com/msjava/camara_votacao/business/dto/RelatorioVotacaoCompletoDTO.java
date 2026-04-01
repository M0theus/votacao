package com.msjava.camara_votacao.business.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioVotacaoCompletoDTO {
    private Integer votacaoId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEncerramento;
    private Boolean votacaoAtiva;
    private Integer totalVotos;
    private Integer totalUsuarios;
    private Map<String, Integer> contagemVotos;
    private Map<String, Double> percentualVotos;
    private List<VotoUsuarioDTO> votos;
    private LocalDateTime dataGeracao;
}
