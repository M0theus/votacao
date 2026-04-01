package com.msjava.camara_votacao.business.services;

import com.msjava.camara_votacao.business.dto.RelatorioVotacaoCompletoDTO;
import com.msjava.camara_votacao.business.dto.VotoUsuarioDTO;
import com.msjava.camara_votacao.business.enums.TipoVoto;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private static final Color COLOR_PRIMARY = new Color(44, 62, 80);
    private static final Color COLOR_SUCCESS = new Color(39, 174, 96);
    private static final Color COLOR_DANGER = new Color(231, 76, 60);
    private static final Color COLOR_WARNING = new Color(243, 156, 18);
    private static final Color COLOR_INFO = new Color(52, 152, 219);
    private static final Color COLOR_WHITE = new Color(255, 255, 255);
    private static final Color COLOR_GRAY = new Color(102, 102, 102);
    private static final Color COLOR_LIGHT_GRAY = new Color(241, 243, 244);

    // Gerar PDF para uma votação específica
    public byte[] gerarPdfCompleto(RelatorioVotacaoCompletoDTO relatorio) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            adicionarCabecalho(document);
            adicionarTitulo(document, "Relatório de Votação #" + relatorio.getVotacaoId());
            adicionarInformacoesVotacao(document, relatorio);
            adicionarEstatisticas(document, relatorio);
            adicionarTabelaVotos(document, relatorio.getVotos());
            adicionarRodape(document, relatorio.getDataGeracao());
            
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Erro ao gerar PDF: {}", e.getMessage());
            throw new RuntimeException("Erro ao gerar relatório PDF", e);
        }
    }

    // Gerar PDF para múltiplas votações (por data)
    public byte[] gerarPdfMultiplo(List<RelatorioVotacaoCompletoDTO> relatorios, LocalDate data) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            adicionarCabecalho(document);
            adicionarTitulo(document, "Relatório de Votações - " + data.format(DATE_ONLY_FORMATTER));
            
            for (int i = 0; i < relatorios.size(); i++) {
                if (i > 0) {
                    document.newPage();
                }
                adicionarInformacoesVotacao(document, relatorios.get(i));
                adicionarEstatisticas(document, relatorios.get(i));
                adicionarTabelaVotos(document, relatorios.get(i).getVotos());
            }
            
            adicionarRodape(document, LocalDateTime.now());
            document.close();
            
            return baos.toByteArray();
            
        } catch (Exception e) {
            log.error("Erro ao gerar PDF múltiplo: {}", e.getMessage());
            throw new RuntimeException("Erro ao gerar relatório PDF", e);
        }
    }

    private void adicionarCabecalho(Document document) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(1);
        headerTable.setWidthPercentage(100);
        
        PdfPCell cell = new PdfPCell(new Phrase("GERE - Sistema de Votação", 
                getFont(18, Font.BOLD, COLOR_WHITE)));
        cell.setBackgroundColor(COLOR_PRIMARY);
        cell.setBorderColor(COLOR_WHITE);
        cell.setPadding(15);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        headerTable.addCell(cell);
        
        document.add(headerTable);
        document.add(Chunk.NEWLINE);
    }

    private void adicionarTitulo(Document document, String titulo) throws DocumentException {
        Paragraph paragraph = new Paragraph(titulo, getFont(16, Font.BOLD, COLOR_PRIMARY));
        paragraph.setAlignment(Element.ALIGN_CENTER);
        paragraph.setSpacingAfter(20);
        document.add(paragraph);
    }

    private void adicionarInformacoesVotacao(Document document, RelatorioVotacaoCompletoDTO relatorio) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        
        adicionarLinhaInfo(infoTable, "ID da Votação:", relatorio.getVotacaoId().toString());
        adicionarLinhaInfo(infoTable, "Data de Criação:", relatorio.getDataCriacao().format(DATE_FORMATTER));
        adicionarLinhaInfo(infoTable, "Data de Encerramento:", 
                relatorio.getDataEncerramento() != null ? 
                relatorio.getDataEncerramento().format(DATE_FORMATTER) : "Em andamento");
        adicionarLinhaInfo(infoTable, "Status:", relatorio.getVotacaoAtiva() ? "ATIVA" : "ENCERRADA");
        
        document.add(infoTable);
        document.add(Chunk.NEWLINE);
    }

    private void adicionarLinhaInfo(PdfPTable table, String label, String value) {
        Font labelFont = getFont(10, Font.BOLD, COLOR_PRIMARY);
        Font valueFont = getFont(10, Font.NORMAL, COLOR_GRAY);
        
        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelFont));
        labelCell.setBorder(PdfPCell.NO_BORDER);
        labelCell.setPadding(5);
        table.addCell(labelCell);
        
        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueFont));
        valueCell.setBorder(PdfPCell.NO_BORDER);
        valueCell.setPadding(5);
        table.addCell(valueCell);
    }

    private void adicionarEstatisticas(Document document, RelatorioVotacaoCompletoDTO relatorio) throws DocumentException {
        Paragraph title = new Paragraph("Resultados da Votação", getFont(14, Font.BOLD, COLOR_PRIMARY));
        title.setSpacingAfter(10);
        document.add(title);

        PdfPTable statsTable = new PdfPTable(4);
        statsTable.setWidthPercentage(100);
        statsTable.setSpacingAfter(20);
        statsTable.setWidths(new float[]{1, 1, 1, 1});

        // Cabeçalho
        String[] headers = {"Total de Votos", "SIM", "NÃO", "AUSENTE"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, getFont(11, Font.BOLD, COLOR_WHITE)));
            cell.setBackgroundColor(COLOR_PRIMARY);
            cell.setPadding(10);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            statsTable.addCell(cell);
        }

        // Valores
        adicionarCelulaEstatistica(statsTable, relatorio.getTotalVotos().toString(), COLOR_PRIMARY);
        adicionarCelulaEstatistica(statsTable, 
                String.format("%d (%.1f%%)", 
                    relatorio.getContagemVotos().getOrDefault("SIM", 0),
                    relatorio.getPercentualVotos().getOrDefault("SIM", 0.0)), 
                COLOR_SUCCESS);
        adicionarCelulaEstatistica(statsTable, 
                String.format("%d (%.1f%%)", 
                    relatorio.getContagemVotos().getOrDefault("NAO", 0),
                    relatorio.getPercentualVotos().getOrDefault("NAO", 0.0)), 
                COLOR_DANGER);
        adicionarCelulaEstatistica(statsTable, 
                String.format("%d (%.1f%%)", 
                    relatorio.getContagemVotos().getOrDefault("AUSENTE", 0),
                    relatorio.getPercentualVotos().getOrDefault("AUSENTE", 0.0)), 
                COLOR_INFO);

        document.add(statsTable);
        
        // Participação
        Paragraph participacao = new Paragraph(String.format("Participação: %d de %d usuários (%.1f%%)",
                relatorio.getTotalVotos(),
                relatorio.getTotalUsuarios(),
                (relatorio.getTotalVotos() * 100.0 / relatorio.getTotalUsuarios())),
                getFont(11, Font.NORMAL, COLOR_GRAY));
        participacao.setSpacingAfter(15);
        document.add(participacao);
    }

    private void adicionarCelulaEstatistica(PdfPTable table, String valor, Color cor) {
        PdfPCell cell = new PdfPCell(new Phrase(valor, getFont(12, Font.BOLD, cor)));
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(cor);
        cell.setBorderWidth(1);
        table.addCell(cell);
    }

    private void adicionarTabelaVotos(Document document, List<VotoUsuarioDTO> votos) throws DocumentException {
        Paragraph title = new Paragraph("Detalhamento dos Votos", getFont(14, Font.BOLD, COLOR_PRIMARY));
        title.setSpacingAfter(10);
        document.add(title);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{2f, 2.5f, 1.5f, 1.5f});

        String[] headers = {"Data/Hora", "Usuário", "Partido", "Voto"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, getFont(11, Font.BOLD, COLOR_WHITE)));
            cell.setBackgroundColor(COLOR_PRIMARY);
            cell.setPadding(12);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (VotoUsuarioDTO voto : votos) {
            table.addCell(criarCelula(voto.getDataVoto().format(DATE_FORMATTER), Element.ALIGN_LEFT));
            table.addCell(criarCelula(voto.getUsuarioNome(), Element.ALIGN_LEFT));
            table.addCell(criarCelula(voto.getUsuarioPartido() != null ? voto.getUsuarioPartido() : "-", Element.ALIGN_LEFT));
            table.addCell(criarCelulaVoto(voto.getVoto()));
        }

        document.add(table);
    }

    private PdfPCell criarCelula(String texto, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, getFont(10, Font.NORMAL, new Color(51, 51, 51))));
        cell.setPadding(8);
        cell.setHorizontalAlignment(alignment);
        cell.setBorderColor(COLOR_LIGHT_GRAY);
        return cell;
    }

    private PdfPCell criarCelulaVoto(TipoVoto voto) {
        Font font = getFont(10, Font.BOLD, COLOR_WHITE);
        Color backgroundColor;
        String textoVoto;
        
        switch (voto) {
            case SIM:
                backgroundColor = COLOR_SUCCESS;
                textoVoto = "SIM";
                break;
            case NAO:
                backgroundColor = COLOR_DANGER;
                textoVoto = "NÃO";
                break;
            case ABSTENCAO:
                backgroundColor = COLOR_WARNING;
                textoVoto = "ABSTENÇÃO";
                break;
            case AUSENTE:
                backgroundColor = COLOR_INFO;
                textoVoto = "AUSENTE";
                break;
            default:
                backgroundColor = COLOR_GRAY;
                textoVoto = voto.name();
                break;
        }
        
        PdfPCell cell = new PdfPCell(new Phrase(textoVoto, font));
        cell.setPadding(8);
        cell.setBackgroundColor(backgroundColor);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(COLOR_LIGHT_GRAY);
        return cell;
    }

    private void adicionarRodape(Document document, LocalDateTime dataGeracao) {
        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setTotalWidth(PageSize.A4.getWidth() - 80);
        footerTable.setWidthPercentage(100);
        
        String textoRodape = String.format("Relatório gerado em: %s", dataGeracao.format(DATE_FORMATTER));
        
        PdfPCell cell = new PdfPCell(new Phrase(textoRodape, getFont(8, Font.ITALIC, new Color(149, 165, 166))));
        cell.setBorder(PdfPCell.TOP);
        cell.setPadding(10);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        footerTable.addCell(cell);
        
        try {
            document.add(footerTable);
        } catch (DocumentException e) {
            log.error("Erro ao adicionar rodapé: {}", e.getMessage());
        }
    }

    private Font getFont(int size, int style, Color color) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setSize(size);
        font.setStyle(style);
        font.setColor(color);
        return font;
    }
}
