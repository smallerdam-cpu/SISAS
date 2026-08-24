package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.utilidad.ConfiguracionSistema; // Lectura/escritura de la configuración.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas.
import java.awt.FlowLayout; // Fila que no estira.
import java.awt.GridBagConstraints; // Reglas de GridBagLayout.
import java.awt.GridBagLayout; // Diseño que no estira.
import java.awt.Insets; // Márgenes.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import javax.swing.JButton; // Botón.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JTextField; // Campo de texto.

/**
 * DialogoConfiguracion (corregido): casillas anchas y editables sin depender de copias parciales.
 * CONEXIONES:
 * - Es abierto desde el menú Reportes de com.seq.sisas.ui.VentanaPrincipal.
 * - Lee y escribe com.seq.sisas.utilidad.ConfiguracionSistema.
 */
public class DialogoConfiguracion extends JDialog { // Hereda de JDialog (modal).

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_CAMPO = 320; // Ancho fijo de las casillas.
    private static final int ALTO_CAMPO = 26; // Alto fijo de las casillas.
    private static final int ANCHO_BOTON = 110; // Ancho fijo de los botones.
    private static final int ALTO_BOTON = 30; // Alto fijo de los botones.
    private static final int ESPACIO = 6; // Margen entre componentes.
    // ============================================

    private JTextField txtNombre; // Campo del nombre de la directora.
    private JTextField txtCargo; // Campo del cargo de la directora.

    // Constructor: owner = ventana principal.
    public DialogoConfiguracion(JFrame owner) { // Recibe al padre.
        super(owner, true); // true = modal.
        setTitle("Configuración del Reporte Oficial"); // Título del diálogo.
        setSize(470, 190); // Tamaño del diálogo.
        setLocationRelativeTo(owner); // Centra sobre el padre.
        setResizable(false); // Bloquea redimensionado.

        // Constructor con 30 columnas: garantiza ancho aunque falle cualquier otra medida.
        txtNombre = new JTextField(30); // Campo de nombre con ancho base de 30 columnas.
        txtNombre.setText(ConfiguracionSistema.obtenerNombreDirectora()); // Carga el nombre actual.
        txtNombre.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija exacta.

        txtCargo = new JTextField(30); // Campo de cargo con ancho base de 30 columnas.
        txtCargo.setText(ConfiguracionSistema.obtenerCargoDirectora()); // Carga el cargo actual.
        txtCargo.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija exacta.

        JPanel panel = new JPanel(new GridBagLayout()); // Cuadrícula que no estira.
        GridBagConstraints gbc = new GridBagConstraints(); // Reglas de colocación.
        gbc.insets = new Insets(ESPACIO, ESPACIO, ESPACIO, ESPACIO); // Margen uniforme.

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Nombre de la directora:"), gbc); // Etiqueta de nombre.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtNombre, gbc); // Campo de nombre.

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Cargo:"), gbc); // Etiqueta de cargo.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo a la izquierda.
        panel.add(txtCargo, gbc); // Campo de cargo.

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
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                try { // Intenta escribir el archivo.
                    ConfiguracionSistema.guardar(txtNombre.getText().trim(), txtCargo.getText().trim()); // Guarda nombre y cargo.
                    JOptionPane.showMessageDialog(DialogoConfiguracion.this, "Configuración guardada.\nLas próximas hojas y Excel la usarán.", "Éxito", JOptionPane.INFORMATION_MESSAGE); // Confirma.
                    dispose(); // Cierra el diálogo.
                } catch (Exception ex) { // Captura error de archivo.
                    JOptionPane.showMessageDialog(DialogoConfiguracion.this, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
                } // Fin del catch.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        btnCancelar.addActionListener(new ActionListener() { // Escucha clic en cancelar.
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); } // Cierra sin guardar.
        }); // Fin del listener.
    } // Fin del constructor.
} // Fin de la clase DialogoConfiguracion.