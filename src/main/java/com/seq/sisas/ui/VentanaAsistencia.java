package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad del admin (incluye su hash de contraseña).
import com.seq.sisas.modelo.Estudiante; // Entidad para la ficha con foto.
import com.seq.sisas.seguridad.SeguridadUtil; // Verificación BCrypt de la contraseña del admin.
import com.seq.sisas.servicio.ServicioAsistencia; // Lógica de negocio de asistencia.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas en píxeles.
import java.awt.FlowLayout; // Diseño en fila que no estira componentes.
import java.awt.GridBagConstraints; // Reglas de colocación de GridBagLayout.
import java.awt.GridBagLayout; // Diseño que centra sin estirar.
import java.awt.Image; // Para escalar la fotografía.
import java.awt.event.ActionEvent; // Evento de clic o Enter.
import java.awt.event.ActionListener; // Escuchador de eventos.
import java.io.File; // Archivo de foto en disco.
import java.sql.SQLException; // Errores de BD.
import java.time.LocalDateTime; // Fecha y hora actual.
import java.time.format.DateTimeFormatter; // Formato del reloj.
import javax.swing.ImageIcon; // Carga imágenes.
import javax.swing.JButton; // Botón.
import javax.swing.JFrame; // Ventana.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JPasswordField; // Campo de contraseña para el acceso admin.
import javax.swing.JTextField; // Campo de código.
import javax.swing.Timer; // Temporizadores (reloj y privacidad).

/**
 * VentanaAsistencia (V11.1): ficha con foto centrada debajo del campo de código.
 * CONEXIONES:
 * - Es abierta por com.seq.sisas.ui.VentanaPrincipal, que se oculta en modo kiosco.
 * - Usa com.seq.sisas.servicio.ServicioAsistencia para procesar códigos.
 * - Usa com.seq.sisas.seguridad.SeguridadUtil para validar la contraseña al salir.
 */
public class VentanaAsistencia extends JFrame { // Hereda de JFrame.

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_CAMPO_CODIGO = 220; // Ancho fijo del campo de código.
    private static final int ALTO_CAMPO = 30; // Alto fijo del campo de código.
    private static final int ANCHO_BOTON = 120; // Ancho fijo del botón Registrar.
    private static final int ALTO_BOTON = 32; // Alto fijo del botón Registrar.
    private static final int ANCHO_BOTON_ADMIN = 180; // Ancho fijo del botón Acceso Administrador.
    private static final int TAMANO_FOTO = 150; // Lado del recuadro cuadrado de la foto.
    private static final int FUENTE_RELOJ = 18; // Tamaño de fuente del reloj.
    // ============================================

    private ServicioAsistencia servicio; // Lógica de negocio.
    private Administrador admin; // Admin responsable (para validar su contraseña).
    private VentanaPrincipal ventanaPrincipal; // Referencia al panel oculto para restaurarlo.
    private JTextField txtCodigo; // Campo para código (acepta lector USB).
    private JLabel lblResultado; // Registro inferior de ENTRADA/SALIDA con hora y fecha (NO se mueve).
    private JLabel lblReloj; // Reloj con fecha y hora en vivo.
    private JLabel lblFoto; // Etiqueta con la fotografía del alumno.
    private JLabel lblNombreAlumno; // Texto bajo la foto: ENTRADA/SALIDA + nombre.
    private Timer timerPrivacidad; // Temporizador de 5 segundos que borra la ficha.

