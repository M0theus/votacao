package com.msjava.camara_votacao.business.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SenhaUtils {
    private final BCryptPasswordEncoder passwordEncoder;

    public SenhaUtils() {
        this.passwordEncoder= new BCryptPasswordEncoder();
    }
    
    public String criptografarSenha(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        return passwordEncoder.encode(senha);
    }
    
    public boolean verificarSenha(String senhaDigitada, String senhaCriptografada) {
        if (senhaDigitada == null || senhaCriptografada == null) {
            return false;
        }
        return passwordEncoder.matches(senhaDigitada, senhaCriptografada);
    }
}
