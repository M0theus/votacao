package com.msjava.camara_votacao.business.services;

import org.springframework.stereotype.Service;
import com.msjava.camara_votacao.business.dto.*;
import com.msjava.camara_votacao.business.enums.TipoUsuario;
import com.msjava.camara_votacao.business.enums.TipoVoto;
import com.msjava.camara_votacao.infrastructure.entitys.*;
import com.msjava.camara_votacao.infrastructure.repository.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UsuarioVotacaoService {
    private final UsuarioVotacaoRepository usuarioVotacaoRepository;
    private final VotacaoRepository votacaoRepository;
    private final UsuarioRepository usuarioRepository;
    
    // Método auxiliar para buscar a votação ativa
    private Votacao getVotacaoAtiva() {
        List<Votacao> ativas = votacaoRepository.findByVotacaoAtivaTrue();
        if (ativas.isEmpty()) {
            throw new RuntimeException("Não há votação ativa no momento");
        }
        return ativas.get(0); // Retorna a única votação ativa
    }
    
    // Registrar voto (agora sem precisar do votacaoId no request)
    public UsuarioVotacaoResponseDTO registrarVoto(UsuarioVotacaoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        validarPermissaoParaVotar(usuario);
        
        // Buscar a ÚNICA votação ativa
        Votacao votacaoAtiva = getVotacaoAtiva();
        
        // Verificar se usuário já votou
        boolean jaVotou = usuarioVotacaoRepository.existsByUsuarioIdAndVotacaoId(
            request.getUsuarioId(), votacaoAtiva.getId()
        );
        if (jaVotou) {
            throw new RuntimeException("Usuário já votou nesta votação.");
        }
        
        // Registrar voto
        UsuarioVotacao usuarioVotacao = UsuarioVotacao.builder()
            .usuario(usuario)
            .votacao(votacaoAtiva)
            .voto(request.getVoto())
            .build();
        
        UsuarioVotacao salvo = usuarioVotacaoRepository.save(usuarioVotacao);
        return toUsuarioVotacaoDTO(salvo);
    }
    
    // Marcar ausente (agora usa a votação ativa automaticamente)
    public UsuarioVotacaoResponseDTO marcarAusente(Integer usuarioId) {
        // Buscar a ÚNICA votação ativa
        Votacao votacaoAtiva = getVotacaoAtiva();
        
        // Verificar se já votou
        boolean jaVotou = usuarioVotacaoRepository.existsByUsuarioIdAndVotacaoId(
            usuarioId, votacaoAtiva.getId()
        );
        if (jaVotou) {
            throw new RuntimeException("Usuário já votou");
        }
        
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        validarPermissaoParaVotar(usuario);
        
        UsuarioVotacao ausente = UsuarioVotacao.builder()
            .usuario(usuario)
            .votacao(votacaoAtiva)
            .voto(TipoVoto.AUSENTE)
            .build();
        
        UsuarioVotacao salvo = usuarioVotacaoRepository.save(ausente);
        return toUsuarioVotacaoDTO(salvo);
    }
    
    // Obter resultado da votação ativa
    public ResultadoVotacaoDTO obterResultado() {
        try {
            Votacao votacaoAtiva = getVotacaoAtiva();
            return obterResultado(votacaoAtiva.getId());
        } catch (RuntimeException e) {

            return criarResultadoVazio();
        }
    }
    
    // Método privado para obter resultado de uma votação específica
    private ResultadoVotacaoDTO obterResultado(Integer votacaoId) {
        Long totalSim = usuarioVotacaoRepository.countByVotacaoIdAndVoto(votacaoId, TipoVoto.SIM);
        Long totalNao = usuarioVotacaoRepository.countByVotacaoIdAndVoto(votacaoId, TipoVoto.NAO);
        Long totalAusente = usuarioVotacaoRepository.countByVotacaoIdAndVoto(votacaoId, TipoVoto.AUSENTE);
        Long totalAbstencao = usuarioVotacaoRepository.countByVotacaoIdAndVoto(votacaoId, TipoVoto.ABSTENCAO);
        
        Long totalNormal = usuarioRepository.countByTipo(TipoUsuario.NORMAL);
        Long totalPresidente = usuarioRepository.countByTipo(TipoUsuario.PRESIDENTE);
        Long totalUsuarios = totalNormal + totalPresidente;

        
        return new ResultadoVotacaoDTO(totalSim, totalNao, totalAbstencao, totalAusente, totalUsuarios);
    }

    private ResultadoVotacaoDTO criarResultadoVazio() {
        Long totalNormal = usuarioRepository.countByTipo(TipoUsuario.NORMAL);
        Long totalPresidente = usuarioRepository.countByTipo(TipoUsuario.PRESIDENTE);
        Long totalUsuarios = totalNormal + totalPresidente;
        return new ResultadoVotacaoDTO(0L, 0L, 0L, 0L, totalUsuarios);
    }
    
    // Listar todos os votos da votação ativa
    public List<UsuarioVotacaoResponseDTO> listarVotos() {
            try {
                Votacao votacaoAtiva = getVotacaoAtiva();
                return usuarioVotacaoRepository.findByVotacaoId(votacaoAtiva.getId()).stream()
                .map(this::toUsuarioVotacaoDTO)
                .collect(Collectors.toList());
            } catch (RuntimeException e) {
                // Se não houver votação ativa, retorna lista vazia
                return List.of();
            }
    }
    
    // Método auxiliar para converter
    private UsuarioVotacaoResponseDTO toUsuarioVotacaoDTO(UsuarioVotacao uv) {
        UsuarioVotacaoResponseDTO dto = new UsuarioVotacaoResponseDTO();
        dto.setId(uv.getId());
        dto.setUsuarioId(uv.getUsuario().getId());
        dto.setUsuarioNome(uv.getUsuario().getNome());
        dto.setVoto(uv.getVoto());
        dto.setDataVoto(uv.getDataVoto());
        dto.setVotacaoId(uv.getVotacao().getId());
        dto.setVotacaoAtiva(uv.getVotacao().getVotacaoAtiva());
        return dto;
    }

    private void validarPermissaoParaVotar(Usuario usuario) {
        if (usuario.getTipo() == TipoUsuario.ADMINISTRADOR) {
            throw new RuntimeException(
                String.format("Usuário ADMINISTRADOR não tem permissão para votar. Apenas NORMAL e PRESIDENTE podem votar.",
                    usuario.getNome(), usuario.getTipo()
                )
            );
        }
    }
}