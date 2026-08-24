package com.seq.sisas.servicio; // Paquete de lógica de negocio.

import com.seq.sisas.modelo.Asistencia; // Entidad de movimiento.
import com.seq.sisas.modelo.Estudiante; // Entidad de alumno.
import com.seq.sisas.repositorio.AsistenciaDAO; // DAO de movimientos.
//import com.seq.sisas.ui.PanelHojaReporte; // Constantes oficiales de nombre y cargo de la directora.
import com.seq.sisas.utilidad.ConfiguracionSistema;

import java.io.File; // Manejo de carpetas en disco.
import java.io.FileOutputStream; // Escritura del archivo .xlsx en disco.
import java.io.InputStream; // Lectura de recursos embebidos (imágenes).
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Listas de Java.
import org.apache.poi.ss.usermodel.CellStyle; // Estilo de celda (alineación).
import org.apache.poi.ss.usermodel.ClientAnchor; // Ancla que posiciona la imagen en la hoja.
import org.apache.poi.ss.usermodel.CreationHelper; // Fábrica de anclas de POI.
import org.apache.poi.ss.usermodel.Drawing; // Lienzo de dibujos de la hoja.
import org.apache.poi.ss.usermodel.Font; // Fuente de celda (negritas).
import org.apache.poi.ss.usermodel.HorizontalAlignment; // Alineación horizontal.
import org.apache.poi.ss.usermodel.Workbook; // Constantes de tipo de imagen.
import org.apache.poi.ss.util.CellRangeAddress; // Rango para combinar celdas.
import org.apache.poi.xssf.usermodel.XSSFCell; // Celda de hoja .xlsx.
import org.apache.poi.xssf.usermodel.XSSFRow; // Fila de hoja .xlsx.
import org.apache.poi.xssf.usermodel.XSSFSheet; // Hoja de .xlsx.
import org.apache.poi.xssf.usermodel.XSSFWorkbook; // Libro completo .xlsx.

/**
 * ServicioReportes (V12): consulta historiales y genera el Excel oficial con logos y firma.
 * CONEXIONES:
 * - Usa com.seq.sisas.repositorio.AsistenciaDAO para leer movimientos.
 * - Lee recursos embebidos /imagenes/logo_sep.png, logo_seq.png y firma_directora.png.
 * - Usa las constantes de com.seq.sisas.ui.PanelHojaReporte (nombre y cargo de la directora).
 * - Es invocado por com.seq.sisas.ui.VentanaReportes.
 */
public class ServicioReportes { // Clase de servicio.

    private AsistenciaDAO asistenciaDAO; // DAO de movimientos.

    public ServicioReportes() { // Constructor.
        this.asistenciaDAO = new AsistenciaDAO(); // Inicializa el DAO.
    } // Fin del constructor.

    // Devuelve los movimientos del alumno según el rango de fechas.
    public List<Asistencia> obtenerAsistencias(int estudianteId, String desde, String hasta) throws SQLException { // Lanza error si falla.
        return asistenciaDAO.obtenerPorEstudiante(estudianteId, desde, hasta); // Delega al DAO.
    } // Fin de obtenerAsistencias.

