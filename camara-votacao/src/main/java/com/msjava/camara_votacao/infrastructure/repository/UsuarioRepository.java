package com.msjava.camara_votacao.infrastructure.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.msjava.camara_votacao.business.enums.TipoUsuario;
import com.msjava.camara_votacao.infrastructure.entitys.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>{

    Optional<Usuario> findByNome(String nome);
    Optional<Usuario> findByCpf(String cpf);
    boolean existsByNome(String nome);
    java.util.List<Usuario> findByPartido(String partido);
    java.util.List<Usuario> findByTipo(TipoUsuario tipoUsuario);
    Long countByTipo(TipoUsuario tipoUsuario);
}