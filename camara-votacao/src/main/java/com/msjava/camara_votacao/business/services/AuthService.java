package com.msjava.camara_votacao.business.services;

import org.springframework.stereotype.Service;

import com.msjava.camara_votacao.business.dto.LoginRequestDTO;
import com.msjava.camara_votacao.business.dto.LoginResponseDTO;
import com.msjava.camara_votacao.business.utils.JwtUtil;
import com.msjava.camara_votacao.infrastructure.entitys.Usuario;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtUtil jwtUtil;

    public LoginResponseDTO login(LoginRequestDTO request) {
        
        Usuario usuario = usuarioRepository.findByCpf(request.getCpf())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        
        if (!request.getSenha().equals(usuario.getSenha())) {
            throw new RuntimeException("Senha incorreta");
        }

        
        String token = jwtUtil.generateToken(
            usuario.getId(), 
            usuario.getNome(),
            usuario.getTipo(),
            usuario.getCpf()
        );

        
        LoginResponseDTO response = new LoginResponseDTO();
        response.setId(usuario.getId());
        response.setNome(usuario.getNome());
        response.setPartido(usuario.getPartido());
        response.setTipo(usuario.getTipo());
        response.setCpf(usuario.getCpf());
        response.setToken(token);
        response.setMensagem("Login realizado com sucesso");

        return response;
    }
}
