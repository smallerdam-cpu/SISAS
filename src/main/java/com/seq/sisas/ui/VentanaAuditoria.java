package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.RegistroAuditoria; // Entidad de evento.
import com.seq.sisas.servicio.ServicioAuditoria; // Lógica de negocio.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas.
import java.awt.FlowLayout; // Fila que no estira.
import java.awt.event.ActionEvent; // Evento de clic o escritura.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Lista en memoria.
import javax.swing.JButton; // Botón.
import javax.swing.JFrame; // Ventana.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JScrollPane; // Scroll de la tabla.
import javax.swing.JTable; // Tabla visual.
import javax.swing.JTextField; // Barra de búsqueda.
import javax.swing.event.DocumentEvent; // Evento de escritura.
import javax.swing.event.DocumentListener; // Escuchador de escritura en vivo.
import javax.swing.table.DefaultTableModel; // Modelo de la tabla.

/**
 * VentanaAuditoria: historial de seguridad de solo lectura con búsqueda en vivo.
 * CONEXIONES:
 * - Es abierta desde el menú Administración de com.seq.sisas.ui.VentanaPrincipal.
 * - Usa com.seq.sisas.servicio.ServicioAuditoria.
 */
public class VentanaAuditoria extends JFrame { // Hereda de JFrame.

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_BUSCADOR = 250; // Ancho fijo de la barra de búsqueda.
    private static final int ALTO_CAMPO = 26; // Alto fijo de la barra.
    private static final int ANCHO_BOTON = 130; // Ancho fijo del botón Recargar.
    private static final int ALTO_BOTON = 30; // Alto fijo del botón.
    private static final int ALTO_FILA_TABLA = 28; // Alto de cada fila de la tabla.
    // ============================================

    private ServicioAuditoria servicio; // Lógica de negocio.
    private DefaultTableModel modelo; // Modelo de la tabla.
    private JTable tabla; // Tabla visual.
    private JTextField txtBuscar; // Barra de búsqueda.

    // Constructor: ventana de solo lectura del historial.
    public VentanaAuditoria() { // Sin dependencias externas.
        this.servicio = new ServicioAuditoria(); // Inicializa el servicio.

        setTitle("SISAS - Auditoría del Sistema"); // Título de la ventana.
        setSize(900, 500); // Tamaño de la ventana.
        setLocationRelativeTo(null); // Centra en pantalla.

        txtBuscar = new JTextField(); // Campo de búsqueda sin columnas.
        txtBuscar.setPreferredSize(new Dimension(ANCHO_BUSCADOR, ALTO_CAMPO)); // Medida fija.
        JPanel panelBusqueda = new JPanel(new FlowLayout()); // Fila centrada que no estira.
        panelBusqueda.add(new JLabel("Buscar (acción, detalle o responsable):")); // Etiqueta descriptiva.
        panelBusqueda.add(txtBuscar); // Agrega el campo.
        add(panelBusqueda, BorderLayout.NORTH); // Barra arriba.

        modelo = new DefaultTableModel() { // Modelo con celdas no editables.
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Solo lectura: la auditoría no se altera.
        }; // Fin del modelo anónimo.
        modelo.setColumnIdentifiers(new String[]{"ID", "Fecha y Hora", "Responsable", "Acción", "Detalle"}); // Encabezados.
        tabla = new JTable(modelo); // Crea la tabla.
        tabla.setRowHeight(ALTO_FILA_TABLA); // Alto de fila fijo.
        JScrollPane scroll = new JScrollPane(tabla); // Scroll para muchos eventos.
        add(scroll, BorderLayout.CENTER); // Tabla al centro, adaptable.

        JButton btnRecargar = new JButton("Recargar"); // Botón que refresca el historial.
        btnRecargar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JPanel panelBotones = new JPanel(new FlowLayout()); // Fila centrada.
        panelBotones.add(btnRecargar); // Agrega recargar.
        add(panelBotones, BorderLayout.SOUTH); // Botón abajo.

        txtBuscar.getDocument().addDocumentListener(new DocumentListener() { // Búsqueda en vivo.
            @Override public void insertUpdate(DocumentEvent e) { cargarDatos(); } // Al escribir.
            @Override public void removeUpdate(DocumentEvent e) { cargarDatos(); } // Al borrar.
            @Override public void changedUpdate(DocumentEvent e) { cargarDatos(); } // Al cambiar.
        }); // Fin del DocumentListener.

        btnRecargar.addActionListener(new ActionListener() { // Escucha clic en recargar.
            @Override
            public void actionPerformed(ActionEvent e) { cargarDatos(); } // Refresca desde la BD.
        }); // Fin del listener.

        cargarDatos(); // Carga inicial del historial.
    } // Fin del constructor.

    // Consulta la BD (con filtro si existe) y rellena la tabla del historial.
    private void cargarDatos() { // Refresca la tabla.
        try { // Intenta leer la BD.
            List<RegistroAuditoria> lista = servicio.buscar(txtBuscar.getText().trim()); // Obtiene eventos filtrados.
            modelo.setRowCount(0); // Limpia filas actuales.
            for (RegistroAuditoria r : lista) { // Recorre cada evento.
                modelo.addRow(new Object[]{r.getId(), r.getFechaHora(), r.getResponsable(), r.getAccion(), r.getDetalle()}); // Agrega fila visual.
            } // Fin del recorrido.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al cargar auditoría: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de cargarDatos.
} // Fin de la clase VentanaAuditoria.