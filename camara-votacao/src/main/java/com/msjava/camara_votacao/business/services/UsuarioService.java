package com.msjava.camara_votacao.business.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;

import com.msjava.camara_votacao.business.dto.UsuarioDto;
import com.msjava.camara_votacao.business.dto.UsuarioRequestDto;
import com.msjava.camara_votacao.business.enums.TipoUsuario;
import com.msjava.camara_votacao.infrastructure.entitys.Usuario;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    public UsuarioService(UsuarioRepository repository) {
        this.usuarioRepository = repository;
    }

    public UsuarioDto criarUsuario(UsuarioRequestDto request) {
        if (usuarioRepository.existsByNome(request.getNome())) {
            throw new RuntimeException("Já existe um usuário com esse nome");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(request.getNome());
        usuario.setPartido(request.getPartido());
        usuario.setCpf(request.getCpf());
        usuario.setSenha(request.getSenha());
        
        usuario.setTipo(request.getTipo());

        Usuario usuarioSalvo = usuarioRepository.saveAndFlush(usuario);
        return toDTO(usuarioSalvo);
    }

    public List<UsuarioDto> listarTodos() {
        return usuarioRepository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public UsuarioDto buscarPorId(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeErrorException(null, "Usuário não encontrado com ID: " + id));
        return toDTO(usuario);
    }

    public UsuarioDto atualizarUsuario(Integer id, UsuarioRequestDto request) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeErrorException(null, "Usuário não encontrado com ID: " + id));
        
        usuario.setNome(request.getNome());
        usuario.setCpf(request.getCpf());
        usuario.setPartido(request.getPartido());
        usuario.setTipo(request.getTipo());

        if (request.getSenha() != null && request.getSenha().trim().isEmpty()) {
            usuario.setSenha(request.getSenha());
        }

        Usuario usuarioAtualizado = usuarioRepository.saveAndFlush(usuario);
        return toDTO(usuarioAtualizado);
    }

    public void deletarUsuario(Integer id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    public List<UsuarioDto> buscarPorPartido(String partido) {
        return usuarioRepository.findByPartido(partido)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UsuarioDto> buscarPorTipo(TipoUsuario tipo) {
        return usuarioRepository.findByTipo(tipo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private UsuarioDto toDTO(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setCpf(usuario.getCpf());
        dto.setPartido(usuario.getPartido());
        dto.setTipo(usuario.getTipo());
        return dto;
    }
    
}
