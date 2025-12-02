package com.msjava.camara_votacao.business.dto;

import java.time.LocalDateTime;
import com.msjava.camara_votacao.business.enums.TipoVoto;
import lombok.Data;

@Data
public class UsuarioVotacaoResponseDTO {
    private Integer id;
    private Integer usuarioId;
    private String usuarioNome;
    private TipoVoto voto;
    private LocalDateTime dataVoto;
    private Integer votacaoId;
    public boolean votacaoAtiva;

}
