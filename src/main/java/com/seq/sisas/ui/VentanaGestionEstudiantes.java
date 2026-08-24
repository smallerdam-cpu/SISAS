package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad del admin logueado.
import com.seq.sisas.modelo.Estudiante; // Entidad alumno.
import com.seq.sisas.servicio.ServicioEstudiante; // Lógica de negocio.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas en píxeles.
import java.awt.FlowLayout; // Diseño en fila que no estira componentes.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.io.File; // Verificación de archivo QR en disco.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Lista de estudiantes en memoria.
import javax.swing.ImageIcon; // Carga la imagen PNG del QR.
import javax.swing.JButton; // Botón.
import javax.swing.JFrame; // Ventana.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JScrollPane; // Barra de desplazamiento.
import javax.swing.JTable; // Tabla visual.
import javax.swing.JTextField; // Barra de búsqueda.
import javax.swing.event.DocumentEvent; // Evento de escritura.
import javax.swing.event.DocumentListener; // Escuchador de escritura en vivo.
import javax.swing.table.DefaultTableModel; // Modelo de la tabla.
import com.seq.sisas.utilidad.FormatoHoras; // Formato humano de horas acumuladas.
import javax.swing.JMenuItem; // Opción del menú de puntos.
import javax.swing.JPopupMenu; // Menú contextual de 3 puntos.

/**
 * VentanaGestionEstudiantes (V11): búsqueda y botones fijos; tabla adaptable.
 * CONEXIONES:
 * - Es abierta por com.seq.sisas.ui.VentanaPrincipal.
 * - Usa ServicioEstudiante para listar, buscar, crear, editar, cambiar estado y eliminar.
 * - Abre com.seq.sisas.ui.DialogoEstudiante para crear/editar.
 */
public class VentanaGestionEstudiantes extends JFrame { // Hereda de JFrame.

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_BUSCADOR = 350; // Ancho fijo de la barra de búsqueda.
    private static final int ALTO_CAMPO = 26; // Alto fijo de la barra de búsqueda.
    private static final int ANCHO_BOTON = 130; // Ancho fijo de cada botón de acción.
    private static final int ALTO_BOTON = 30; // Alto fijo de cada botón de acción.
    private static final int ALTO_FILA_TABLA = 28; // Alto de cada fila de la tabla (legibilidad).
    // ============================================

    private ServicioEstudiante servicio; // Lógica de negocio.
    private Administrador adminLogueado; // Admin responsable de las acciones.
    private DefaultTableModel modelo; // Modelo de datos de la tabla.
    private JTable tabla; // Tabla visual.
    private JTextField txtBuscar; // Barra de búsqueda por nombre/apellidos/CURP.
    private List<Estudiante> listaActual; // Lista en memoria sincronizada con la tabla.

