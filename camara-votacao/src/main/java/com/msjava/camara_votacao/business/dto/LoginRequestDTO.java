package com.msjava.camara_votacao.business.dto;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String cpf;
    private String senha;
}
