package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Asistencia; // Entidad de movimiento.
import com.seq.sisas.modelo.Estudiante; // Entidad de alumno.
import com.seq.sisas.utilidad.ConfiguracionSistema; // Nombre y cargo de la directora desde configuración.
import com.seq.sisas.utilidad.FormatoHoras; // Formato humano de horas.
import java.awt.Color; // Colores de dibujo.
import java.awt.Dimension; // Tamaño fijo de la hoja.
import java.awt.Font; // Fuentes de texto.
import java.awt.Graphics; // Lápiz básico.
import java.awt.Graphics2D; // Lápiz avanzado con antialiasing.
import java.awt.image.BufferedImage; // Imágenes decodificadas.
import java.io.InputStream; // Lectura de recursos embebidos.
import java.util.ArrayList; // Lista filtrada de movimientos visibles.
import java.util.List; // Lista de movimientos.
import javax.imageio.ImageIO; // Decodificador de imágenes.
import javax.swing.JPanel; // Panel que se dibuja a sí mismo.

/**
 * PanelHojaReporte (V18): hoja oficial discreta.
 * Los ajustes (horas dobles o descuentos) NO se listan: se absorben en el total.
 * El detalle completo de adsorbe en el total de horas.
 * CONEXIONES:
 * - Es mostrada por com.seq.sisas.ui.DialogoVistaPrevia (pantalla e impresión).
 * - Lee recursos embebidos: /imagenes/logo_seq.png, logo_sep.png, firma_directora.png.
 * - Lee nombre y cargo de com.seq.sisas.utilidad.ConfiguracionSistema.
 */
public class PanelHojaReporte extends JPanel { // Panel con dibujo personalizado.

    public static final int ANCHO = 700; // Ancho lógico de la hoja en píxeles.
    public static final int ALTO = 960; // Alto lógico de la hoja en píxeles.
    private static final int MAX_FILAS = 24; // Máximo de movimientos que caben en la hoja.

    private Estudiante estudiante; // Alumno del reporte.
    private List<Asistencia> asistencias; // Movimientos completos (para el total real).
    private List<Asistencia> visibles; // Movimientos oficiales (sin ajustes) para listar.
    private BufferedImage logoSeq; // Logo SEQ embebido (puede ser null).
    private BufferedImage logoSep; // Logo SEP embebido (puede ser null).
    private BufferedImage firma; // Firma embebida (puede ser null).

    // Constructor: recibe alumno y movimientos, filtra ajustes y carga imágenes.
    public PanelHojaReporte(Estudiante estudiante, List<Asistencia> asistencias) { // Recibe datos del reporte.
        this.estudiante = estudiante; // Guarda al alumno.
        this.asistencias = asistencias; // Guarda todos los movimientos (incluye ajustes).
        this.visibles = new ArrayList<>(); // Lista oficial para la hoja.
        for (Asistencia a : asistencias) { // Recorre todos los movimientos.
            if (!a.getEstado().equals("AJUSTE")) { visibles.add(a); } // Solo entradas/salidas reales en la hoja oficial.
        } // Fin del filtrado discreto.
        this.logoSeq = cargar("/imagenes/Logo_Marca_Quintana_Roo.png"); // Carga logo SEQ embebido.
        this.logoSep = cargar("/imagenes/Logo_Acronimo SEQ_Servicios.png"); // Carga logo SEP embebido. Logo_Marca_Quintana_Roo.png
        this.firma = cargar("/imagenes/firma_directora.png"); // Carga firma embebida.
        setPreferredSize(new Dimension(ANCHO, ALTO)); // Tamaño fijo de hoja.
        setBackground(Color.WHITE); // Fondo blanco tipo papel.
    } // Fin del constructor.

    // Lee una imagen embebida del sistema; devuelve null si no existe.
    private BufferedImage cargar(String ruta) { // Carga recurso de classpath.
        try (InputStream is = getClass().getResourceAsStream(ruta)) { // Abre el recurso.
            if (is == null) { return null; } // Si no existe, devuelve null sin fallar.
            return ImageIO.read(is); // Decodifica la imagen.
        } catch (Exception ex) { // Ante cualquier error de lectura...
            return null; // ...devuelve null y la hoja se imprime sin esa imagen.
        } // Fin del catch.
    } // Fin de cargar.

