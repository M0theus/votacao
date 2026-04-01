package com.msjava.camara_votacao.infrastructure.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.msjava.camara_votacao.infrastructure.entitys.Votacao;

@Repository
public interface VotacaoRepository extends JpaRepository<Votacao, Integer> {

    List<Votacao> findByVotacaoAtiva(Boolean votacaoAtiva);
    List<Votacao> findByDataCriacaoAfter(LocalDateTime data);
    List<Votacao> findByVotacaoAtivaFalse();
    List<Votacao> findByVotacaoAtivaTrue();
    List<Votacao> findByDataCriacaoBetween(LocalDateTime inicio, LocalDateTime fim);
    Votacao findTopByOrderByIdDesc();

    @Query("SELECT v FROM Votacao v WHERE DATE(v.dataCriacao) = DATE(:data)")
    List<Votacao> findByDataCriacao(@Param("data") LocalDateTime data);
}
