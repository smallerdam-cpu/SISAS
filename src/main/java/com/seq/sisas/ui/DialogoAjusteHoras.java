package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Admin que confirma con su contraseña.
import com.seq.sisas.modelo.Estudiante; // Alumno ajustado.
import com.seq.sisas.seguridad.SeguridadUtil; // Verificación BCrypt de la contraseña.
import com.seq.sisas.servicio.ServicioEstudiante; // Lógica de negocio del ajuste.
import com.seq.sisas.utilidad.FormatoHoras; // Formato humano de horas acumuladas.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas.
import java.awt.FlowLayout; // Fila que no estira.
import java.awt.GridBagConstraints; // Reglas de GridBagLayout.
import java.awt.GridBagLayout; // Diseño que no estira.
import java.awt.Insets; // Márgenes.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import javax.swing.ButtonGroup; // Grupo excluyente de radios.
import javax.swing.JButton; // Botón.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JPasswordField; // Campo de contraseña de confirmación.
import javax.swing.JRadioButton; // Opciones agregar/quitar.
import javax.swing.JTextField; // Campos de texto.

/**
 * DialogoAjusteHoras: ajuste manual de horas con motivo y contraseña del administrador.
 * CONEXIONES:
 * - Es abierto desde el menú de 3 puntos de com.seq.sisas.ui.VentanaGestionEstudiantes.
 * - Usa com.seq.sisas.servicio.ServicioEstudiante.ajustarHoras y SeguridadUtil.
 */
public class DialogoAjusteHoras extends JDialog { // Hereda de JDialog (modal).

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_CAMPO = 220; // Ancho fijo de casillas.
    private static final int ALTO_CAMPO = 26; // Alto fijo de casillas.
    private static final int ANCHO_BOTON = 110; // Ancho fijo de botones.
    private static final int ALTO_BOTON = 30; // Alto fijo de botones.
    private static final int ESPACIO = 6; // Margen entre componentes.
    // ============================================

    private JRadioButton rbAgregar; // Opción de sumar horas.
    private JRadioButton rbQuitar; // Opción de restar horas.
    private JTextField txtCantidad; // Cantidad de horas (acepta decimales).
    private JTextField txtMotivo; // Motivo obligatorio del ajuste.
    private JPasswordField txtPassword; // Contraseña del administrador para confirmar.
    private ServicioEstudiante servicio; // Lógica de negocio.
    private Estudiante estudiante; // Alumno ajustado.
    private Administrador admin; // Admin que firma y confirma.