    // Constructor: recibe al admin y a la ventana principal que quedará oculta.
    public VentanaAsistencia(Administrador admin, VentanaPrincipal ventanaPrincipal) { // Recibe dependencias.
        this.admin = admin; // Guarda al admin.
        this.ventanaPrincipal = ventanaPrincipal; // Guarda referencia del panel principal.
        this.servicio = new ServicioAsistencia(); // Inicializa el servicio.

        setTitle("SISAS - Registro de Asistencia (Modo Kiosco)"); // Título del kiosco.
        setSize(750, 420); // Alto ampliado para la ficha centrada bajo el campo.
        setLocationRelativeTo(null); // Centra en pantalla.
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // La X no cierra: seguridad del kiosco.

        lblReloj = new JLabel("", JLabel.CENTER); // Etiqueta del reloj.
        lblReloj.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, FUENTE_RELOJ)); // Fuente configurable.
        actualizarReloj(); // Pinta la hora al abrir.

        Timer timer = new Timer(1000, new ActionListener() { // Temporizador del reloj (1 segundo).
            @Override
            public void actionPerformed(ActionEvent e) { actualizarReloj(); } // Refresca la hora.
        }); // Fin del ActionListener.
        timer.start(); // Arranca el reloj en vivo.

        // Temporizador de privacidad: a los 5 segundos borra foto y texto de la ficha.
        timerPrivacidad = new Timer(5000, new ActionListener() { // 5000 milisegundos.
            @Override
            public void actionPerformed(ActionEvent e) { limpiarFicha(); } // Borra los datos visibles.
        }); // Fin del ActionListener.
        timerPrivacidad.setRepeats(false); // Disparo único: se reinicia con cada escaneo.

        // --- Campo de código y botón Registrar (NO se mueven) ---
        txtCodigo = new JTextField(); // Campo para el código sin columnas.
        txtCodigo.setPreferredSize(new Dimension(ANCHO_CAMPO_CODIGO, ALTO_CAMPO)); // Medida fija.
        JButton btnRegistrar = new JButton("Registrar"); // Botón de registro.
        btnRegistrar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.

        JPanel panelCodigo = new JPanel(new FlowLayout()); // Fila centrada que no estira.
        panelCodigo.add(txtCodigo); // Agrega el campo.
        panelCodigo.add(btnRegistrar); // Agrega el botón.

        // --- Ficha centrada: foto arriba y texto debajo ---
        lblFoto = new JLabel(" ", JLabel.CENTER); // Recuadro de foto vacío al inicio.
        lblFoto.setOpaque(true); // Fondo sólido para el recuadro.
        lblFoto.setBackground(new java.awt.Color(230, 230, 230)); // Gris claro de fondo.
        lblFoto.setPreferredSize(new Dimension(TAMANO_FOTO, TAMANO_FOTO)); // Recuadro cuadrado fijo.
        lblNombreAlumno = new JLabel(" ", JLabel.CENTER); // Texto vacío al inicio.

        JPanel panelFicha = new JPanel(new GridBagLayout()); // Centra foto y texto sin estirar.
        GridBagConstraints gbc = new GridBagConstraints(); // Reglas de colocación.
        gbc.gridx = 0; gbc.gridy = 0; // La foto va en la fila 0, centrada.
        panelFicha.add(lblFoto, gbc); // Coloca el recuadro de foto.
        gbc.gridy = 1; // El texto va en la fila 1, centrado.
        panelFicha.add(lblNombreAlumno, gbc); // Coloca el texto bajo la foto.

        // --- Zona central: campo de código arriba y ficha debajo ---
        JPanel panelCentro = new JPanel(new BorderLayout()); // Apila las dos zonas centradas.
        panelCentro.add(panelCodigo, BorderLayout.NORTH); // El campo de código queda arriba (sin moverse).
        panelCentro.add(panelFicha, BorderLayout.CENTER); // La ficha queda centrada debajo del campo.
        add(panelCentro, BorderLayout.CENTER); // Zona central de la ventana.

        // --- Zona inferior SIN cambios: registro de entrada/salida y acceso admin ---
        JButton btnAdmin = new JButton("Acceso Administrador"); // Única salida del kiosco.
        btnAdmin.setPreferredSize(new Dimension(ANCHO_BOTON_ADMIN, ALTO_BOTON)); // Medida fija.
        lblResultado = new JLabel("Escanee o escriba el codigo del estudiante.", JLabel.CENTER); // Registro inferior inicial.

        JPanel panelSur = new JPanel(new BorderLayout()); // Panel inferior compuesto.
        panelSur.add(lblResultado, BorderLayout.CENTER); // Registro de ENTRADA/SALIDA con hora y fecha.
        panelSur.add(btnAdmin, BorderLayout.EAST); // Botón admin a la derecha.
        add(panelSur, BorderLayout.SOUTH); // Zona inferior de la ventana.

        add(lblReloj, BorderLayout.NORTH); // Reloj arriba.

        btnRegistrar.addActionListener(new ActionListener() { // Escucha clic.
            @Override
            public void actionPerformed(ActionEvent e) { procesar(); } // Procesa el código.
        }); // Fin del listener.

        txtCodigo.addActionListener(new ActionListener() { // Escucha Enter (lector USB).
            @Override
            public void actionPerformed(ActionEvent e) { procesar(); } // Procesa el código.
        }); // Fin del listener.

        btnAdmin.addActionListener(new ActionListener() { // Escucha clic en acceso admin.
            @Override
            public void actionPerformed(ActionEvent e) { solicitarAccesoAdmin(); } // Pide contraseña.
        }); // Fin del listener.
    } // Fin del constructor.

    // Pide la contraseña del administrador para salir del modo kiosco.
    private void solicitarAccesoAdmin() { // Validación de salida.
        JPasswordField pf = new JPasswordField(12); // Campo oculto para la contraseña.
        int opcion = JOptionPane.showConfirmDialog(this, pf, "Escriba la contrasena del administrador para salir del kiosco:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE); // Diálogo con campo.
        if (opcion == JOptionPane.OK_OPTION) { // Si presionó Aceptar...
            String pass = new String(pf.getPassword()); // Lee la contraseña escrita.
            if (SeguridadUtil.verificar(pass, admin.getPasswordHash())) { // Compara contra el hash BCrypt del admin.
                timerPrivacidad.stop(); // Detiene el temporizador de privacidad.
                dispose(); // Cierra el kiosco.
                ventanaPrincipal.setVisible(true); // Restaura el panel principal oculto.
            } else { // Si la contraseña es incorrecta...
                JOptionPane.showMessageDialog(this, "Contrasena incorrecta. Acceso denegado.", "Seguridad", JOptionPane.WARNING_MESSAGE); // ...deniega y permanece en kiosco.
            } // Fin de evaluación de contraseña.
        } // Fin de validación de opción.
    } // Fin de solicitarAccesoAdmin.

    // Borra foto y texto de la ficha para proteger los datos del alumno.
    private void limpiarFicha() { // Limpieza por privacidad.
        lblFoto.setIcon(null); // Quita la imagen.
        lblFoto.setText(" "); // Deja el recuadro vacío.
        lblNombreAlumno.setText(" "); // Borra el texto de ENTRADA/SALIDA y nombre.
    } // Fin de limpiarFicha.

    // Envía el código al servicio, muestra el registro inferior y pinta la ficha centrada.
    private void procesar() { // Procesa el escaneo.
        String codigo = txtCodigo.getText().trim(); // Lee y limpia el código antes de borrarlo.
        try { // Intenta registrar el movimiento.
            String msg = servicio.registrarMovimiento(codigo, admin.getId()); // Llama al servicio.
            lblResultado.setText(msg); // Registro inferior con hora y fecha (sin cambios).
            boolean esEntrada = msg.startsWith("ENTRADA"); // Detecta si el mensaje es de entrada.
            mostrarFicha(codigo, esEntrada); // Pinta foto y texto según el tipo de movimiento.
            timerPrivacidad.restart(); // Reinicia los 5 segundos de privacidad.
            txtCodigo.setText(""); // Limpia el campo para el siguiente.
        } catch (SQLException ex) { // Captura error de BD.
            lblResultado.setText("Error de base de datos."); // Muestra error genérico abajo.
        } // Fin del catch.
        txtCodigo.requestFocus(); // Devuelve el foco al campo.
    } // Fin de procesar.

    // Carga la foto y escribe el texto de bienvenida/salida bajo ella.
    private void mostrarFicha(String codigo, boolean esEntrada) { // Pinta la ficha centrada.
        try { // Intenta leer la BD.
            Estudiante est = servicio.obtenerEstudiantePorCodigo(codigo); // Obtiene la ficha completa.
            if (est == null) { // Si el código no existe...
                lblNombreAlumno.setText("SIN REGISTRO"); // Muestra aviso bajo el recuadro.
                lblFoto.setIcon(null); // Limpia imagen.
                lblFoto.setText(" "); // Recuadro vacío.
                return; // Corta ejecución.
            } // Fin de validación.
            if (esEntrada) { // Si es ENTRADA...
                lblNombreAlumno.setText("ENTRADA - Bienvenido, " + est.getNombreCompleto()); // Texto de bienvenida con nombre.
            } else { // Si es SALIDA...
                lblNombreAlumno.setText("SALIDA - " + est.getNombreCompleto()); // Texto de salida con nombre.
            } // Fin de evaluación de tipo.
            if (est.getFoto() != null) { // Si el alumno tiene foto registrada...
                File f = new File("datos/fotos/" + est.getFoto()); // Ruta del archivo de foto.
                if (f.exists()) { // Si el archivo existe en disco...
                    ImageIcon original = new ImageIcon(f.getAbsolutePath()); // Carga la imagen.
                    Image escalada = original.getImage().getScaledInstance(TAMANO_FOTO, TAMANO_FOTO, Image.SCALE_SMOOTH); // Escala al recuadro fijo.
                    lblFoto.setIcon(new ImageIcon(escalada)); // Pone la imagen escalada.
                    lblFoto.setText(""); // Quita el texto del recuadro.
                    return; // Termina con foto mostrada.
                } // Fin de validación de archivo.
            } // Fin de validación de foto.
            lblFoto.setIcon(null); // Sin imagen disponible.
            lblFoto.setText("SIN FOTO"); // Muestra texto por defecto en el recuadro.
        } catch (SQLException ex) { // Captura error de BD.
            lblFoto.setText("SIN FOTO"); // Texto por defecto ante error.
        } // Fin del catch.
    } // Fin de mostrarFicha.

    // Pinta la fecha y hora actual en el reloj.
    private void actualizarReloj() { // Refresca el reloj.
        lblReloj.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm:ss"))); // Formato con fecha y hora.
    } // Fin de actualizarReloj.
} // Fin de la clase VentanaAsistencia.