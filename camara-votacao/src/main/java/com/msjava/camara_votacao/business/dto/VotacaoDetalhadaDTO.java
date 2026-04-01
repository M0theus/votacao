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
public class VotacaoDetalhadaDTO {
    private Integer votacaoId;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEncerramento;
    private Boolean votacaoAtiva;
    private Integer totalVotos;
    private Map<String, Integer> contagemVotos;
    private List<VotoUsuarioDTO> votos;
}