    // Constructor: recibe alumno seleccionado, admin logueado y servicio.
    public DialogoAjusteHoras(JFrame owner, Estudiante estudiante, Administrador admin, ServicioEstudiante servicio) { // Recibe dependencias.
        super(owner, true); // true = modal.
        this.estudiante = estudiante; // Guarda al alumno.
        this.admin = admin; // Guarda al admin.
        this.servicio = servicio; // Guarda el servicio.

        setTitle("Ajuste de Horas"); // Título del diálogo.
        setSize(450, 320); // Tamaño del diálogo.
        setLocationRelativeTo(owner); // Centra sobre el padre.
        setResizable(false); // Bloquea redimensionado.

        JLabel lblInfo = new JLabel("<html>Alumno: " + estudiante.getNombreCompleto() + // Muestra al alumno afectado.
            "<br>Acumulado: " + FormatoHoras.horasMinutos(estudiante.getHorasAcumuladas()) + " de " + estudiante.getHorasRequeridas() + "h</html>"); // Muestra el acumulado actual.

        rbAgregar = new JRadioButton("Agregar horas", true); // Opción por defecto: sumar.
        rbQuitar = new JRadioButton("Quitar horas"); // Opción de restar.
        ButtonGroup grupo = new ButtonGroup(); // Grupo excluyente.
        grupo.add(rbAgregar); // Registra agregar.
        grupo.add(rbQuitar); // Registra quitar.
        JPanel panelRadios = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Fila de radios sin estirar.
        panelRadios.add(rbAgregar); // Agrega radio agregar.
        panelRadios.add(rbQuitar); // Agrega radio quitar.

        txtCantidad = new JTextField(); // Campo de cantidad.
        txtCantidad.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.
        txtMotivo = new JTextField(); // Campo de motivo.
        txtMotivo.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.
        txtPassword = new JPasswordField(); // Campo de contraseña de confirmación.
        txtPassword.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.

        JPanel panel = new JPanel(new GridBagLayout()); // Cuadrícula que no estira.
        GridBagConstraints gbc = new GridBagConstraints(); // Reglas de colocación.
        gbc.insets = new Insets(ESPACIO, ESPACIO, ESPACIO, ESPACIO); // Margen uniforme.

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST; // Información en 2 columnas.
        panel.add(lblInfo, gbc); // Muestra alumno y acumulado.
        gbc.gridwidth = 1; // Vuelve a una columna.

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Operación:"), gbc); // Etiqueta de operación.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Control a la izquierda.
        panel.add(panelRadios, gbc); // Radios agregar/quitar.

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Horas (ej. 4 o 4.5):"), gbc); // Etiqueta de cantidad.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtCantidad, gbc); // Campo de cantidad.

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Motivo (*):"), gbc); // Etiqueta de motivo obligatorio.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtMotivo, gbc); // Campo de motivo.

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Contraseña admin (*):"), gbc); // Etiqueta de confirmación.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtPassword, gbc); // Campo de contraseña.

        JButton btnGuardar = new JButton("Aplicar"); // Botón aplicar ajuste.
        btnGuardar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnCancelar = new JButton("Cancelar"); // Botón cancelar.
        btnCancelar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JPanel panelBotones = new JPanel(new FlowLayout()); // Fila centrada.
        panelBotones.add(btnGuardar); // Agrega aplicar.
        panelBotones.add(btnCancelar); // Agrega cancelar.

        add(panel, BorderLayout.CENTER); // Cuadrícula al centro.
        add(panelBotones, BorderLayout.SOUTH); // Botones abajo.

        btnGuardar.addActionListener(new ActionListener() { // Escucha clic en aplicar.
            @Override
            public void actionPerformed(ActionEvent e) { aplicar(); } // Ejecuta el ajuste validado.
        }); // Fin del listener.

        btnCancelar.addActionListener(new ActionListener() { // Escucha clic en cancelar.
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); } // Cierra sin aplicar.
        }); // Fin del listener.
    } // Fin del constructor.

    // Valida cantidad, motivo y contraseña, y aplica el ajuste firmado.
    private void aplicar() { // Procesa el ajuste.
        String cantidadTexto = txtCantidad.getText().trim().replace(',', '.'); // Lee y normaliza el decimal.
        double cantidad; // Variable numérica de horas.
        try { // Intenta convertir.
            cantidad = Double.parseDouble(cantidadTexto); // Convierte a double.
        } catch (NumberFormatException ex) { // Si no es número...
            JOptionPane.showMessageDialog(this, "Cantidad de horas invalida.", "Validacion", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return; // Corta ejecución.
        } // Fin de validación numérica.
        if (cantidad <= 0) { // La cantidad debe ser positiva.
            JOptionPane.showMessageDialog(this, "La cantidad debe ser mayor que cero.", "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación de positivo.
        String motivo = txtMotivo.getText().trim(); // Lee el motivo.
        if (motivo.isEmpty()) { // Motivo obligatorio.
            JOptionPane.showMessageDialog(this, "El motivo es obligatorio.", "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación de motivo.
        String password = new String(txtPassword.getPassword()); // Lee la contraseña de confirmación.
        if (!SeguridadUtil.verificar(password, admin.getPasswordHash())) { // Compara contra el hash del admin.
            JOptionPane.showMessageDialog(this, "Contrasena incorrecta. El ajuste no se aplico.", "Seguridad", JOptionPane.WARNING_MESSAGE); // Rechaza sin contraseña válida.
            return; // Corta ejecución.
        } // Fin de validación de contraseña.
        int deltaMinutos = (int) Math.round(cantidad * 60) * (rbAgregar.isSelected() ? 1 : -1); // Convierte a minutos con signo.
        try { // Intenta aplicar el ajuste.
            servicio.ajustarHoras(estudiante.getId(), deltaMinutos, motivo, admin.getId()); // Ajusta con reportes y auditoría.
            JOptionPane.showMessageDialog(this, "Ajuste aplicado y registrado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE); // Confirma.
            dispose(); // Cierra el diálogo.
        } catch (Exception ex) { // Captura reglas de negocio (ej. quitar de más).
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra el motivo exacto.
        } // Fin del catch.
    } // Fin de aplicar.
} // Fin de la clase DialogoAjusteHoras.