package com.msjava.camara_votacao.business.dto;

import lombok.Data;

@Data
public class ResultadoVotacaoDTO {
    private Long sim;
    private Long nao;
    private Long ausentes;
    private Long totalUsuarios;
    
    public ResultadoVotacaoDTO(Long totalSim, Long totalNao, Long totalAusente) {
        this.sim = totalSim;
        this.nao = totalNao;
        this.ausentes = totalAusente;
        this.totalUsuarios = totalSim + totalNao + totalAusente;
    }
}
