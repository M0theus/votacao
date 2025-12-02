package com.msjava.camara_votacao.business.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class VotacaoResponseDTO {
    private Integer id;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataEncerramento;
    private Boolean votacaoAtiva;
    private Long totalVotos;

}
