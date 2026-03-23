package com.msjava.camara_votacao.business.dto;

import lombok.Data;

@Data
public class ResultadoVotacaoDTO {
    private Long sim;
    private Long nao;
    private Long ausentes;
    private Long abstencao;
    private Long totalUsuarios;
    
    public ResultadoVotacaoDTO(Long totalSim, Long totalNao, long abstencao, Long totalAusente, long totalUsuarios) {
        this.sim = totalSim;
        this.nao = totalNao;
        this.ausentes = totalAusente;
        this.abstencao = abstencao;
        this.totalUsuarios = totalUsuarios;
    }
}
