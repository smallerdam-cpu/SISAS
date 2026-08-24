package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad del admin responsable.
import com.seq.sisas.modelo.Asistencia; // Entidad de movimiento.
import com.seq.sisas.modelo.Estudiante; // Entidad de alumno.
import com.seq.sisas.servicio.ServicioEstudiante; // Servicio para listar/buscar alumnos.
import com.seq.sisas.servicio.ServicioReportes; // Servicio de reportes y Excel.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas en píxeles.
import java.awt.FlowLayout; // Diseño en fila que no estira componentes.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Listas en memoria.
import javax.swing.JButton; // Botón.
import javax.swing.JComboBox; // Desplegable de resultados de búsqueda.
import javax.swing.JFrame; // Ventana.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JScrollPane; // Scroll para la tabla.
import javax.swing.JTable; // Tabla visual.
import javax.swing.JTextField; // Campos de texto.
import javax.swing.event.DocumentEvent; // Evento de escritura.
import javax.swing.event.DocumentListener; // Escuchador de escritura en vivo.
import javax.swing.table.DefaultTableModel; // Modelo de la tabla.

/**
 * VentanaReportes (V11): controles con medidas fijas; tabla de movimientos adaptable.
 * CONEXIONES:
 * - Es abierta por com.seq.sisas.ui.VentanaPrincipal.
 * - Usa ServicioEstudiante para buscar alumnos y llenar el combo.
 * - Usa ServicioReportes para consultar movimientos y exportar a Excel.
 */
public class VentanaReportes extends JFrame { // Hereda de JFrame.

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_BUSCADOR = 150; // Ancho fijo de la barra de búsqueda.
    private static final int ANCHO_COMBO = 220; // Ancho fijo del desplegable de estudiantes.
    private static final int ANCHO_FECHA = 90; // Ancho fijo de los campos Desde/Hasta.
    private static final int ALTO_CAMPO = 26; // Alto fijo de todos los campos.
    private static final int ANCHO_BOTON = 130; // Ancho fijo de Consultar/Exportar.
    private static final int ALTO_BOTON = 30; // Alto fijo de Consultar/Exportar.
    private static final int ALTO_FILA_TABLA = 28; // Alto de cada fila de la tabla.
    // ============================================

    private ServicioReportes servicioReportes; // Lógica de reportes.
    private ServicioEstudiante servicioEstudiante; // Lógica de alumnos.
    private List<Estudiante> listaEstudiantes; // Resultados de búsqueda en memoria.
    private List<Asistencia> listaAsistencias; // Movimientos consultados actualmente.
    private JTextField txtBusqueda; // Barra de búsqueda por nombre/apellidos/CURP.
    private JComboBox<String> comboEstudiantes; // Desplegable con coincidencias.
    private JTextField txtDesde; // Filtro de fecha inicial.
    private JTextField txtHasta; // Filtro de fecha final.
    private DefaultTableModel modelo; // Modelo de la tabla visual.
    private JTable tabla; // Tabla de movimientos.
    private JLabel lblTotal; // Etiqueta con el total de horas del periodo.

