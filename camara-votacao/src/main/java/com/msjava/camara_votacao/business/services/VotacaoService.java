package com.msjava.camara_votacao.business.services;

import org.springframework.stereotype.Service;

import com.msjava.camara_votacao.business.dto.VotacaoResponseDTO;
import com.msjava.camara_votacao.infrastructure.entitys.Votacao;
import com.msjava.camara_votacao.infrastructure.repository.VotacaoRepository;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioRepository;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioVotacaoRepository;
import com.msjava.camara_votacao.business.enums.TipoUsuario;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VotacaoService {
    private final VotacaoRepository votacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioVotacaoRepository usuarioVotacaoRepository;
    
    // Criar nova votação (apenas Presidente/ADM)
    public VotacaoResponseDTO criarVotacao(Integer usuarioId) {
        // Verificar permissão
        var criador = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!criador.getTipo().equals(TipoUsuario.PRESIDENTE) && 
            !criador.getTipo().equals(TipoUsuario.ADMINISTRADOR)) {
            throw new RuntimeException("Apenas Presidente ou Administrador podem criar votações");
        }
        
        // Verificar se já existe votação ativa
        boolean existeAtiva = votacaoRepository.findByVotacaoAtivaTrue().size() > 0;
        if (existeAtiva) {
            throw new RuntimeException("Já existe uma votação ativa. Encerre-a antes de criar uma nova.");
        }
        
        // Criar nova votação
        Votacao votacao = Votacao.builder()
            .votacaoAtiva(true)
            .build();
        
        Votacao votacaoSalva = votacaoRepository.save(votacao);
        return toVotacaoDTO(votacaoSalva);
    }

    public VotacaoResponseDTO encerrarVotacao(Integer usuarioId) {
        // Verificar permissão
        var encerrador = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        
        if (!encerrador.getTipo().equals(TipoUsuario.PRESIDENTE) && 
            !encerrador.getTipo().equals(TipoUsuario.ADMINISTRADOR)) {
            throw new RuntimeException("Apenas Presidente ou ADM podem encerrar votações");
        }
        
        // Buscar a votação ativa
        Votacao votacao = getVotacaoAtiva();
        
        // Encerrar
        votacao.setVotacaoAtiva(false);
        votacao.setDataEncerramento(LocalDateTime.now());
        
        Votacao votacaoSalva = votacaoRepository.save(votacao);
        return toVotacaoDTO(votacaoSalva);
    }
    
    // Listar todas as votações
    public List<VotacaoResponseDTO> listarTodasVotacoes() {
        return votacaoRepository.findAll().stream()
            .map(this::toVotacaoDTO)
            .collect(Collectors.toList());
    }
    
    // Buscar votação por data
    public List<VotacaoResponseDTO> buscarPorData(LocalDateTime data) {
        return votacaoRepository.findByDataCriacao(data).stream()
            .map(this::toVotacaoDTO)
            .collect(Collectors.toList());
    }
    
    // Método auxiliar para converter entity para DTO
    private VotacaoResponseDTO toVotacaoDTO(Votacao votacao) {
        VotacaoResponseDTO dto = new VotacaoResponseDTO();
        dto.setId(votacao.getId());
        dto.setDataCriacao(votacao.getDataCriacao());
        dto.setDataEncerramento(votacao.getDataEncerramento());
        dto.setVotacaoAtiva(votacao.getVotacaoAtiva());
        
        // Contar votos para esta votação
        Long totalVotos = usuarioVotacaoRepository.countByVotacaoId(votacao.getId());
        dto.setTotalVotos(totalVotos);
        
        return dto;
    }
    
    private Votacao getVotacaoAtiva() {
        List<Votacao> ativas = votacaoRepository.findByVotacaoAtivaTrue();
        if (ativas.isEmpty()) {
            throw new RuntimeException("Não há votação ativa no momento");
        }
        return ativas.get(0); // Retorna a única votação ativa
    }
}