    // Dibuja toda la hoja oficial con ajustes absorbidos en el total.
    @Override
    protected void paintComponent(Graphics g) { // Sobrescribe el dibujo del panel.
        super.paintComponent(g); // Dibuja el fondo blanco.
        Graphics2D g2 = (Graphics2D) g; // Lápiz avanzado.
        g2.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING, java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON); // Texto suave.

        // --- Encabezado con logos ---
        if (logoSep != null) { g2.drawImage(logoSep, 30, 20, 85, 85, this); } // Logo SEP a la izquierda.
        if (logoSeq != null) { g2.drawImage(logoSeq, ANCHO - 130, 20, 85, 85, this); } // Logo SEQ a la derecha.

        int xIni = 125; // Fin del logo izquierdo + margen.
        int xFin = ANCHO - 140; // Inicio del logo derecho - margen.
        g2.setColor(Color.BLACK); // Tinta negra.
        g2.setFont(new Font("Arial", Font.BOLD, 12)); // Fuente del encabezado.
        textoCentradoZona(g2, "DIRECCIÓN DE INSTITUCIONES", 45, xIni, xFin); // Línea 1, corta arriba.
        textoCentradoZona(g2, "FORMADORAS DE DOCENTES ZONA NORTE", 63, xIni, xFin); // Línea 2, larga abajo.
        g2.setFont(new Font("Arial", Font.PLAIN, 11)); // Fuente secundaria.
        textoCentradoZona(g2, "Jefatura de Gestion y Vinculacion Zona Norte", 81, xIni, xFin); // Línea 3.
        g2.setFont(new Font("Arial", Font.BOLD, 15)); // Fuente del título.
        textoCentrado(g2, "REPORTE DE SERVICIO SOCIAL", 115); // Título del documento.

        // --- Datos del alumno ---
        g2.setFont(new Font("Arial", Font.PLAIN, 11)); // Fuente de datos.
        int y = 135; // Cursor vertical inicial de datos.
        g2.drawString("Nombre: " + estudiante.getNombreCompleto(), 40, y); y += 20; // Nombre.
        g2.drawString("CURP: " + estudiante.getCurp(), 40, y); // CURP a la izquierda.
        g2.drawString("Folio: " + estudiante.getId(), 420, y); y += 20; // Folio a la derecha.
        g2.drawString("Escuela: " + estudiante.getEscuela(), 40, y); y += 20; // Escuela.
        g2.drawString("Nivel: " + (estudiante.getNivel() == null ? "" : estudiante.getNivel()), 40, y); // Nivel a la izquierda.
        g2.drawString("Tipo: " + (estudiante.getTipoServicio() == null ? "" : estudiante.getTipoServicio()), 420, y); y += 20; // Tipo a la derecha.
        g2.drawString("Matrícula: " + (estudiante.getMatricula() == null ? "" : estudiante.getMatricula()), 40, y); // Matrícula a la izquierda.
        g2.drawString("Especialidad: " + (estudiante.getEspecialidad() == null ? "" : estudiante.getEspecialidad()), 420, y); y += 20; // Especialidad a la derecha.
        g2.drawString("Fecha de ingreso: " + estudiante.getFechaIngreso(), 40, y); // Fecha ingreso.
        g2.drawString("Fecha fin: " + (estudiante.getFechaFin() == null ? "" : estudiante.getFechaFin()), 420, y); y += 20; // Fecha fin a la derecha.
        g2.drawString("Estado: " + estudiante.getEstado(), 40, y); y += 20; // Estado.
        g2.drawString("Horas requeridas: " + estudiante.getHorasRequeridas() + "h", 40, y); // Horas meta.
        g2.drawString("Horas acumuladas: " + FormatoHoras.horasMinutos(estudiante.getHorasAcumuladas()), 420, y); y += 25; // Acumulado humano (incluye ajustes, sin mostrarlos).

        // --- Tabla de movimientos oficiales (sin ajustes) ---
        int[] xs = {40, 115, 235, 355, 420}; // Posiciones X de las columnas.
        g2.setFont(new Font("Arial", Font.BOLD, 10)); // Fuente de encabezados de tabla.
        g2.drawString("Fecha", xs[0], y); // Encabezado Fecha.
        g2.drawString("Entrada", xs[1], y); // Encabezado Entrada.
        g2.drawString("Salida", xs[2], y); // Encabezado Salida.
        g2.drawString("Min", xs[3], y); // Encabezado Minutos.
        g2.drawString("Estado", xs[4], y); // Encabezado Estado.
        y += 5; // Pequeño respiro.
        g2.drawLine(40, y, ANCHO - 40, y); y += 15; // Línea bajo encabezados.

