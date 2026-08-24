package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad de administrador.
import com.seq.sisas.servicio.ServicioAdministradores; // Lógica de negocio.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas.
import java.awt.FlowLayout; // Fila que no estira.
import java.awt.GridBagConstraints; // Reglas de GridBagLayout.
import java.awt.GridBagLayout; // Diseño que no estira.
import java.awt.Insets; // Márgenes.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import javax.swing.JButton; // Botón.
import javax.swing.JCheckBox; // Casilla de cuenta activa.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JPasswordField; // Campo de contraseña.
import javax.swing.JTextField; // Campo de texto.

/**
 * DialogoAdministrador: formulario para crear o editar cuentas de administración.
 * CONEXIONES:
 * - Es abierto por com.seq.sisas.ui.VentanaGestionAdministradores.
 * - Usa com.seq.sisas.servicio.ServicioAdministradores.
 */
public class DialogoAdministrador extends JDialog { // Hereda de JDialog (modal).

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_CAMPO = 220; // Ancho fijo de casillas.
    private static final int ALTO_CAMPO = 26; // Alto fijo de casillas.
    private static final int ANCHO_BOTON = 110; // Ancho fijo de botones.
    private static final int ALTO_BOTON = 30; // Alto fijo de botones.
    private static final int ESPACIO = 6; // Margen entre componentes.
    // ============================================

    private JTextField txtNombre; // Campo nombre real.
    private JTextField txtUsuario; // Campo usuario de acceso.
    private JPasswordField txtPassword; // Campo contraseña (obligatoria al crear, opcional al editar).
    private JCheckBox chkActivo; // Casilla de cuenta activa (solo edición).
    private ServicioAdministradores servicio; // Lógica de negocio.
    private Integer responsableId; // ID del administrador que firma la acción.
    private Administrador existente; // Null si es creación; lleno si es edición.

    // Constructor: existente = null para crear cuenta nueva.
    public DialogoAdministrador(JFrame owner, ServicioAdministradores servicio, Integer responsableId, Administrador existente) { // Recibe dependencias.
        super(owner, true); // true = modal.
        this.servicio = servicio; // Guarda el servicio.
        this.responsableId = responsableId; // Guarda al responsable.
        this.existente = existente; // Guarda la cuenta a editar (o null).

        setTitle(existente == null ? "Nuevo Administrador" : "Editar Administrador"); // Título según modo.
        setSize(420, 240); // Tamaño del diálogo.
        setLocationRelativeTo(owner); // Centra sobre el padre.
        setResizable(false); // Bloquea redimensionado.

        txtNombre = new JTextField(20); // Campo nombre.
        txtNombre.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.
        txtUsuario = new JTextField(20); // Campo usuario.
        txtUsuario.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.
        txtPassword = new JPasswordField(20); // Campo contraseña.
        txtPassword.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.
        chkActivo = new JCheckBox("Cuenta activa", true); // Casilla activa por defecto.

        // Si es edición, pre-llena nombre y usuario y estado.
        if (existente != null) { // Modo edición.
            txtNombre.setText(existente.getNombreCompleto()); // Carga nombre.
            txtUsuario.setText(existente.getUsuario()); // Carga usuario.
            chkActivo.setSelected(existente.isActivo()); // Carga estado actual.
        } // Fin de pre-llenado.

        JPanel panel = new JPanel(new GridBagLayout()); // Cuadrícula que no estira.
        GridBagConstraints gbc = new GridBagConstraints(); // Reglas de colocación.
        gbc.insets = new Insets(ESPACIO, ESPACIO, ESPACIO, ESPACIO); // Margen uniforme.

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Nombre completo:"), gbc); // Etiqueta nombre.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtNombre, gbc); // Campo nombre.

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Usuario:"), gbc); // Etiqueta usuario.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtUsuario, gbc); // Campo usuario.

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel(existente == null ? "Contraseña (mín. 6):" : "Nueva contraseña (opcional):"), gbc); // Etiqueta según modo.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtPassword, gbc); // Campo contraseña.

        gbc.gridx = 1; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; // Casilla alineada con campos.
        panel.add(chkActivo, gbc); // Casilla de cuenta activa.

        JButton btnGuardar = new JButton("Guardar"); // Botón guardar.
        btnGuardar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnCancelar = new JButton("Cancelar"); // Botón cancelar.
        btnCancelar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JPanel panelBotones = new JPanel(new FlowLayout()); // Fila centrada.
        panelBotones.add(btnGuardar); // Agrega guardar.
        panelBotones.add(btnCancelar); // Agrega cancelar.

        add(panel, BorderLayout.CENTER); // Cuadrícula al centro.
        add(panelBotones, BorderLayout.SOUTH); // Botones abajo.

        btnGuardar.addActionListener(new ActionListener() { // Escucha clic en guardar.
            @Override
            public void actionPerformed(ActionEvent e) { guardar(); } // Ejecuta guardado.
        }); // Fin del listener.

        btnCancelar.addActionListener(new ActionListener() { // Escucha clic en cancelar.
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); } // Cierra sin guardar.
        }); // Fin del listener.
    } // Fin del constructor.

    // Valida y guarda (creación o edición) con firma de auditoría.
    private void guardar() { // Procesa el guardado.
        String nombre = txtNombre.getText().trim(); // Lee nombre.
        String usuario = txtUsuario.getText().trim(); // Lee usuario.
        String password = new String(txtPassword.getPassword()); // Lee contraseña.

        if (nombre.isEmpty() || usuario.isEmpty()) { // Nombre y usuario obligatorios.
            JOptionPane.showMessageDialog(this, "Nombre y usuario son obligatorios.", "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación básica.

        if (existente == null && password.length() < 6) { // Al crear, contraseña mínima.
            JOptionPane.showMessageDialog(this, "La contrasena debe tener al menos 6 caracteres.", "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación de creación.

        if (existente != null && !password.isEmpty() && password.length() < 6) { // Al editar, si se escribe, mínima.
            JOptionPane.showMessageDialog(this, "La contrasena debe tener al menos 6 caracteres.", "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación de edición.

        try { // Intenta guardar.
            if (existente == null) { // Modo creación.
                servicio.crear(nombre, usuario, password, responsableId); // Crea cuenta cifrada y auditada.
                JOptionPane.showMessageDialog(this, "Administrador creado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE); // Confirma.
            } else { // Modo edición.
                existente.setNombreCompleto(nombre); // Actualiza nombre.
                existente.setUsuario(usuario); // Actualiza usuario.
                existente.setActivo(chkActivo.isSelected()); // Actualiza estado.
                servicio.actualizar(existente, responsableId); // Guarda con auditoría y reglas.
                if (!password.isEmpty()) { servicio.cambiarPassword(existente.getId(), password, responsableId); } // Cambia contraseña solo si se escribió.
                JOptionPane.showMessageDialog(this, "Cuenta actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE); // Confirma.
            } // Fin de evaluación de modo.
            dispose(); // Cierra el diálogo.
        } catch (Exception ex) { // Captura usuario duplicado o regla de seguridad.
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra el motivo exacto.
        } // Fin del catch.
    } // Fin de guardar.
} // Fin de la clase DialogoAdministrador.