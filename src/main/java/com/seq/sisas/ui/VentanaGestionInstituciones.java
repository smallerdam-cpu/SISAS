package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad del admin logueado.
import com.seq.sisas.modelo.Institucion; // Entidad plantel.
import com.seq.sisas.servicio.ServicioInstitucion; // Lógica de negocio.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas.
import java.awt.FlowLayout; // Fila que no estira.
import java.awt.GridLayout; // Formulario de 2 filas.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Lista en memoria.
import javax.swing.JButton; // Botón.
import javax.swing.JComboBox; // Lista desplegable.
import javax.swing.JFrame; // Ventana.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JScrollPane; // Scroll de la tabla.
import javax.swing.JTable; // Tabla visual.
import javax.swing.JTextField; // Campo de texto.
import javax.swing.table.DefaultTableModel; // Modelo de la tabla.

/**
 * VentanaGestionInstituciones: tabla de prepas y universidades con crear por
 * nombre y nivel.
 * CONEXIONES:
 * - Se abrirá desde el menú Gestión de com.seq.sisas.ui.VentanaPrincipal (paso
 * posterior).
 * - Usa com.seq.sisas.servicio.ServicioInstitucion.
 */
public class VentanaGestionInstituciones extends JFrame { // Hereda de JFrame.
    private static final int ANCHO_BOTON = 170; // Ancho fijo de cada botón.
    private static final int ALTO_BOTON = 30; // Alto fijo de cada botón.
    private static final int ALTO_FILA_TABLA = 28; // Alto de cada fila de la tabla.
    private ServicioInstitucion servicio; // Lógica de negocio.
    private Administrador adminLogueado; // Admin responsable que firma las acciones.
    private DefaultTableModel modelo; // Modelo de la tabla.
    private JTable tabla; // Tabla visual.
    private List<Institucion> listaActual; // Lista en memoria sincronizada con la tabla.
    // Constructor: recibe al administrador logueado.

    public VentanaGestionInstituciones(Administrador admin) { // Recibe al admin.
        this.adminLogueado = admin; // Guarda al responsable.
        this.servicio = new ServicioInstitucion(); // Inicializa el servicio.
        setTitle("SISAS - Gestión de Prepas y Universidades"); // Título de la ventana.
        setSize(650, 400); // Tamaño de la ventana.
        setLocationRelativeTo(null); // Centra en pantalla.
        modelo = new DefaultTableModel() { // Modelo con celdas no editables.
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            } // Solo lectura visual.
        }; // Fin del modelo anónimo.
        modelo.setColumnIdentifiers(new String[] { "ID", "Nombre", "Nivel", "Activo" }); // Encabezados.
        tabla = new JTable(modelo); // Crea la tabla.
        tabla.setRowHeight(ALTO_FILA_TABLA); // Alto de fila fijo.
        JScrollPane scroll = new JScrollPane(tabla); // Scroll para la tabla.
        add(scroll, BorderLayout.CENTER); // Tabla al centro.
        JButton btnNuevo = new JButton("Nueva Institución"); // Crea plantel.
        btnNuevo.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnCerrar = new JButton("Cerrar"); // Cierra la ventana.
        btnCerrar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JPanel panelBotones = new JPanel(new FlowLayout()); // Fila centrada.
        panelBotones.add(btnNuevo); // Agrega nuevo.
        panelBotones.add(btnCerrar); // Agrega cerrar.
        add(panelBotones, BorderLayout.SOUTH); // Botones abajo.
        btnNuevo.addActionListener(new ActionListener() { // Clic en Nuevo.
            @Override
            public void actionPerformed(ActionEvent e) {
                crearInstitucion();
            } // Abre formulario de creación.
        }); // Fin del listener.
        btnCerrar.addActionListener(new ActionListener() { // Clic en Cerrar.
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            } // Cierra la ventana.
        }); // Fin del listener.
        cargarDatos(); // Carga inicial de la tabla.
    } // Fin del constructor.
      // Pide nombre y nivel en un diálogo simple y crea la institución.

    private void crearInstitucion() { // Creación rápida.
        JTextField txtNombre = new JTextField(); // Campo nombre.
        JComboBox<String> cmbNivel = new JComboBox<>(new String[] { "PREPA", "UNIVERSIDAD" }); // Lista de nivel.
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5)); // Formulario 2x2.
        panel.add(new JLabel("Nombre (*):")); // Etiqueta nombre.
        panel.add(txtNombre); // Campo nombre.
        panel.add(new JLabel("Nivel (*):")); // Etiqueta nivel.
        panel.add(cmbNivel); // Lista nivel.
        int opcion = JOptionPane.showConfirmDialog(this, panel, "Nueva Institución", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE); // Diálogo con formulario.
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        } // Corta si canceló.
        try { // Intenta crear.
            servicio.crear(txtNombre.getText(), (String) cmbNivel.getSelectedItem(), adminLogueado.getId()); // Valida,
                                                                                                             // guarda y
                                                                                                             // firma.
            JOptionPane.showMessageDialog(this, "Institución creada correctamente.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE); // Confirma.
            cargarDatos(); // Refresca la tabla.
        } catch (Exception ex) { // Captura cualquier error.
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra
                                                                                                                  // error.
        } // Fin del catch.
    } // Fin de crearInstitucion.
      // Consulta la BD y rellena la tabla de instituciones.

    private void cargarDatos() { // Refresca la tabla.
        try { // Intenta leer la BD.
            listaActual = servicio.listar(); // Obtiene todas las instituciones.
            modelo.setRowCount(0); // Limpia filas actuales.
            for (Institucion inst : listaActual) { // Recorre cada institución.
                modelo.addRow(new Object[] { inst.getId(), inst.getNombre(), inst.getNivel(),
                        inst.getActivo() == 1 ? "SÍ" : "NO" }); // Agrega fila visual.
            } // Fin del recorrido.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al cargar instituciones: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de cargarDatos.
} // Fin de la clase VentanaGestionInstituciones.