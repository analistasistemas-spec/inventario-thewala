package com.thewala.inventario;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class ExcelExportador {

    public static byte[] generar(List<Equipo> equipos) throws Exception {
        Workbook libro = new XSSFWorkbook();
        Sheet hoja = libro.createSheet("Inventario");

        String[] titulos = {"Placa", "Marca", "Modelo", "Tipo", "Sede",
                "Responsable", "Procesador", "RAM (GB)", "Disco (GB)",
                "Estado", "Fecha compra"};

        // Verde institucional (el mismo del boton) y su version clara
        XSSFColor verdeFuerte = new XSSFColor(new java.awt.Color(25, 135, 84), null);
        XSSFColor verdeClaro  = new XSSFColor(new java.awt.Color(209, 231, 221), null);

        // ---------- ESTILOS ----------
        // Titulo grande del reporte
        CellStyle estiloReporte = libro.createCellStyle();
        Font fuenteReporte = libro.createFont();
        fuenteReporte.setBold(true);
        fuenteReporte.setFontHeightInPoints((short) 14);
        fuenteReporte.setColor(IndexedColors.BLACK1.getIndex());
        estiloReporte.setFont(fuenteReporte);
        estiloReporte.setAlignment(HorizontalAlignment.CENTER);

        // Encabezado de columnas: fondo azul, letra blanca, centrado, con borde
        CellStyle estiloEncabezado = libro.createCellStyle();
        Font fuenteEncabezado = libro.createFont();
        fuenteEncabezado.setBold(true);
        fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
        estiloEncabezado.setFont(fuenteEncabezado);
        ((XSSFCellStyle) estiloEncabezado).setFillForegroundColor(verdeFuerte);
        estiloEncabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        estiloEncabezado.setAlignment(HorizontalAlignment.CENTER);
        estiloEncabezado.setVerticalAlignment(VerticalAlignment.CENTER);
        ponerBordes(estiloEncabezado);

        // Celdas normales y celdas de fila alterna (gris clarito)
        CellStyle estiloCelda = libro.createCellStyle();
        ponerBordes(estiloCelda);

        CellStyle estiloCeldaAlterna = libro.createCellStyle();
        ponerBordes(estiloCeldaAlterna);
        ((XSSFCellStyle) estiloCeldaAlterna).setFillForegroundColor(verdeClaro);
        estiloCeldaAlterna.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Fechas con formato dd/MM/yyyy
        CreationHelper ayudante = libro.getCreationHelper();
        CellStyle estiloFecha = libro.createCellStyle();
        ponerBordes(estiloFecha);
        estiloFecha.setDataFormat(ayudante.createDataFormat().getFormat("dd/MM/yyyy"));
        estiloFecha.setAlignment(HorizontalAlignment.CENTER);

        CellStyle estiloFechaAlterna = libro.createCellStyle();
        estiloFechaAlterna.cloneStyleFrom(estiloFecha);
        ((XSSFCellStyle) estiloFechaAlterna).setFillForegroundColor(verdeClaro);
        estiloFechaAlterna.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // ---------- FILA 0: TITULO DEL REPORTE ----------
        Row filaReporte = hoja.createRow(0);
        filaReporte.setHeightInPoints(22);
        Cell celdaReporte = filaReporte.createCell(0);
        celdaReporte.setCellValue("INVENTARIO DE EQUIPOS - IPS THE WALA");
        celdaReporte.setCellStyle(estiloReporte);
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, titulos.length - 1));

        // ---------- FILA 1: ENCABEZADOS ----------
        Row filaTitulos = hoja.createRow(1);
        filaTitulos.setHeightInPoints(18);
        for (int i = 0; i < titulos.length; i++) {
            Cell celda = filaTitulos.createCell(i);
            celda.setCellValue(titulos[i]);
            celda.setCellStyle(estiloEncabezado);
        }

        // ---------- DATOS ----------
        int numeroFila = 2;
        for (Equipo equipo : equipos) {
            boolean alterna = (numeroFila % 2 == 0);
            CellStyle estilo = alterna ? estiloCeldaAlterna : estiloCelda;

            Row fila = hoja.createRow(numeroFila++);
            escribir(fila, 0, equipo.getPlaca(), estilo);
            escribir(fila, 1, equipo.getMarca(), estilo);
            escribir(fila, 2, equipo.getModelo(), estilo);
            escribir(fila, 3, equipo.getTipo(), estilo);
            escribir(fila, 4, equipo.getSede(), estilo);
            escribir(fila, 5, equipo.getResponsable(), estilo);
            escribir(fila, 6, equipo.getProcesador(), estilo);
            escribirNumero(fila, 7, equipo.getRamGb(), estilo);
            escribirNumero(fila, 8, equipo.getDiscoGb(), estilo);
            escribir(fila, 9, equipo.getEstado(), estilo);

            Cell celdaFecha = fila.createCell(10);
            celdaFecha.setCellStyle(alterna ? estiloFechaAlterna : estiloFecha);
            if (equipo.getFechaCompra() != null) {
                celdaFecha.setCellValue(equipo.getFechaCompra());
            }
        }

        // ---------- ACABADOS ----------
        for (int i = 0; i < titulos.length; i++) {
            hoja.autoSizeColumn(i);
            // un poquito de aire extra en cada columna
            hoja.setColumnWidth(i, hoja.getColumnWidth(i) + 800);
        }
        hoja.createFreezePane(0, 2);                              // congela titulo + encabezado
        hoja.setAutoFilter(new CellRangeAddress(1, numeroFila - 1, 0, titulos.length - 1));

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        libro.write(salida);
        libro.close();
        return salida.toByteArray();
    }

    private static void ponerBordes(CellStyle estilo) {
        estilo.setBorderTop(BorderStyle.THIN);
        estilo.setBorderBottom(BorderStyle.THIN);
        estilo.setBorderLeft(BorderStyle.THIN);
        estilo.setBorderRight(BorderStyle.THIN);
    }

    private static void escribir(Row fila, int columna, String valor, CellStyle estilo) {
        Cell celda = fila.createCell(columna);
        celda.setCellStyle(estilo);
        if (valor != null) {
            celda.setCellValue(valor);
        }
    }

    private static void escribirNumero(Row fila, int columna, Integer valor, CellStyle estilo) {
        Cell celda = fila.createCell(columna);
        celda.setCellStyle(estilo);
        if (valor != null) {
            celda.setCellValue(valor);
        }
    }
}