        g2.setFont(new Font("Arial", Font.PLAIN, 9)); // Fuente compacta de filas.
        int filas = Math.min(visibles.size(), MAX_FILAS); // Limita filas visibles al espacio de la hoja.
        for (int i = 0; i < filas; i++) { // Recorre solo movimientos oficiales.
            Asistencia a = visibles.get(i); // Movimiento oficial actual.
            g2.drawString(a.getFecha(), xs[0], y); // Día.
            g2.drawString(horaCorta(a.getHoraEntrada()), xs[1], y); // Solo la hora de entrada.
            g2.drawString(a.getHoraSalida() == null ? "EN CURSO" : horaCorta(a.getHoraSalida()), xs[2], y); // Hora de salida o aviso.
            g2.drawString(String.valueOf(a.getMinutos()), xs[3], y); // Minutos de la sesión.
            g2.drawString(a.getEstado(), xs[4], y); // Estado del movimiento.
            y += 16; // Avanza a la siguiente fila.
        } // Fin del recorrido de filas oficiales.
        if (visibles.size() > MAX_FILAS) { g2.drawString("* Movimientos restantes no caben en esta hoja.", 40, y); y += 16; } // Nota si hay más movimientos oficiales.

        // --- Total del periodo: se calcula con TODOS los movimientos (ajustes absorbidos) ---
        int totalMin = 0; // Acumulador de minutos reales.
        for (Asistencia a : asistencias) { totalMin += a.getMinutos(); } // Suma incluyendo ajustes: dobles y descuentos quedan discretos.
        g2.setFont(new Font("Arial", Font.BOLD, 11)); // Fuente de total.
        g2.drawString("TOTAL DEL PERIODO: " + totalMin + " min (" + FormatoHoras.horasMinutos(totalMin / 60.0) + ")", 40, y + 10); // Total humano oficial.
        g2.setFont(new Font("Arial", Font.PLAIN, 8)); // Fuente pequeña de nota al pie.
        g2.drawString("* Los ajustes se absorben en el total de horas.", 40, y + 26); // Remite al cotejo interno sin exhibir ajustes.

        // --- Bloque de firma de la directora ---
        int centroX = ANCHO / 2; // Centro horizontal de la hoja.
        if (firma != null) { g2.drawImage(firma, centroX - 110, ALTO - 165, 220, 80, this); } // Firma escaneada sobre la línea.
        g2.drawLine(centroX - 120, ALTO - 78, centroX + 120, ALTO - 78); // Línea de firma.
        g2.setFont(new Font("Arial", Font.BOLD, 11)); // Fuente del nombre.
        textoCentrado(g2, ConfiguracionSistema.obtenerNombreDirectora(), ALTO - 62); // Nombre leído de configuración.
        g2.setFont(new Font("Arial", Font.PLAIN, 10)); // Fuente del cargo.
        textoCentrado(g2, ConfiguracionSistema.obtenerCargoDirectora(), ALTO - 47); // Cargo leído de configuración.
    } // Fin de paintComponent.

    // Devuelve solo la parte de hora (HH:mm:ss) de un timestamp completo.
    private String horaCorta(String timestamp) { // Recibe timestamp completo.
        if (timestamp != null && timestamp.length() > 10) { return timestamp.substring(11); } // Corta la fecha y deja la hora.
        return timestamp; // Si es corto, lo devuelve igual.
    } // Fin de horaCorta.

    // Dibuja un texto centrado solo entre dos X (zona libre entre los logos).
    private void textoCentradoZona(Graphics2D g2, String s, int y, int xIni, int xFin) { // Centra en la zona.
        int anchoZona = xFin - xIni; // Ancho libre entre logos.
        int x = xIni + (anchoZona - g2.getFontMetrics().stringWidth(s)) / 2; // Calcula X de centrado.
        g2.drawString(s, x, y); // Dibuja centrado en la zona.
    } // Fin de textoCentradoZona.

    // Dibuja un texto centrado horizontalmente en la hoja.
    private void textoCentrado(Graphics2D g2, String s, int y) { // Centra el texto.
        int x = (ANCHO - g2.getFontMetrics().stringWidth(s)) / 2; // Calcula X de centrado.
        g2.drawString(s, x, y); // Dibuja centrado.
    } // Fin de textoCentrado.
} // Fin de la clase PanelHojaReporte.