package com.msjava.camara_votacao.business.dto;

import com.msjava.camara_votacao.business.enums.TipoUsuario;

import lombok.Data;

@Data
public class UsuarioDto {
    private Integer id;
    private String nome;
    private int numero;
    private String partido;
    private TipoUsuario tipo;
}
