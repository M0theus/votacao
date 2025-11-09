package com.msjava.camara_votacao.infrastructure.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.msjava.camara_votacao.business.enums.TipoVoto;
import com.msjava.camara_votacao.infrastructure.entitys.Votacao;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Integer> {
    Optional<Votacao> findByUsuarioId(Integer usuarioId);
    
    List<Votacao> findByVoto(TipoVoto voto);
    
    List<Votacao> findByVotacaoAtiva(Boolean votacaoAtiva);
    
    @Query("SELECT COUNT(v) FROM Votacao v WHERE v.voto = ?1")
    Long countByVoto(TipoVoto voto);
    
    @Modifying
    @Query("DELETE FROM Votacao")
    void deleteAllVotacoes();

}
