package com.msjava.camara_votacao.business.dto;

import com.msjava.camara_votacao.business.enums.TipoUsuario;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private Integer id;
    private String nome;
    private String partido;
    private String cpf;
    private TipoUsuario tipo;
    private String token;
    private String mensagem;
}