    // Constructor: recibe al administrador responsable.
    public VentanaReportes(Administrador admin) { // Recibe al admin.
        this.servicioReportes = new ServicioReportes(); // Inicializa servicio de reportes.
        this.servicioEstudiante = new ServicioEstudiante(); // Inicializa servicio de alumnos.

        setTitle("SISAS - Reportes y Excel"); // Título de la ventana.
        setSize(1200, 800); // Tamaño inicial de la ventana.
        setLocationRelativeTo(null); // Centra en pantalla.

        // --- Controles superiores con medida fija ---
        txtBusqueda = new JTextField(); // Barra de búsqueda sin columnas.
        txtBusqueda.setPreferredSize(new Dimension(ANCHO_BUSCADOR, ALTO_CAMPO)); // Medida fija.
        comboEstudiantes = new JComboBox<>(); // Combo de coincidencias.
        comboEstudiantes.setPreferredSize(new Dimension(ANCHO_COMBO, ALTO_CAMPO)); // Medida fija.
        txtDesde = new JTextField(); // Campo fecha inicial.
        txtDesde.setPreferredSize(new Dimension(ANCHO_FECHA, ALTO_CAMPO)); // Medida fija.
        txtHasta = new JTextField(); // Campo fecha final.
        txtHasta.setPreferredSize(new Dimension(ANCHO_FECHA, ALTO_CAMPO)); // Medida fija.
        lblTotal = new JLabel("Total del periodo: -"); // Etiqueta de total inicial.

        actualizarCombo(); // Carga inicial: muestra todos los alumnos.

        JButton btnConsultar = new JButton("Consultar"); // Botón de consulta.
        btnConsultar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnExportar = new JButton("Exportar Excel"); // Botón de exportación.
        btnExportar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnVistaPrevia = new JButton("Vista Previa"); // Botón que abre la hoja oficial.
        btnVistaPrevia.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.

        JPanel panelSuperior = new JPanel(new FlowLayout()); // Fila que no estira componentes.
        panelSuperior.add(new JLabel("Buscar:")); // Etiqueta de búsqueda.
        panelSuperior.add(txtBusqueda); // Agrega barra de búsqueda.
        panelSuperior.add(new JLabel("Estudiante:")); // Etiqueta del combo.
        panelSuperior.add(comboEstudiantes); // Agrega combo de coincidencias.
        panelSuperior.add(new JLabel("Desde:")); // Etiqueta fecha inicial.
        panelSuperior.add(txtDesde); // Agrega campo desde.
        panelSuperior.add(new JLabel("Hasta:")); // Etiqueta fecha final.
        panelSuperior.add(txtHasta); // Agrega campo hasta.
        panelSuperior.add(btnConsultar); // Agrega botón consultar.
        panelSuperior.add(btnExportar); // Agrega botón exportar.
        panelSuperior.add(btnVistaPrevia); // Agrega botón de vista previa.

        // --- Tabla de movimientos (zona de datos: sí se adapta a la ventana) ---
        modelo = new DefaultTableModel() { // Modelo con celdas no editables.
            @Override
            public boolean isCellEditable(int row, int column) { // Sobrescribe edición.
                return false; // Solo lectura visual.
            } // Fin de isCellEditable.
        }; // Fin del modelo anónimo.
        modelo.setColumnIdentifiers(new String[]{"Fecha", "Entrada", "Salida", "Minutos", "Estado"}); // Encabezados.
        tabla = new JTable(modelo); // Crea la tabla.
        tabla.setRowHeight(ALTO_FILA_TABLA); // Alto de fila fijo para lectura cómoda.
        JScrollPane scroll = new JScrollPane(tabla); // Scroll para muchos registros.

        add(panelSuperior, BorderLayout.NORTH); // Controles arriba.
        add(scroll, BorderLayout.CENTER); // Tabla al centro, adaptable.
        add(lblTotal, BorderLayout.SOUTH); // Total abajo.

        // La búsqueda recarga el combo de estudiantes en vivo con cada tecla.
        txtBusqueda.getDocument().addDocumentListener(new DocumentListener() { // Listener de escritura.
            @Override public void insertUpdate(DocumentEvent e) { actualizarCombo(); } // Al escribir.
            @Override public void removeUpdate(DocumentEvent e) { actualizarCombo(); } // Al borrar.
            @Override public void changedUpdate(DocumentEvent e) { actualizarCombo(); } // Al cambiar.
        }); // Fin del DocumentListener.

        btnConsultar.addActionListener(new ActionListener() { // Escucha clic en consultar.
            @Override
            public void actionPerformed(ActionEvent e) { consultar(); } // Ejecuta la consulta.
        }); // Fin del listener.

        btnExportar.addActionListener(new ActionListener() { // Escucha clic en exportar.
            @Override
            public void actionPerformed(ActionEvent e) { exportar(); } // Genera el Excel.
        }); // Fin del listener.
                btnVistaPrevia.addActionListener(new ActionListener() { // Escucha clic en vista previa.
            @Override
            public void actionPerformed(ActionEvent e) { abrirVistaPrevia(); } // Abre el visor oficial.
        }); // Fin del listener.
    } // Fin del constructor.

