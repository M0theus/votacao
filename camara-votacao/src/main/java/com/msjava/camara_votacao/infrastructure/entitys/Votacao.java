package com.msjava.camara_votacao.infrastructure.entitys;

import java.time.LocalDateTime;

import com.msjava.camara_votacao.business.enums.TipoVoto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "votacoes")
public class Votacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private TipoVoto voto;
    
    @Column(name = "data_voto")
    private LocalDateTime dataVoto;
    
    @Column(name = "votacao_ativa")
    private Boolean votacaoAtiva = true;
    
    @PrePersist
    public void prePersist() {
        this.dataVoto = LocalDateTime.now();
    }

}
