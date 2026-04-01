package com.msjava.camara_votacao.business.dto;

import java.time.LocalDateTime;

import com.msjava.camara_votacao.business.enums.TipoVoto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VotoUsuarioDTO {
    private Integer usuarioId;
    private String usuarioNome;
    private String usuarioPartido;
    private TipoVoto voto;
    private LocalDateTime dataVoto;
}
