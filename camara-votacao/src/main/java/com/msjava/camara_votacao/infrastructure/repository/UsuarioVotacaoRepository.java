package com.msjava.camara_votacao.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.msjava.camara_votacao.business.enums.TipoVoto;
import com.msjava.camara_votacao.infrastructure.entitys.UsuarioVotacao;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioVotacaoRepository extends JpaRepository<UsuarioVotacao, Integer> {
    
    boolean existsByUsuarioIdAndVotacaoId(Integer usuarioId, Integer votacaoId);
    List<UsuarioVotacao> findByVotacaoId(Integer votacaoId);
    List<UsuarioVotacao> findByUsuarioId(Integer usuarioId);
    List<UsuarioVotacao> findByVotacaoIdAndVoto(Integer votacaoId, TipoVoto voto);
    Long countByVotacaoIdAndVoto(Integer votacaoId, TipoVoto voto);
    Optional<UsuarioVotacao> findByUsuarioIdAndVotacaoId(Integer usuarioId, Integer votacaoId);

    @Query("SELECT uv.voto, COUNT(uv) FROM UsuarioVotacao uv WHERE uv.votacao.id = :votacaoId GROUP BY uv.voto")
    List<Object[]> contarVotosPorTipo(@Param("votacaoId") Integer votacaoId);
    
    Long countByVotacaoId(Integer votacaoId);
    boolean existsByVotacaoId(Integer votacaoId);
}