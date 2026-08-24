package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad para recibir al usuario logueado.
import com.seq.sisas.servicio.ServicioAutenticacion; // Servicio que valida credenciales.
import java.awt.Dimension; // Medidas fijas en píxeles.
import java.awt.GridBagConstraints; // Reglas de colocación de GridBagLayout.
import java.awt.GridBagLayout; // Diseño que NO estira los componentes.
import java.awt.Insets; // Márgenes entre componentes.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.sql.SQLException; // Manejo de errores de base de datos.
import javax.swing.JButton; // Botón interactivo.
import javax.swing.JFrame; // Ventana principal.
import javax.swing.JLabel; // Etiqueta de texto.
import javax.swing.JOptionPane; // Diálogos emergentes.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JPasswordField; // Campo de contraseña oculta.
import javax.swing.JTextField; // Campo de texto normal.

/**
 * VentanaLogin (V11): campos y botón de tamaño fijo, independientes de la ventana.
 * CONEXIONES:
 * - Es instanciada por com.seq.sisas.app.Main al arrancar el programa.
 * - Usa com.seq.sisas.servicio.ServicioAutenticacion para validar el ingreso.
 * - Al tener éxito, invoca a com.seq.sisas.ui.VentanaPrincipal y se destruye.
 */
public class VentanaLogin extends JFrame { // Hereda de JFrame.

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_CAMPO = 180; // Ancho fijo de las casillas de texto.
    private static final int ALTO_CAMPO = 28; // Alto fijo de las casillas de texto.
    private static final int ANCHO_BOTON = 130; // Ancho fijo del botón Ingresar.
    private static final int ALTO_BOTON = 32; // Alto fijo del botón Ingresar.
    private static final int ESPACIO = 6; // Margen entre todos los componentes.
    // ============================================

    private JTextField txtUsuario; // Campo para el nombre de usuario.
    private JPasswordField txtPassword; // Campo de contraseña oculta.
    private JButton btnIngresar; // Botón que dispara la validación.
    private JLabel lblMensaje; // Etiqueta para errores en pantalla.
    private ServicioAutenticacion authService; // Instancia del servicio de negocio.

    // Constructor: configura diseño y componentes con medidas fijas.
    public VentanaLogin() { // Se ejecuta al crear el objeto.

        setTitle("SISAS - Acceso SEQ"); // Título de la barra superior.
        setSize(700, 550); // Tamaño inicial de la ventana.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra el programa al cerrar.
        setLocationRelativeTo(null); // Centra la ventana en pantalla.

        this.authService = new ServicioAutenticacion(); // Conecta con la lógica de base de datos.

        JLabel lblTitulo = new JLabel("Sistema de Asistencia SEQ", JLabel.CENTER); // Título centrado.
        lblTitulo.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16)); // Fuente en negrita.

        txtUsuario = new JTextField(); // Campo de usuario sin columnas (la medida la da setPreferredSize).
        txtUsuario.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija independiente de la ventana.
        txtPassword = new JPasswordField(); // Campo de contraseña.
        txtPassword.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija independiente de la ventana.
        btnIngresar = new JButton("Ingresar"); // Botón de acceso.
        btnIngresar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija independiente de la ventana.
        lblMensaje = new JLabel(" ", JLabel.CENTER); // Etiqueta de mensajes de error.
        lblMensaje.setForeground(java.awt.Color.RED); // Color rojo para errores.

        JLabel lblUsuario = new JLabel("Usuario:", JLabel.RIGHT); // Etiqueta alineada a la derecha.
        JLabel lblPassword = new JLabel("Contraseña:", JLabel.RIGHT); // Etiqueta alineada a la derecha.

        // --- Panel con GridBagLayout: nada se estira al redimensionar ---
        JPanel panel = new JPanel(new GridBagLayout()); // Diseño que mantiene tamaños.
        GridBagConstraints gbc = new GridBagConstraints(); // Reglas de colocación.
        gbc.insets = new Insets(ESPACIO, ESPACIO, ESPACIO, ESPACIO); // Margen uniforme entre componentes.

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; // El título ocupa las 2 columnas.
        panel.add(lblTitulo, gbc); // Coloca el título arriba.
        gbc.gridwidth = 1; // Vuelve a una columna para las filas siguientes.

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; // Etiqueta pegada a la derecha.
        panel.add(lblUsuario, gbc); // Coloca etiqueta de usuario.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo pegado a la izquierda.
        panel.add(txtUsuario, gbc); // Coloca campo de usuario.

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; // Etiqueta pegada a la derecha.
        panel.add(lblPassword, gbc); // Coloca etiqueta de contraseña.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Campo pegado a la izquierda.
        panel.add(txtPassword, gbc); // Coloca campo de contraseña.

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; // Botón centrado en 2 columnas.
        panel.add(btnIngresar, gbc); // Coloca el botón Ingresar.
        gbc.gridwidth = 1; // Restaura una columna.

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; // El mensaje ocupa las 2 columnas.
        panel.add(lblMensaje, gbc); // Coloca la etiqueta de mensaje abajo.

        add(panel); // El panel va al centro de la ventana por defecto.

        btnIngresar.addActionListener(new ActionListener() { // Escucha el clic del botón.
            @Override
            public void actionPerformed(ActionEvent e) { validarCredenciales(); } // Ejecuta la validación.
        }); // Fin del ActionListener.
    } // Fin del constructor.

    // Método privado que valida contra el servicio.
    private void validarCredenciales() { // Procesa el login.

        String usuario = txtUsuario.getText(); // Lee el usuario escrito.
        String password = new String(txtPassword.getPassword()); // Lee la contraseña como String.

        if (usuario.isEmpty() || password.isEmpty()) { // Si hay campos vacíos...
            lblMensaje.setText("Complete todos los campos."); // ...muestra advertencia.
            return; // Corta la ejecución.
        } // Fin de validación de vacíos.

        try { // Intenta validar contra SQLite.
            Administrador admin = authService.iniciarSesion(usuario, password); // Llama al servicio.

            if (admin != null) { // Si el login fue exitoso...
                lblMensaje.setText(""); // Limpia errores previos.
                VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(admin); // Crea el panel principal.
                ventanaPrincipal.setVisible(true); // Lo hace visible.
                this.dispose(); // Destruye la ventana de login.
            } else { // Si las credenciales fallaron...
                lblMensaje.setText("Usuario o contraseña incorrectos."); // Muestra el error.
                txtPassword.setText(""); // Limpia la contraseña por seguridad.
            } // Fin de evaluación.

        } catch (SQLException ex) { // Captura errores graves de BD.
            JOptionPane.showMessageDialog(this, "Error de base de datos: " + ex.getMessage(), "Error Crítico", JOptionPane.ERROR_MESSAGE); // Muestra diálogo de error.
        } // Fin del catch.
    } // Fin de validarCredenciales.
} // Fin de la clase VentanaLogin.