    // Genera el .xlsx oficial: logos arriba, datos, tabla, total y firma con nombre centrado.
    public String exportarExcel(Estudiante est, List<Asistencia> lista) throws Exception { // Lanza Exception si falla I/O o POI.

        File carpeta = new File("datos/reportes"); // Carpeta destino de reportes.
        if (!carpeta.exists()) { carpeta.mkdirs(); } // La crea si no existe.
        String ruta = "datos/reportes/reporte_" + est.getCodigoUnico() + ".xlsx"; // Nombre único por alumno.

        try (XSSFWorkbook libro = new XSSFWorkbook()) { // Crea el libro Excel en memoria.
            XSSFSheet hoja = libro.createSheet("Servicio Social"); // Crea la hoja con nombre.

            for (int i = 0; i < 10; i++) { hoja.setColumnWidth(i, 256 * 12); } // Ancho uniforme de 12 caracteres por columna.

            // --- Logos institucionales anclados arriba ---
            agregarImagen(hoja, libro, "/imagenes/logo_sep", 0, 0, 3, 5); // Logo SEP arriba a la izquierda.
            agregarImagen(hoja, libro, "/imagenes/logo_seq", 7, 0, 10, 5); // Logo SEQ arriba a la derecha.

            CellStyle centro = estiloCentro(libro, false); // Estilo centrado normal.
            CellStyle centroNegrita = estiloCentro(libro, true); // Estilo centrado en negritas.

            // --- Encabezados centrados debajo de los logos ---
            filaCentrada(hoja, centroNegrita, 6, "SERVICIOS EDUCATIVOS DE QUINTANA ROO"); // Línea institucional 1.
            filaCentrada(hoja, centro, 7, "Departamento de Educación Media Superior y Sperior"); // Línea institucional 2.
            filaCentrada(hoja, centroNegrita, 8, "REPORTE DE SERVICIO SOCIAL"); // Título del documento.

            // --- Datos del alumno ---
            int numFila = 10; // Cursor de filas tras el encabezado.
            hoja.createRow(numFila++).createCell(0).setCellValue("Nombre: " + est.getNombreCompleto()); // Nombre.
            XSSFRow filaCurp = hoja.createRow(numFila++); // Fila de CURP y folio.
            filaCurp.createCell(0).setCellValue("CURP: " + est.getCurp()); // CURP a la izquierda.
            filaCurp.createCell(5).setCellValue("Folio: " + est.getId()); // Folio a la derecha.
            hoja.createRow(numFila++).createCell(0).setCellValue("Escuela: " + est.getEscuela()); // Escuela.
            XSSFRow filaFechas = hoja.createRow(numFila++); // Fila de ingreso y estado.
            filaFechas.createCell(0).setCellValue("Fecha de ingreso: " + est.getFechaIngreso()); // Fecha de ingreso.
            filaFechas.createCell(5).setCellValue("Estado: " + est.getEstado()); // Estado actual.
            XSSFRow filaHoras = hoja.createRow(numFila++); // Fila de horas.
            filaHoras.createCell(0).setCellValue("Horas requeridas: " + est.getHorasRequeridas() + "h"); // Meta de horas.
            filaHoras.createCell(5).setCellValue("Horas acumuladas: " + String.format("%.2f", est.getHorasAcumuladas()) + "h"); // Acumulado.
            numFila++; // Fila en blanco de separación.

            // --- Tabla de movimientos ---
            XSSFRow encabezado = hoja.createRow(numFila++); // Fila de encabezados de tabla.
            encabezado.createCell(0).setCellValue("Fecha"); // Columna 0.
            encabezado.createCell(1).setCellValue("Entrada"); // Columna 1.
            encabezado.createCell(2).setCellValue("Salida"); // Columna 2.
            encabezado.createCell(3).setCellValue("Minutos"); // Columna 3.
            encabezado.createCell(4).setCellValue("Estado"); // Columna 4.

            int totalMinutos = 0; // Acumulador para el total del periodo.
            for (Asistencia a : lista) { // Recorre cada movimiento.
                XSSFRow fila = hoja.createRow(numFila++); // Crea la fila del movimiento.
                fila.createCell(0).setCellValue(a.getFecha()); // Día del movimiento.
                fila.createCell(1).setCellValue(a.getHoraEntrada()); // Timestamp de entrada.
                fila.createCell(2).setCellValue(a.getHoraSalida() == null ? "EN CURSO" : a.getHoraSalida()); // Salida o sesión abierta.
                fila.createCell(3).setCellValue(a.getMinutos()); // Minutos de la sesión.
                fila.createCell(4).setCellValue(a.getEstado()); // Estado del movimiento.
                totalMinutos += a.getMinutos(); // Suma al total del periodo.
            } // Fin del recorrido.

            numFila++; // Fila en blanco antes del total.
            XSSFRow filaTotal = hoja.createRow(numFila++); // Fila de total.
            filaTotal.createCell(0).setCellValue("TOTAL DEL PERIODO"); // Etiqueta de total.
            filaTotal.createCell(3).setCellValue(totalMinutos); // Total de minutos.
            filaTotal.createCell(4).setCellValue(String.format("%.2f", totalMinutos / 60.0) + " hrs"); // Total en horas.
            numFila += 2; // Espacio en blanco antes de la firma.

            // --- Firma de la directora con nombre y cargo centrados ---
            agregarImagen(hoja, libro, "/imagenes/firma_directora", 3, numFila, 7, numFila + 6); // Firma escaneada sobre la línea de nombre.
            filaCentrada(hoja, centroNegrita, numFila + 7, ConfiguracionSistema.obtenerNombreDirectora()); // Nombre leído de configuración.
            filaCentrada(hoja, centro, numFila + 8, ConfiguracionSistema.obtenerCargoDirectora()); // Cargo leído de configuración.
            try (FileOutputStream out = new FileOutputStream(ruta)) { // Abre el archivo destino en disco.
                libro.write(out); // Escribe el libro Excel al archivo.
            } // Cierra el flujo de salida.
        } // Cierra el libro de POI.

        return ruta; // Devuelve la ruta para mostrarla al administrador.
    } // Fin de exportarExcel.

