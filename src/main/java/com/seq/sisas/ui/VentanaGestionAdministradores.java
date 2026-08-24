package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad de administrador.
import com.seq.sisas.servicio.ServicioAdministradores; // Lógica de negocio.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas.
import java.awt.FlowLayout; // Fila que no estira.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Lista en memoria.
import javax.swing.JButton; // Botón.
import javax.swing.JFrame; // Ventana.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JPasswordField; // Campo para nueva contraseña.
import javax.swing.JScrollPane; // Scroll de la tabla.
import javax.swing.JTable; // Tabla visual.
import javax.swing.table.DefaultTableModel; // Modelo de la tabla.

/**
 * VentanaGestionAdministradores: tabla de cuentas con crear, editar y cambiar contraseña.
 * CONEXIONES:
 * - Es abierta desde el menú Administración de com.seq.sisas.ui.VentanaPrincipal.
 * - Usa com.seq.sisas.servicio.ServicioAdministradores y abre DialogoAdministrador.
 */
public class VentanaGestionAdministradores extends JFrame { // Hereda de JFrame.

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_BOTON = 150; // Ancho fijo de cada botón.
    private static final int ALTO_BOTON = 30; // Alto fijo de cada botón.
    private static final int ALTO_FILA_TABLA = 28; // Alto de cada fila de la tabla.
    // ============================================

    private ServicioAdministradores servicio; // Lógica de negocio.
    private Administrador adminLogueado; // Admin responsable que firma las acciones.
    private DefaultTableModel modelo; // Modelo de la tabla.
    private JTable tabla; // Tabla visual.
    private List<Administrador> listaActual; // Lista en memoria sincronizada con la tabla.

    // Constructor: recibe al administrador logueado.
    public VentanaGestionAdministradores(Administrador admin) { // Recibe al admin.
        this.adminLogueado = admin; // Guarda al responsable.
        this.servicio = new ServicioAdministradores(); // Inicializa el servicio.

        setTitle("SISAS - Gestión de Administradores"); // Título de la ventana.
        setSize(650, 400); // Tamaño de la ventana.
        setLocationRelativeTo(null); // Centra en pantalla.

        modelo = new DefaultTableModel() { // Modelo con celdas no editables.
            @Override
            public boolean isCellEditable(int row, int column) { return false; } // Solo lectura visual.
        }; // Fin del modelo anónimo.
        modelo.setColumnIdentifiers(new String[]{"ID", "Nombre", "Usuario", "Activo"}); // Encabezados.
        tabla = new JTable(modelo); // Crea la tabla.
        tabla.setRowHeight(ALTO_FILA_TABLA); // Alto de fila fijo.
        JScrollPane scroll = new JScrollPane(tabla); // Scroll para la tabla.
        add(scroll, BorderLayout.CENTER); // Tabla al centro.

        JButton btnNuevo = new JButton("Nuevo"); // Crea cuenta.
        btnNuevo.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnEditar = new JButton("Editar"); // Edita cuenta.
        btnEditar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnPassword = new JButton("Cambiar Contraseña"); // Cambia contraseña.
        btnPassword.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.

        JPanel panelBotones = new JPanel(new FlowLayout()); // Fila centrada.
        panelBotones.add(btnNuevo); // Agrega nuevo.
        panelBotones.add(btnEditar); // Agrega editar.
        panelBotones.add(btnPassword); // Agrega cambiar contraseña.
        add(panelBotones, BorderLayout.SOUTH); // Botones abajo.

        btnNuevo.addActionListener(new ActionListener() { // Clic en Nuevo.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                new DialogoAdministrador(VentanaGestionAdministradores.this, servicio, adminLogueado.getId(), null).setVisible(true); // Abre formulario de creación.
                cargarDatos(); // Refresca la tabla.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        btnEditar.addActionListener(new ActionListener() { // Clic en Editar.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                Administrador sel = obtenerSeleccionado(); // Obtiene la fila seleccionada.
                if (sel != null) { // Si hay selección válida...
                    new DialogoAdministrador(VentanaGestionAdministradores.this, servicio, adminLogueado.getId(), sel).setVisible(true); // Abre formulario de edición.
                    cargarDatos(); // Refresca la tabla.
                } // Fin de validación.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        btnPassword.addActionListener(new ActionListener() { // Clic en Cambiar Contraseña.
            @Override
            public void actionPerformed(ActionEvent e) { cambiarContrasena(); } // Ejecuta el cambio.
        }); // Fin del listener.

        cargarDatos(); // Carga inicial de la tabla.
    } // Fin del constructor.

    // Consulta la BD y rellena la tabla de cuentas.
    private void cargarDatos() { // Refresca la tabla.
        try { // Intenta leer la BD.
            listaActual = servicio.listar(); // Obtiene todas las cuentas.
            modelo.setRowCount(0); // Limpia filas actuales.
            for (Administrador a : listaActual) { // Recorre cada cuenta.
                modelo.addRow(new Object[]{a.getId(), a.getNombreCompleto(), a.getUsuario(), a.isActivo() ? "SÍ" : "NO"}); // Agrega fila visual.
            } // Fin del recorrido.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al cargar administradores: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de cargarDatos.

    // Devuelve la cuenta seleccionada en la tabla, o null si no hay.
    private Administrador obtenerSeleccionado() { // Obtiene selección.
        int fila = tabla.getSelectedRow(); // Índice de la fila seleccionada.
        if (fila == -1) { // Si no hay selección...
            JOptionPane.showMessageDialog(this, "Seleccione un administrador de la tabla.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return null; // Devuelve null.
        } // Fin de validación.
        return listaActual.get(fila); // Devuelve el objeto sincronizado.
    } // Fin de obtenerSeleccionado.

    // Pide una nueva contraseña en un diálogo simple y la aplica cifrada.
    private void cambiarContrasena() { // Cambio rápido de contraseña.
        Administrador sel = obtenerSeleccionado(); // Obtiene selección.
        if (sel == null) { return; } // Corta si no hay selección.
        JPasswordField pf = new JPasswordField(12); // Campo oculto para la nueva contraseña.
        int opcion = JOptionPane.showConfirmDialog(this, pf, "Nueva contrasena para '" + sel.getUsuario() + "' (mínimo 6):", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); // Diálogo con campo.
        if (opcion != JOptionPane.OK_OPTION) { return; } // Corta si canceló.
        String password = new String(pf.getPassword()); // Lee la contraseña escrita.
        if (password.length() < 6) { // Validación de longitud mínima.
            JOptionPane.showMessageDialog(this, "La contrasena debe tener al menos 6 caracteres.", "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación.
        try { // Intenta aplicar el cambio.
            servicio.cambiarPassword(sel.getId(), password, adminLogueado.getId()); // Cifra, guarda y firma.
            JOptionPane.showMessageDialog(this, "Contraseña actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE); // Confirma.
        } catch (Exception ex) { // Captura cualquier error.
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de cambiarContrasena.
} // Fin de la clase VentanaGestionAdministradores.