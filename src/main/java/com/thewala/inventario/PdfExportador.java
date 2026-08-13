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
    public static byte[] generarActa(Equipo equipo, List<Periferico> perifericos) throws Exception {
        Document documento = new Document(PageSize.A4, 45, 45, 45, 45);
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        PdfWriter.getInstance(documento, salida);
        documento.open();

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, VERDE);
        Paragraph titulo = new Paragraph("ACTA DE ENTREGA DE EQUIPO", fuenteTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        documento.add(titulo);

        Font fuenteGris = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.GRAY);
        String fechaHoy = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        Paragraph subtitulo = new Paragraph("IPS THE WALA  |  " + fechaHoy, fuenteGris);
        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(20);
        documento.add(subtitulo);

        Font fuenteTexto = FontFactory.getFont(FontFactory.HELVETICA, 11);
        Paragraph intro = new Paragraph(
                "En la fecha se hace entrega del siguiente equipo de computo, con los perifericos "
                        + "relacionados, al funcionario responsable. El funcionario se compromete a velar "
                        + "por su buen uso y conservacion.", fuenteTexto);
        intro.setSpacingAfter(18);
        documento.add(intro);

        // ---------- DATOS DEL EQUIPO ----------
        PdfPTable datos = new PdfPTable(2);
        datos.setWidthPercentage(100);
        datos.setWidths(new float[]{1.2f, 3f});
        dato(datos, "Placa", equipo.getPlaca());
        dato(datos, "Tipo", equipo.getTipo());
        dato(datos, "Marca / Modelo", equipo.getMarca() + " " + equipo.getModelo());
        dato(datos, "Procesador", equipo.getProcesador());
        dato(datos, "Memoria RAM", textoDe(equipo.getRamGb()) + " GB");
        dato(datos, "Disco duro", textoDe(equipo.getDiscoGb()) + " GB");
        dato(datos, "Sede", equipo.getSede());
        dato(datos, "Estado", equipo.getEstado());
        dato(datos, "Responsable", equipo.getResponsable());
        datos.setSpacingAfter(18);
        documento.add(datos);

        // ---------- PERIFERICOS ----------
        Font fuenteSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, VERDE);
        Paragraph tituloPerifericos = new Paragraph("Perifericos entregados", fuenteSubtitulo);
        tituloPerifericos.setSpacingAfter(8);
        documento.add(tituloPerifericos);

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        Font fuenteEncabezado = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (String texto : new String[]{"Tipo", "Marca", "Serial"}) {
            PdfPCell celda = new PdfPCell(new Phrase(texto, fuenteEncabezado));
            celda.setBackgroundColor(VERDE);
            celda.setPadding(5);
            tabla.addCell(celda);
        }
        Font fuenteDato = FontFactory.getFont(FontFactory.HELVETICA, 10);
        if (perifericos.isEmpty()) {
            PdfPCell vacio = new PdfPCell(new Phrase("Sin perifericos registrados", fuenteDato));
            vacio.setColspan(3);
            vacio.setPadding(6);
            tabla.addCell(vacio);
        } else {
            for (Periferico periferico : perifericos) {
                agregar(tabla, periferico.getTipo(), fuenteDato, Color.WHITE);
                agregar(tabla, periferico.getMarca(), fuenteDato, Color.WHITE);
                agregar(tabla, periferico.getSerial(), fuenteDato, Color.WHITE);
            }
        }
        tabla.setSpacingAfter(45);
        documento.add(tabla);

        // ---------- FIRMAS ----------
        PdfPTable firmas = new PdfPTable(2);
        firmas.setWidthPercentage(100);
        firma(firmas, "ENTREGA", "Coordinacion de Sistemas");
        firma(firmas, "RECIBE", equipo.getResponsable());
        documento.add(firmas);

        documento.close();
        return salida.toByteArray();
    }

    private static void dato(PdfPTable tabla, String etiqueta, String valor) {
        Font fuenteEtiqueta = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
        Font fuenteValor = FontFactory.getFont(FontFactory.HELVETICA, 10);

        PdfPCell celdaEtiqueta = new PdfPCell(new Phrase(etiqueta, fuenteEtiqueta));
        celdaEtiqueta.setBackgroundColor(VERDE_CLARO);
        celdaEtiqueta.setPadding(5);
        tabla.addCell(celdaEtiqueta);

        PdfPCell celdaValor = new PdfPCell(new Phrase(valor == null ? "" : valor, fuenteValor));
        celdaValor.setPadding(5);
        tabla.addCell(celdaValor);
    }

    private static void firma(PdfPTable tabla, String rol, String nombre) {
        Font fuenteRol = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
        Font fuenteNombre = FontFactory.getFont(FontFactory.HELVETICA, 9);

        Paragraph contenido = new Paragraph();
        contenido.add(new Phrase("__________________________\n", fuenteNombre));
        contenido.add(new Phrase(rol + "\n", fuenteRol));
        contenido.add(new Phrase(nombre == null ? "" : nombre, fuenteNombre));

        PdfPCell celda = new PdfPCell();
        celda.addElement(contenido);
        celda.setBorder(Rectangle.NO_BORDER);
        celda.setPadding(8);
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        tabla.addCell(celda);
    }
}