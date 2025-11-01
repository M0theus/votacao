package com.msjava.camara_votacao.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.msjava.camara_votacao.business.dto.UsuarioDto;
import com.msjava.camara_votacao.business.dto.UsuarioRequestDto;
import com.msjava.camara_votacao.business.enums.TipoUsuario;
import com.msjava.camara_votacao.business.services.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioDto> criarUsuario(@RequestBody UsuarioRequestDto request) {
        UsuarioDto usuario = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> listarTodos() {
        List<UsuarioDto> usuarios = usuarioService.listarTodos();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDto> buscarPorId(@PathVariable Integer id) {
        UsuarioDto usuario = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(usuario);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDto> atualizarUsuario(@PathVariable Integer id,
            @RequestBody UsuarioRequestDto request) {
        UsuarioDto usuario = usuarioService.atualizarUsuario(id, request);
        return ResponseEntity.ok(usuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable Integer id) {
        usuarioService.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/partido/{partido}")
    public ResponseEntity<List<UsuarioDto>> buscarPorPartido(@PathVariable String partido) {
        List<UsuarioDto> usuarios = usuarioService.buscarPorPartido(partido);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<UsuarioDto>> buscarPorTipo(@PathVariable TipoUsuario tipo) {
        List<UsuarioDto> usuarios = usuarioService.buscarPorTipo(tipo);
        return ResponseEntity.ok(usuarios);
    }
}