    // Lee un recurso embebido del sistema como arreglo de bytes; null si no existe.
    private byte[] leerRecurso(String rutaRecurso) { // Carga imagen del classpath.
        try (InputStream is = getClass().getResourceAsStream(rutaRecurso)) { // Abre el recurso.
            if (is == null) { return null; } // Si no existe, devuelve null sin fallar.
            return is.readAllBytes(); // Convierte el recurso completo a bytes.
        } catch (Exception ex) { // Ante cualquier error de lectura...
            return null; // ...devuelve null y el Excel se genera sin esa imagen.
        } // Fin del catch.
    } // Fin de leerRecurso.

    // Ancla una imagen embebida (png o jpg) en un rango de celdas de la hoja.
    private void agregarImagen(XSSFSheet hoja, XSSFWorkbook libro, String baseRuta, int col1, int row1, int col2, int row2) { // Posiciona la imagen.
        byte[] bytes = leerRecurso(baseRuta + ".png"); // Intenta cargar como PNG.
        int tipo = Workbook.PICTURE_TYPE_PNG; // Tipo por defecto PNG.
        if (bytes == null) { bytes = leerRecurso(baseRuta + ".jpg"); tipo = Workbook.PICTURE_TYPE_JPEG; } // Si no, intenta JPG.
        if (bytes == null) { return; } // Si no existe ninguna, sale sin fallar.
        int indice = libro.addPicture(bytes, tipo); // Registra la imagen en el libro.
        CreationHelper helper = libro.getCreationHelper(); // Fábrica de anclas.
        Drawing<?> lienzo = hoja.createDrawingPatriarch(); // Lienzo de dibujos de la hoja.
        ClientAnchor ancla = helper.createClientAnchor(); // Crea el ancla de posición.
        ancla.setCol1(col1); // Columna inicial.
        ancla.setRow1(row1); // Fila inicial.
        ancla.setCol2(col2); // Columna final (define el ancho).
        ancla.setRow2(row2); // Fila final (define el alto).
        lienzo.createPicture(ancla, indice); // Dibuja la imagen anclada en la hoja.
    } // Fin de agregarImagen.

    // Crea un estilo de celda centrado, con negritas opcional.
    private CellStyle estiloCentro(XSSFWorkbook libro, boolean negrita) { // Fábrica de estilos.
        CellStyle estilo = libro.createCellStyle(); // Crea el estilo.
        estilo.setAlignment(HorizontalAlignment.CENTER); // Centra el contenido.
        if (negrita) { // Si se pide negrita...
            Font fuente = libro.createFont(); // Crea la fuente.
            fuente.setBold(true); // Activa negritas.
            estilo.setFont(fuente); // Asigna la fuente al estilo.
        } // Fin de validación de negrita.
        return estilo; // Devuelve el estilo listo.
    } // Fin de estiloCentro.

    // Escribe un texto centrado combinando las columnas 0 a 9 de una fila.
    private void filaCentrada(XSSFSheet hoja, CellStyle estilo, int fila, String texto) { // Fila oficial centrada.
        hoja.addMergedRegion(new CellRangeAddress(fila, fila, 0, 9)); // Combina las 10 columnas de la fila.
        XSSFRow r = hoja.createRow(fila); // Crea la fila.
        XSSFCell c = r.createCell(0); // Crea la celda inicial del rango combinado.
        c.setCellValue(texto); // Escribe el texto oficial.
        c.setCellStyle(estilo); // Aplica el estilo centrado.
    } // Fin de filaCentrada.
} // Fin de la clase ServicioReportes.