    // Rellena el combo con todos los alumnos o con las coincidencias de la búsqueda.
    private void actualizarCombo() { // Recarga el combo.
        try { // Intenta leer la BD.
            String filtro = txtBusqueda.getText().trim(); // Lee el criterio.
            if (filtro.isEmpty()) { // Si no hay texto...
                listaEstudiantes = servicioEstudiante.listarEstudiantes(); // ...carga todos.
            } else { // Si hay texto...
                listaEstudiantes = servicioEstudiante.buscarEstudiantes(filtro); // ...carga coincidencias.
            } // Fin de evaluación de filtro.
            comboEstudiantes.removeAllItems(); // Limpia el combo.
            for (Estudiante e : listaEstudiantes) { // Recorre resultados.
                comboEstudiantes.addItem(e.getNombreCompleto() + " [" + e.getCurp() + "]"); // Muestra nombre y CURP en la opción.
            } // Fin del recorrido.
            if (comboEstudiantes.getItemCount() > 0) { comboEstudiantes.setSelectedIndex(0); } // Selecciona el primero si hay resultados.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al buscar estudiantes: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de actualizarCombo.

    // Devuelve el estudiante seleccionado en el combo, o null si no hay.
    private Estudiante obtenerSeleccionado() { // Obtiene selección del combo.
        if (listaEstudiantes == null || listaEstudiantes.isEmpty()) { return null; } // No hay resultados.
        int indice = comboEstudiantes.getSelectedIndex(); // Índice seleccionado.
        if (indice < 0 || indice >= listaEstudiantes.size()) { return null; } // Protección de índices.
        return listaEstudiantes.get(indice); // Devuelve el objeto sincronizado.
    } // Fin de obtenerSeleccionado.

    // Consulta los movimientos según filtros y rellena la tabla.
    private void consultar() { // Ejecuta la consulta.
        Estudiante sel = obtenerSeleccionado(); // Obtiene alumno del combo.
        if (sel == null) { // Si no hay alumnos...
            JOptionPane.showMessageDialog(this, "No hay estudiantes que coincidan con la busqueda.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return; // Corta ejecución.
        } // Fin de validación.
        try { // Intenta consultar la BD.
            listaAsistencias = servicioReportes.obtenerAsistencias(sel.getId(), txtDesde.getText().trim(), txtHasta.getText().trim()); // Consulta con filtros.
            modelo.setRowCount(0); // Limpia la tabla visual.
            int totalMinutos = 0; // Acumulador de minutos.
            for (Asistencia a : listaAsistencias) { // Recorre cada movimiento.
                modelo.addRow(new Object[]{a.getFecha(), a.getHoraEntrada(), a.getHoraSalida() == null ? "EN CURSO" : a.getHoraSalida(), a.getMinutos(), a.getEstado()}); // Agrega fila visual.
                totalMinutos += a.getMinutos(); // Suma minutos.
            } // Fin del recorrido.
            lblTotal.setText("Total del periodo: " + totalMinutos + " min (" + String.format("%.2f", totalMinutos / 60.0) + " hrs)"); // Muestra total.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al consultar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de consultar.

    // Genera el archivo Excel con la consulta actual.
    private void exportar() { // Ejecuta la exportación.
        Estudiante sel = obtenerSeleccionado(); // Obtiene alumno del combo.
        if (sel == null || listaAsistencias == null || listaAsistencias.isEmpty()) { // Si no hay consulta previa...
            JOptionPane.showMessageDialog(this, "Primero presione Consultar para generar datos.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return; // Corta ejecución.
        } // Fin de validación.
        try { // Intenta generar el archivo.
            String ruta = servicioReportes.exportarExcel(sel, listaAsistencias); // Llama al servicio con POI.
            JOptionPane.showMessageDialog(this, "Reporte generado en:\n" + ruta, "Éxito", JOptionPane.INFORMATION_MESSAGE); // Muestra ruta.
        } catch (Exception ex) { // Captura cualquier error.
            JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de exportar.

        // Abre la vista previa con el alumno y los movimientos consultados.
    private void abrirVistaPrevia() { // Valida y abre el visor.
        Estudiante sel = obtenerSeleccionado(); // Obtiene alumno del combo.
        if (sel == null || listaAsistencias == null || listaAsistencias.isEmpty()) { // Si no hay consulta previa...
            JOptionPane.showMessageDialog(this, "Primero presione Consultar para generar la hoja.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return; // Corta ejecución.
        } // Fin de validación.
        new DialogoVistaPrevia(this, sel, listaAsistencias).setVisible(true); // Abre la hoja oficial.
    } // Fin de abrirVistaPrevia.
} // Fin de la clase VentanaReportes.