package com.msjava.camara_votacao.business.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.msjava.camara_votacao.business.dto.ResultadoVotacaoDTO;
import com.msjava.camara_votacao.business.dto.VotacaoRequestDTO;
import com.msjava.camara_votacao.business.dto.VotacaoResponseDTO;
import com.msjava.camara_votacao.business.enums.TipoVoto;
import com.msjava.camara_votacao.infrastructure.entitys.Usuario;
import com.msjava.camara_votacao.infrastructure.entitys.Votacao;
import com.msjava.camara_votacao.infrastructure.repository.UsuarioRepository;
import com.msjava.camara_votacao.infrastructure.repository.VotacaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VotacaoService {
    private final VotacaoRepository votacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public VotacaoResponseDTO registrarVoto(VotacaoRequestDTO request) {
        if (!isVotacaoAtiva()) {
            throw new RuntimeException("Votação não está ativa. Não é possível votar.");
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        votacaoRepository.findByUsuarioId(request.getUsuarioId())
            .ifPresent(v -> {
                throw new RuntimeException("Usuário já votou");
            });

        Votacao votacao = new Votacao();
        votacao.setUsuario(usuario);
        votacao.setVoto(request.getVoto());
        votacao.setVotacaoAtiva(true);

        Votacao votacaoSalva = votacaoRepository.save(votacao);
        return toDTO(votacaoSalva);
    }

    public VotacaoResponseDTO marcarAusente(Integer usuarioId) {
        if (!isVotacaoAtiva()) {
            throw new RuntimeException("Votação não está ativa. Não é possível marcar ausente.");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        votacaoRepository.findByUsuarioId(usuarioId)
            .ifPresent(v -> {
                throw new RuntimeException("Usuário já foi processado");
            });

        Votacao votacao = new Votacao();
        votacao.setUsuario(usuario);
        votacao.setVoto(TipoVoto.AUSENTE);
        votacao.setVotacaoAtiva(true);

        Votacao votacaoSalva = votacaoRepository.save(votacao);
        return toDTO(votacaoSalva);
    }

    public ResultadoVotacaoDTO obterResultado() {
        Long totalSim = votacaoRepository.countByVoto(TipoVoto.SIM);
        Long totalNao = votacaoRepository.countByVoto(TipoVoto.NAO);
        Long totalAusente = votacaoRepository.countByVoto(TipoVoto.AUSENTE);

        return new ResultadoVotacaoDTO(totalSim, totalNao, totalAusente);
    }

    public List<VotacaoResponseDTO> listarVotos() {
        return votacaoRepository.findAll()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    public void finalizarVotacao() {
        List<Votacao> votacoesAtivas = votacaoRepository.findByVotacaoAtiva(true);
        votacoesAtivas.forEach(v -> v.setVotacaoAtiva(false));
        votacaoRepository.saveAll(votacoesAtivas);
    }

    public void zerarVotacao() {
        votacaoRepository.deleteAll();
    }

    private boolean isVotacaoAtiva() {
        List<Votacao> votos = votacaoRepository.findAll();
        
        if (votos.isEmpty()) {
            return true;
        }
        
        Votacao ultimoVoto = votos.get(votos.size() - 1);
        return ultimoVoto.getVotacaoAtiva();
    }

    private VotacaoResponseDTO toDTO(Votacao votacao) {
        VotacaoResponseDTO dto = new VotacaoResponseDTO();
        dto.setId(votacao.getId());
        dto.setUsuarioId(votacao.getUsuario().getId());
        dto.setUsuarioNome(votacao.getUsuario().getNome());
        dto.setVoto(votacao.getVoto());
        dto.setDataVoto(votacao.getDataVoto());
        dto.setVotacaoAtiva(votacao.getVotacaoAtiva());
        return dto;
    }
}