    // Constructor: recibe al administrador logueado.
    public VentanaGestionEstudiantes(Administrador admin) { // Recibe al admin.
        this.adminLogueado = admin; // Guarda al admin.
        this.servicio = new ServicioEstudiante(); // Inicializa el servicio.

        setTitle("SISAS - Gestión de Estudiantes"); // Título de la ventana.
        setSize(850, 500); // Tamaño inicial de la ventana.
        setLocationRelativeTo(null); // Centra en pantalla.

        // --- Barra de búsqueda superior con medida fija ---
        txtBuscar = new JTextField(); // Campo de búsqueda sin columnas.
        txtBuscar.setPreferredSize(new Dimension(ANCHO_BUSCADOR, ALTO_CAMPO)); // Medida fija independiente de la ventana.
        JPanel panelBusqueda = new JPanel(new FlowLayout()); // Fila centrada que no estira.
        panelBusqueda.add(new JLabel("Buscar (nombre, apellidos o CURP):")); // Etiqueta descriptiva.
        panelBusqueda.add(txtBuscar); // Agrega el campo de búsqueda.
        add(panelBusqueda, BorderLayout.NORTH); // Barra arriba.

        // Modelo de tabla con celdas no editables.
        modelo = new DefaultTableModel() { // Subclase anónima del modelo.
            @Override
            public boolean isCellEditable(int row, int column) { // Sobrescribe edición.
                return false; // Solo lectura visual.
            } // Fin de isCellEditable.
        }; // Fin del modelo anónimo.
        modelo.setColumnIdentifiers(new String[]{"Folio", "Nombre", "Escuela", "CURP", "Código QR", "Horas", "Estado"}); // Encabezados con Folio oficial.

        tabla = new JTable(modelo); // Crea la tabla con el modelo.
        tabla.setRowHeight(ALTO_FILA_TABLA); // Alto de fila fijo para lectura cómoda.
        JScrollPane scroll = new JScrollPane(tabla); // Barra de desplazamiento.
        add(scroll, BorderLayout.CENTER); // La tabla SÍ se adapta a la ventana (es la zona de datos).

        // --- Botones de acción con medida fija ---
        JButton btnNuevo = new JButton("Nuevo"); // Crea estudiante.
        btnNuevo.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnEditar = new JButton("Editar"); // Modifica seleccionado.
        btnEditar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnEstado = new JButton("Cambiar Estado"); // Activa/inactiva.
        btnEstado.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnVerQR = new JButton("Ver QR"); // Muestra la imagen.
        btnVerQR.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnEliminar = new JButton("Eliminar"); // Borra todo el rastro.
        btnEliminar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnMenu = new JButton("⋮"); // Botón de tres puntos con opciones adicionales.
        btnMenu.setPreferredSize(new Dimension(50, ALTO_BOTON)); // Medida fija pequeña.
        JPopupMenu menuPuntos = new JPopupMenu(); // Menú contextual desplegable.
        JMenuItem itemAjuste = new JMenuItem("Ajustar horas (agregar/quitar)"); // Opción de ajuste manual.
        menuPuntos.add(itemAjuste); // Agrega la opción al menú de puntos.

        JPanel panelBotones = new JPanel(new FlowLayout()); // Fila centrada que no estira.
        panelBotones.add(btnNuevo); // Agrega nuevo.
        panelBotones.add(btnEditar); // Agrega editar.
        panelBotones.add(btnEstado); // Agrega estado.
        panelBotones.add(btnVerQR); // Agrega ver QR.
        panelBotones.add(btnEliminar); // Agrega eliminar.
        panelBotones.add(btnMenu); // Agrega el botón de 3 puntos al final.
        add(panelBotones, BorderLayout.SOUTH); // Botones abajo.

        // La búsqueda filtra la tabla en vivo con cada tecla escrita.
        txtBuscar.getDocument().addDocumentListener(new DocumentListener() { // Listener de escritura.
            @Override public void insertUpdate(DocumentEvent e) { cargarDatos(); } // Al escribir.
            @Override public void removeUpdate(DocumentEvent e) { cargarDatos(); } // Al borrar.
            @Override public void changedUpdate(DocumentEvent e) { cargarDatos(); } // Al cambiar.
        }); // Fin del DocumentListener.

        btnNuevo.addActionListener(new ActionListener() { // Clic en Nuevo.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                new DialogoEstudiante(VentanaGestionEstudiantes.this, servicio, adminLogueado.getId(), null).setVisible(true); // Abre formulario de creación.
                cargarDatos(); // Refresca la tabla respetando el filtro.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        btnEditar.addActionListener(new ActionListener() { // Clic en Editar.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                Estudiante sel = obtenerSeleccionado(); // Obtiene la fila seleccionada.
                if (sel != null) { // Si hay selección válida...
                    new DialogoEstudiante(VentanaGestionEstudiantes.this, servicio, adminLogueado.getId(), sel).setVisible(true); // Abre formulario de edición.
                    cargarDatos(); // Refresca la tabla.
                } // Fin de validación.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        btnEstado.addActionListener(new ActionListener() { // Clic en Cambiar Estado.
            @Override
            public void actionPerformed(ActionEvent e) { cambiarEstado(); } // Ejecuta lógica de estado.
        }); // Fin del listener.

        btnVerQR.addActionListener(new ActionListener() { // Clic en Ver QR.
            @Override
            public void actionPerformed(ActionEvent e) { verQR(); } // Muestra la imagen del QR.
        }); // Fin del listener.

        btnEliminar.addActionListener(new ActionListener() { // Clic en Eliminar.
            @Override
            public void actionPerformed(ActionEvent e) { eliminarSeleccionado(); } // Ejecuta borrado seguro.
        }); // Fin del listener.

                btnMenu.addActionListener(new ActionListener() { // Escucha clic en los 3 puntos.
            @Override
            public void actionPerformed(ActionEvent e) { menuPuntos.show(btnMenu, 0, btnMenu.getHeight()); } // Despliega el menú bajo el botón.
        }); // Fin del listener.

        itemAjuste.addActionListener(new ActionListener() { // Escucha clic en ajustar horas.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                Estudiante sel = obtenerSeleccionado(); // Obtiene la fila seleccionada.
                if (sel != null) { // Si hay selección válida...
                    new DialogoAjusteHoras(VentanaGestionEstudiantes.this, sel, adminLogueado, servicio).setVisible(true); // Abre el diálogo con contraseña.
                    cargarDatos(); // Refresca la tabla tras el ajuste.
                } // Fin de validación.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        cargarDatos(); // Carga inicial sin filtro al abrir.
    } // Fin del constructor.

    // Consulta la BD (con filtro si existe) y rellena la tabla visual.
    private void cargarDatos() { // Refresca la tabla.
        try { // Intenta leer la BD.
            String filtro = txtBuscar.getText().trim(); // Lee el criterio de búsqueda.
            if (filtro.isEmpty()) { // Si no hay texto...
                listaActual = servicio.listarEstudiantes(); // ...carga todos.
            } else { // Si hay texto...
                listaActual = servicio.buscarEstudiantes(filtro); // ...carga coincidencias.
            } // Fin de evaluación de filtro.
            modelo.setRowCount(0); // Limpia filas actuales.
            for (Estudiante e : listaActual) { // Recorre cada estudiante.
                modelo.addRow(new Object[]{e.getId(), e.getNombreCompleto(), e.getEscuela(), e.getCurp(), e.getCodigoUnico(), FormatoHoras.horasMinutos(e.getHorasAcumuladas()) + " / " + e.getHorasRequeridas() + "h", e.getEstado()}); // Horas en formato humano cerrado.
            } // Fin del recorrido.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al cargar datos: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de cargarDatos.

    // Devuelve el objeto Estudiante de la fila seleccionada, o null si no hay.
    private Estudiante obtenerSeleccionado() { // Obtiene selección.
        int fila = tabla.getSelectedRow(); // Índice de la fila seleccionada.
        if (fila == -1) { // Si no hay selección...
            JOptionPane.showMessageDialog(this, "Seleccione un estudiante de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return null; // Devuelve null.
        } // Fin de validación.
        return listaActual.get(fila); // Devuelve el objeto sincronizado con la fila.
    } // Fin de obtenerSeleccionado.

    // Elimina todo el rastro del alumno seleccionado previa confirmación explícita.
    private void eliminarSeleccionado() { // Borrado seguro con confirmación.
        Estudiante sel = obtenerSeleccionado(); // Obtiene selección.
        if (sel == null) { return; } // Corta si no hay selección.
        int confirmacion = JOptionPane.showConfirmDialog(this, // Pregunta al administrador.
            "Se eliminara PERMANENTEMENTE a:\n" + sel.getNombreCompleto() + " (Folio " + sel.getId() + ")" + // Identifica al alumno.
            "\nIncluye: asistencias, QR y fotografia." + // Lista lo que se borra.
            "\nEl folio " + sel.getId() + " NO volvera a utilizarse." + // Aclara el retiro del folio.
            "\nLa accion quedara firmada en auditoria." + // Aclara la responsabilidad.
            "\n¿Desea continuar?", // Pregunta final.
            "Eliminar registro", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); // Título y tipo de diálogo.
        if (confirmacion == JOptionPane.YES_OPTION) { // Solo si confirmó explícitamente...
            try { // Intenta el borrado completo.
                servicio.eliminarEstudiante(sel.getId(), adminLogueado.getId()); // Borra rastro y firma auditoría.
                cargarDatos(); // Refresca la tabla.
            } catch (Exception ex) { // Captura cualquier error.
                JOptionPane.showMessageDialog(this, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
            } // Fin del catch.
        } // Fin de validación de confirmación.
    } // Fin de eliminarSeleccionado.

    // Alterna el estado ACTIVO/INACTIVO del estudiante seleccionado.
    private void cambiarEstado() { // Lógica de estado.
        Estudiante sel = obtenerSeleccionado(); // Obtiene selección.
        if (sel == null) { return; } // Corta si no hay selección.
        if (sel.getEstado().equals("FINALIZADO")) { // Si ya finalizó...
            JOptionPane.showMessageDialog(this, "Un estudiante FINALIZADO no puede cambiar de estado.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...bloquea.
            return; // Corta ejecución.
        } // Fin de validación.
        String nuevoEstado = sel.getEstado().equals("ACTIVO") ? "INACTIVO" : "ACTIVO"; // Alterna estado.
        try { // Intenta actualizar en BD.
            servicio.cambiarEstadoEstudiante(sel.getId(), nuevoEstado, adminLogueado.getId()); // Guarda con auditoría.
            cargarDatos(); // Refresca la tabla.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al cambiar estado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de cambiarEstado.

    // Muestra la imagen PNG del QR del estudiante seleccionado.
    private void verQR() { // Visor de QR.
        Estudiante sel = obtenerSeleccionado(); // Obtiene selección.
        if (sel == null) { return; } // Corta si no hay selección.
        File archivo = new File("datos/qr/" + sel.getCodigoUnico() + ".png"); // Ruta del PNG.
        if (!archivo.exists()) { // Si no existe...
            JOptionPane.showMessageDialog(this, "No se encontró el archivo QR en disco.", "Error", JOptionPane.ERROR_MESSAGE); // ...avisa.
            return; // Corta ejecución.
        } // Fin de validación.
        ImageIcon icono = new ImageIcon(archivo.getAbsolutePath()); // Carga la imagen.
        JLabel lblQR = new JLabel(icono); // Etiqueta con la imagen.
        JOptionPane.showMessageDialog(this, lblQR, "QR de " + sel.getNombreCompleto(), JOptionPane.PLAIN_MESSAGE); // Muestra el QR.
    } // Fin de verQR.
} // Fin de la clase VentanaGestionEstudiantes.