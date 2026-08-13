package com.thewala.inventario;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfExportador {

    private static final Color VERDE = new Color(25, 135, 84);
    private static final Color VERDE_CLARO = new Color(209, 231, 221);

    public static byte[] generar(List<Equipo> equipos) throws Exception {
        // Hoja A4 horizontal (rotate) porque son muchas columnas
        Document documento = new Document(PageSize.A4.rotate(), 25, 25, 30, 30);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, salida);
        documento.open();

        // ---------- TITULO ----------
        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, VERDE);
        Paragraph titulo = new Paragraph("INVENTARIO DE EQUIPOS - IPS THE WALA", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.GRAY);
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Paragraph subtitulo = new Paragraph(
                "Generado el " + fechaHoy + "  |  " + equipos.size() + " equipo(s)", fuenteSubtitulo);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(12);
        documento.add(subtitulo);

        // ---------- TABLA ----------
        String[] titulos = {"Placa", "Marca", "Modelo", "Tipo", "Sede", "Responsable",
                "Procesador", "RAM", "Disco", "Estado", "Fecha compra"};

        PdfPTable tabla = new PdfPTable(titulos.length);
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{2.2f, 1.6f, 2.4f, 1.8f, 1.8f, 2.6f, 2.4f, 1f, 1f, 1.8f, 1.8f});
        tabla.setHeaderRows(1);   // repite el encabezado en cada pagina

        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Color.WHITE);
        for (String texto : titulos) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteEncabezado));
            celda.setBackgroundColor(VERDE);
            celda.setPadding(5);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }

        Font fuenteDato = FontFactory.getFont(FontFactory.HELVETICA, 8);
        int numeroFila = 0;
        for (Equipo equipo : equipos) {
            Color fondo = (numeroFila++ % 2 == 0) ? Color.WHITE : VERDE_CLARO;
            agregar(tabla, equipo.getPlaca(), fuenteDato, fondo);
            agregar(tabla, equipo.getMarca(), fuenteDato, fondo);
            agregar(tabla, equipo.getModelo(), fuenteDato, fondo);
            agregar(tabla, equipo.getTipo(), fuenteDato, fondo);
            agregar(tabla, equipo.getSede(), fuenteDato, fondo);
            agregar(tabla, equipo.getResponsable(), fuenteDato, fondo);
            agregar(tabla, equipo.getProcesador(), fuenteDato, fondo);
            agregar(tabla, textoDe(equipo.getRamGb()), fuenteDato, fondo);
            agregar(tabla, textoDe(equipo.getDiscoGb()), fuenteDato, fondo);
            agregar(tabla, equipo.getEstado(), fuenteDato, fondo);
            String fecha = equipo.getFechaCompra() == null ? ""
                    : equipo.getFechaCompra().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            agregar(tabla, fecha, fuenteDato, fondo);
        }

        documento.add(tabla);
        documento.close();
        return salida.toByteArray();
    }

    private static void agregar(PdfPTable tabla, String texto, Font fuente, Color fondo) {
        PdfPCell celda = new PdfPCell(new Phrase(texto == null ? "" : texto, fuente));
        celda.setBackgroundColor(fondo);
        celda.setPadding(4);
        tabla.addCell(celda);
    }

    private static String textoDe(Integer numero) {
        return numero == null ? "" : numero.toString();
    }
}