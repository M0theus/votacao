package com.msjava.camara_votacao.business.dto;

import com.msjava.camara_votacao.business.enums.TipoVoto;

import lombok.Data;

@Data
public class VotacaoRequestDTO {
    private Integer usuarioId;
    private TipoVoto voto;

}
