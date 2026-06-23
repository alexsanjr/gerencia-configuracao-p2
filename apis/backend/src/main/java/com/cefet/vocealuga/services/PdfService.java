package com.cefet.vocealuga.services;

import com.cefet.vocealuga.dtos.ReservaDTO;
import com.cefet.vocealuga.entities.Filial;
import com.cefet.vocealuga.entities.Pagamento;
import com.cefet.vocealuga.entities.Veiculo;
import com.cefet.vocealuga.repositories.FilialRepository;
import com.cefet.vocealuga.repositories.VeiculoRepository;
import com.cefet.vocealuga.services.exceptions.ResourceNotFoundException;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.properties.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private FilialRepository filialRepository;

    private static final Logger logger = LoggerFactory.getLogger(PdfService.class);

    public byte[] gerarComprovantePagamento(Pagamento pagamento, ReservaDTO reserva) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Título
            Text titulo = new Text("COMPROVANTE DE PAGAMENTO")
                    .setFontSize(18)
                    .setBold();
            Paragraph tituloParagraph = new Paragraph(titulo)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(tituloParagraph);

            // Informações da empresa
            document.add(new Paragraph("VOCÊ ALUGA - LOCADORA DE VEÍCULOS")
                    .setFontSize(14)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("www.vocealuga.com.br")
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Linha separadora
            document.add(new Paragraph("─".repeat(60))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Dados do pagamento
            document.add(new Paragraph("DADOS DO PAGAMENTO")
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(10));

            document.add(new Paragraph("Token: " + pagamento.getToken())
                    .setFontSize(10));
            document.add(new Paragraph("Data: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                    .setFontSize(10));
            document.add(new Paragraph("Método: " + pagamento.getMetodo().toUpperCase())
                    .setFontSize(10));
            document.add(new Paragraph("Valor: R$ " + String.format("%.2f", pagamento.getValor()))
                    .setFontSize(10)
                    .setMarginBottom(15));

            // Dados da reserva (se existir)
            if (reserva != null) {
                document.add(new Paragraph("DADOS DA RESERVA")
                        .setFontSize(12)
                        .setBold()
                        .setMarginBottom(10));

                document.add(new Paragraph("Reserva ID: " + reserva.getId())
                        .setFontSize(10));
                document.add(new Paragraph("Data da Reserva: " + reserva.getDataReserva().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .setFontSize(10));
                document.add(new Paragraph("Data de Vencimento: " + reserva.getDataVencimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                        .setFontSize(10));
                document.add(new Paragraph("Categoria: " + reserva.getCategoria())
                        .setFontSize(10));

                if (reserva.getVeiculoId() != null) {
                    Veiculo veiculo =  veiculoRepository.findById(reserva.getVeiculoId()).orElseThrow(() ->new ResourceNotFoundException("Veículo não encontrado"));
                    document.add(new Paragraph("Veículo: " + veiculo.getModelo() + " - " + veiculo.getPlaca())
                            .setFontSize(10));
                }

                if (reserva.getLocalRetiradaId() != null) {
                    Filial localDeRetirada = filialRepository.findById(reserva.getLocalRetiradaId())
                            .orElseThrow(() -> new ResourceNotFoundException("Filial não encontrada"));
                    document.add(new Paragraph("Local de Retirada: " + localDeRetirada.getNome())
                            .setFontSize(10));
                }

                document.add(new Paragraph("Valor Total: R$ " + String.format("%.2f", reserva.getValorTotal()))
                        .setFontSize(10)
                        .setMarginBottom(15));
            }

            // Dados do cliente
            document.add(new Paragraph("DADOS DO CLIENTE")
                    .setFontSize(12)
                    .setBold()
                    .setMarginBottom(10));
            document.add(new Paragraph("Email: " + pagamento.getEmailUsuario())
                    .setFontSize(10)
                    .setMarginBottom(20));

            // Linha separadora
            document.add(new Paragraph("─".repeat(60))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Status
            document.add(new Paragraph("STATUS: PAGAMENTO CONFIRMADO")
                    .setFontSize(12)
                    .setBold()
                    .setFontColor(ColorConstants.GREEN)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Rodapé
            document.add(new Paragraph("Este é um comprovante gerado automaticamente.")
                    .setFontSize(8)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            logger.error("Erro ao gerar PDF do comprovante: ", e);
            throw new RuntimeException("Erro ao gerar comprovante PDF", e);
        }
    }
}