package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Estudiante; // Entidad alumno.
import com.seq.sisas.servicio.ServicioEstudiante; // Lógica de negocio.
import com.seq.sisas.modelo.Institucion; // Entidad plantel.
import com.seq.sisas.servicio.ServicioInstitucion; // Lógica de planteles.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Lista en memoria.
import javax.swing.JComboBox; // Lista desplegable.
import java.awt.BorderLayout; // Diseño principal del diálogo.
import java.awt.Dimension; // Medidas fijas en píxeles.
import java.awt.FlowLayout; // Diseño en fila que no estira components.
import java.awt.GridBagConstraints; // Reglas de colocación de GridBagLayout.
import java.awt.GridBagLayout; // Diseño que NO estira los components.
import java.awt.Insets; // Márgenes entre components.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.io.File; // Archivo de photo seleccionado.
import java.time.LocalDate; // Fecha para calculator edad.
import java.time.Period; // Cálculo de años.
import javax.swing.JButton; // Botón.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFileChooser; // Selector de archivos del sistema.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos de aviso.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JTextField; // Campo de texto.
import javax.swing.event.DocumentEvent; // Evento de escritura.
import javax.swing.event.DocumentListener; // Escuchador de escritura.
import javax.swing.filechooser.FileNameExtensionFilter; // Filtro de tipos de archivo.
import java.awt.Image; // Imagen para mostrar la photo.
import javax.swing.ImageIcon; // Icono para mostrar la photo.

/**
 * DialogoEstudiante (V11): formulario con medidas fijas independientes de la
 * ventana.
 * CONEXIONES:
 * - Es abierto por com.seq.sisas.ui.VentanaGestionEstudiantes.
 * - Usa com.seq.sisas.servicio.ServicioEstudiante pasando la photo
 * seleccionada.
 */
public class DialogoEstudiante extends JDialog { // Hereda de JDialog (modal).

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_CAMPO = 200; // Ancho fijo de casillas de texto.
    private static final int ALTO_CAMPO = 26; // Alto fijo de casillas de texto.
    private static final int ANCHO_CAMPO_FECHA = 110; // Ancho fijo de la casilla de fecha de nacimiento.
    private static final int ANCHO_BOTON = 110; // Ancho fijo de Guardar/Cancelar.
    private static final int ALTO_BOTON = 30; // Alto fijo de Guardar/Cancelar.
    private static final int ANCHO_BOTON_FOTO = 170; // Ancho fijo del botón de foto.
    private static final int ESPACIO = 5; // Margen entre todos los componentes.
    // ============================================

    private JTextField txtNombre; // Campo nombre (obligatorio).
    private JTextField textCurp; // Campo CURP (obligatorio).
    private JComboBox<String> cmbNivel; // Lista nivel PREPA/UNIVERSIDAD.
    private JComboBox<Institucion> cmbEscuela; // Lista escuela filtrada por nivel.
    private JComboBox<String> cmbTipo; // Lista tipo solo para UNIVERSIDAD.
    private JLabel lblTipo; // Etiqueta tipo, se oculta si es PREPA.
    private JTextField txtMatricula; // Campo matrícula (opcional).
    private JTextField txtEspecialidad; // Campo especialidad (opcional).
    private JTextField txtFechaFin; // Campo fecha fin aaaa-mm-dd (opcional).
    private ServicioInstitucion servicioInstitucion; // Lógica de planteles.
    private List<Institucion> listaEscuelas; // Lista en memoria sincronizada con cmbEscuela.
    private JTextField txtCurp; // Campo CURP (obligatorio).
    private JTextField txtFechaNacimiento; // Campo fecha nacimiento (obligatorio).
    private JTextField txtDireccion; // Campo dirección (opcional).
    private JTextField txtContacto; // Campo contacto (opcional).
    private JTextField txtFecha; // Campo fecha ingreso (obligatorio).
    private JTextField txtHoras; // Campo horas (obligatorio).
    private JLabel lblEdad; // Etiqueta de edad en vivo.
    private JLabel lblFotoEstado; // Etiqueta con el nombre de la foto elegida.
    private JLabel lblFotoPreview; // Etiqueta para mostrar la foto seleccionada.
    private File fotoSeleccionada; // Archivo de imagen elegido (null si no se eligió).
    private ServicioEstudiante servicio; // Lógica de negocio.
    private Integer adminId; // ID del administrador responsable.
    private Estudiante estudianteExistente; // Null si es nuevo; lleno si es edición.

    // Constructor: owner = ventana padre; existente = null para crear nuevo.
    public DialogoEstudiante(JFrame owner, ServicioEstudiante servicio, Integer adminId, Estudiante existente) { // Recibe
                                                                                                                 // dependencias.
        super(owner, true); // true = modal.
        this.servicio = servicio; // Guarda el servicio.
        this.adminId = adminId; // Guarda el ID del responsable.
        this.estudianteExistente = existente; // Guarda estudiante a editar (o null).
        this.servicioInstitucion = new ServicioInstitucion(); // Inicializa servicio de planteles.

        setTitle(existente == null ? "Registrar Estudiante" : "Editar Estudiante"); // Título según modo.
        setSize(680, 800); // Tamaño inicial del diálogo.
        setLocationRelativeTo(owner); // Centra sobre el padre.

        // --- Casillas con medida fija ---
        txtNombre = new JTextField(); // Campo nombre.
        txtNombre.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve 200x26.
        cmbNivel = new JComboBox<>(new String[] { "PREPA", "UNIVERSIDAD" }); // Lista nivel.
        cmbNivel.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve 200x26.
        cmbEscuela = new JComboBox<>(); // Lista escuela, se llena por nivel.
        cmbEscuela.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve 200x26.
        cmbTipo = new JComboBox<>(new String[] { "SERVICIO_SOCIAL", "PRACTICAS_PROFESIONALES" }); // Lista tipo.
        cmbTipo.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve 200x26.
        lblTipo = new JLabel("Tipo (*):"); // Etiqueta tipo.
        lblTipo.setVisible(false); // Oculta al inicio, solo UNIVERSIDAD.
        cmbTipo.setVisible(false); // Oculta al inicio, solo UNIVERSIDAD.
        txtMatricula = new JTextField(); // Campo matrícula.
        txtMatricula.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve 200x26.
        txtEspecialidad = new JTextField(); // Campo especialidad.
        txtEspecialidad.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve 200x26.
        txtFechaFin = new JTextField(); // Campo fecha fin.
        txtFechaFin.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve 200x26.
        textCurp = new JTextField(); // Campo CURP.
        txtCurp.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve a 200x26.
        txtFechaNacimiento = new JTextField(); // Campo fecha nac.
        txtFechaNacimiento.setPreferredSize(new Dimension(ANCHO_CAMPO_FECHA, ALTO_CAMPO)); // Vuelve a 110x26.
        txtDireccion = new JTextField(); // Campo dirección.
        txtDireccion.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve a 200x26.
        txtContacto = new JTextField(); // Campo contacto.
        txtContacto.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve a 200x26.
        txtFecha = new JTextField(); // Campo fecha ingreso.
        txtFecha.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve a 200x26.
        txtHoras = new JTextField(); // Campo horas.
        txtHoras.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Vuelve a 200x26.
        lblEdad = new JLabel("Edad: -"); // Etiqueta de edad inicial.

        JButton btnFoto = new JButton("Cargar foto (opcional)"); // Botón para elegir imagen.
        btnFoto.setPreferredSize(new Dimension(ANCHO_BOTON_FOTO, ALTO_BOTON)); // Medida fija.
        lblFotoEstado = new JLabel("Sin foto"); // Estado inicial sin foto.
        lblFotoPreview = new JLabel(); // Etiqueta para mostrar la foto seleccionada.
        lblFotoPreview.setPreferredSize(new Dimension(120, 120)); // Tamaño fijo de la vista previa.
        lblFotoPreview.setBorder(javax.swing.BorderFactory.createEtchedBorder()); // Borde para la vista previa.

        cmbNivel.addActionListener(new ActionListener() { // Escucha cambio de nivel.
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarEscuelas();
                actualizarTipo();
            } // Recarga escuelas y muestra/oculta tipo.
        }); // Fin del listener.
        cargarEscuelas(); // Carga inicial de escuelas PREPA.
        actualizarTipo(); // Oculta tipo al inicio.

        // Si es edición, pre-llena los campos y muestra la foto actual.
        if (existente != null) { // Modo edición.
            txtNombre.setText(existente.getNombreCompleto()); // Carga nombre.
            if (existente.getNivel() != null && !existente.getNivel().isEmpty()) {
                cmbNivel.setSelectedItem(existente.getNivel());
            } // Carga nivel.
            cargarEscuelas(); // Recarga escuelas del nivel cargado.
            for (Institucion inst : listaEscuelas) {
                if (inst.getId() == existente.getInstitucionId()) {
                    cmbEscuela.setSelectedItem(inst);
                }
            } // Selecciona su escuela.
            if (existente.getTipoServicio() != null) {
                cmbTipo.setSelectedItem(existente.getTipoServicio());
            } // Carga tipo.
            txtMatricula.setText(existente.getMatricula() == null ? "" : existente.getMatricula()); // Carga matrícula.
            txtEspecialidad.setText(existente.getEspecialidad() == null ? "" : existente.getEspecialidad()); // Carga
                                                                                                             // especialidad.
            txtFechaFin.setText(existente.getFechaFin() == null ? "" : existente.getFechaFin()); // Carga fecha fin.
            actualizarTipo(); // Muestra tipo si es UNIVERSIDAD.
            txtCurp.setText(existente.getCurp()); // Carga CURP.
            txtFechaNacimiento.setText(existente.getFechaNacimiento()); // Carga fecha nac.
            txtDireccion.setText(existente.getDireccion()); // Carga dirección.
            txtContacto.setText(existente.getContacto()); // Carga contacto.
            txtFecha.setText(existente.getFechaIngreso()); // Carga fecha ingreso.
            txtHoras.setText(String.valueOf(existente.getHorasRequeridas())); // Carga horas.
            if (existente.getFoto() != null) {
                lblFotoEstado.setText("Foto actual: " + existente.getFoto());
            } // Muestra foto actual.
            calcularEdad(); // Pinta la edad inmediatamente.
        } // Fin de pre-llenado.

        // Recalcula la edad en vivo con cada tecla en fecha de nacimiento.
        txtFechaNacimiento.getDocument().addDocumentListener(new DocumentListener() { // Listener de escritura.
            @Override
            public void insertUpdate(DocumentEvent e) {
                calcularEdad();
            } // Al escribir.

            @Override
            public void removeUpdate(DocumentEvent e) {
                calcularEdad();
            } // Al borrar.

            @Override
            public void changedUpdate(DocumentEvent e) {
                calcularEdad();
            } // Al cambiar.
        }); // Fin del DocumentListener.

        // Abre el selector de archivos y guarda la imagen elegida.
        btnFoto.addActionListener(new ActionListener() { // Escucha clic en cargar foto.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                JFileChooser selector = new JFileChooser(); // Crea el selector del sistema.
                selector.setFileFilter(new FileNameExtensionFilter("Imagenes (jpg, png)", "jpg", "jpeg", "png")); // Solo
                                                                                                                  // imágenes.
                int resultado = selector.showOpenDialog(DialogoEstudiante.this); // Abre diálogo de selección.
                if (resultado == JFileChooser.APPROVE_OPTION) { // Si eligió archivo...
                    fotoSeleccionada = selector.getSelectedFile(); // Guarda el archivo en memoria.
                    lblFotoEstado.setText("Foto cargada: "); // Muestra el nombre elegido.
                    mostrarFoto(fotoSeleccionada); // Muestra la foto en la vista previa.
                } // Fin de validación de opción.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        // --- Paneles de fila que no estiran (FlowLayout) ---
        JPanel panelFechaNac = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Fila fecha + edad.
        panelFechaNac.add(txtFechaNacimiento); // Campo fecha a la izquierda.
        panelFechaNac.add(lblEdad); // Edad a su derecha.

        JPanel panelFoto = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0)); // Fila botón + estado.
        panelFoto.add(btnFoto); // Botón a la izquierda.
        panelFoto.add(lblFotoEstado); // Estado a su derecha.

        // --- Cuadrícula con GridBagLayout: nada se estira ---
        JPanel panelCampos = new JPanel(new GridBagLayout()); // Diseño que mantiene tamaños.
        GridBagConstraints gbc = new GridBagConstraints(); // Reglas de colocación.
        gbc.insets = new Insets(ESPACIO, ESPACIO, ESPACIO, ESPACIO); // Margen uniforme.

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Nivel (*):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(cmbNivel, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Nombre completo (*):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtNombre, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Escuela (*):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(cmbEscuela, gbc);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(lblTipo, gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(cmbTipo, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("CURP (*):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtCurp, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Fecha nacimiento (*):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(panelFechaNac, gbc);
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Direccion (opcional):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtDireccion, gbc);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Contacto:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtContacto, gbc);
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Fecha ingreso (*) aaaa-mm-dd:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtFecha, gbc);
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Fecha fin (aaaa-mm-dd):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtFechaFin, gbc);
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Matrícula:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtMatricula, gbc);
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Especialidad:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtEspecialidad, gbc);
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Horas requeridas (*):"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(txtHoras, gbc);
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Foto de perfil:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(panelFoto, gbc);
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.anchor = GridBagConstraints.EAST;
        panelCampos.add(new JLabel("Vista previa:"), gbc);
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panelCampos.add(lblFotoPreview, gbc);

        // --- Botones inferiores con medida fija ---
        JButton btnGuardar = new JButton("Guardar"); // Botón guardar.
        btnGuardar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnCancelar = new JButton("Cancelar"); // Botón cancelar.
        btnCancelar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JPanel panelBotones = new JPanel(new FlowLayout()); // Fila centrada que no estira.
        panelBotones.add(btnGuardar); // Agrega guardar.
        panelBotones.add(btnCancelar); // Agrega cancelar.

        add(panelCampos, BorderLayout.CENTER); // Cuadrícula al centro.
        add(panelBotones, BorderLayout.SOUTH); // Botones abajo.

        btnGuardar.addActionListener(new ActionListener() { // Escucha clic en guardar.
            @Override
            public void actionPerformed(ActionEvent e) {
                guardar();
            } // Ejecuta guardado.
        }); // Fin del listener.

        btnCancelar.addActionListener(new ActionListener() { // Escucha clic en cancelar.
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            } // Cierra sin guardar.
        }); // Fin del listener.
    } // Fin del constructor.

    // Carga en cmbEscuela solo las instituciones activas del nivel elegido.
    private void cargarEscuelas() { // Recarga escuelas por nivel.
        try { // Intenta leer la BD.
            String nivel = (String) cmbNivel.getSelectedItem(); // Nivel elegido.
            listaEscuelas = servicioInstitucion.listarPorNivel(nivel); // Obtiene solo ese nivel.
            cmbEscuela.removeAllItems(); // Limpia la lista actual.
            for (Institucion inst : listaEscuelas) {
                cmbEscuela.addItem(inst);
            } // Agrega cada plantel.
        } catch (SQLException ex) { // Captura error de BD.
            JOptionPane.showMessageDialog(this, "Error al cargar escuelas: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de cargarEscuelas.
      // Muestra Tipo solo si el nivel es UNIVERSIDAD; si es PREPA lo oculta y fija
      // SERVICIO_SOCIAL.

    private void actualizarTipo() { // Muestra/oculta tipo.
        boolean esUni = "UNIVERSIDAD".equals(cmbNivel.getSelectedItem()); // ¿Es universidad?
        lblTipo.setVisible(esUni); // Muestra etiqueta solo si es universidad.
        cmbTipo.setVisible(esUni); // Muestra lista solo si es universidad.
        if (!esUni) {
            cmbTipo.setSelectedItem("SERVICIO_SOCIAL");
        } // Prepa siempre es servicio social.
        revalidate(); // Recalcula el diseño.
        repaint(); // Repinta el diálogo.
    } // Fin de actualizarTipo.

    // Calcula y muestra la edad en número a partir de la fecha escrita.
    private void calcularEdad() { // Recalcula la edad.
        try { // Intenta interpretar la fecha.
            LocalDate nac = LocalDate.parse(txtFechaNacimiento.getText().trim()); // Convierte a fecha.
            int edad = Period.between(nac, LocalDate.now()).getYears(); // Años cumplidos.
            lblEdad.setText("Edad: " + edad); // Muestra la edad.
        } catch (Exception ex) { // Si la fecha no es válida aún...
            lblEdad.setText("Edad: -"); // ...muestra guion.
        } // Fin del catch.
    } // Fin de calcularEdad.

    // Escala la Foto a 120x120 y la muestra en lblFotoPreview
    private void mostrarFoto(File foto) { // Muestra la foto en la vista previa.
        ImageIcon original = new ImageIcon(foto.getAbsolutePath()); // Carga la imagen.
        Image escala = original.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH); // Escala suavemente.
        lblFotoPreview.setIcon(new ImageIcon(escala)); // Muestra la imagen escalada.
        lblFotoPreview.revalidate(); // Refresca el componente.
        lblFotoPreview.repaint(); // Repinta el componente.

    } // Fin de mostrarFoto.

    // Valida obligatorios y guarda (nuevo o edición) pasando la foto al servicio.
    private void guardar() { // Procesa el guardado.
        String nombre = txtNombre.getText().trim(); // Lee nombre.
        Institucion instSel = (Institucion) cmbEscuela.getSelectedItem(); // Escuela elegida.
        String escuela = instSel == null ? "" : instSel.getNombre(); // Nombre de la escuela elegida
        String curp = txtCurp.getText().trim().toUpperCase(); // Lee CURP en mayúsculas.
        String fechaNacTexto = txtFechaNacimiento.getText().trim(); // Lee fecha nac.
        String direccion = txtDireccion.getText().trim(); // Lee dirección.
        String contacto = txtContacto.getText().trim(); // Lee contacto.
        String fecha = txtFecha.getText().trim(); // Lee fecha ingreso.
        String horasTexto = txtHoras.getText().trim(); // Lee horas.

        if (nombre.isEmpty() || escuela.isEmpty() || fecha.isEmpty() || horasTexto.isEmpty()) { // Obligatorios básicos.
            JOptionPane.showMessageDialog(this, "Nombre, escuela, fecha de ingreso y horas son obligatorios.",
                    "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación básica.

        if (curp.isEmpty()) { // CURP obligatoria.
            JOptionPane.showMessageDialog(this, "La CURP es obligatoria.", "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación de CURP.

        if (!curp.matches("[A-Z]{4}\\d{6}[HM][A-Z]{5}[A-Z0-9]{2}")) { // Formato oficial CURP.
            JOptionPane.showMessageDialog(this, "Formato de CURP invalido. Debe tener 18 caracteres.", "Validacion",
                    JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación de formato.

        LocalDate fechaNac; // Variable de fecha de nacimiento.
        try { // Intenta interpretar.
            fechaNac = LocalDate.parse(fechaNacTexto); // Convierte y valida.
        } catch (Exception ex) { // Si es inválida...
            JOptionPane.showMessageDialog(this, "Fecha de nacimiento obligatoria e invalida. Use aaaa-mm-dd.",
                    "Validacion", JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación de fecha.

        int horas; // Variable numérica de horas.
        try { // Intenta convertir.
            horas = Integer.parseInt(horasTexto); // Convierte a int.
        } catch (NumberFormatException ex) { // Si no es número...
            JOptionPane.showMessageDialog(this, "Las horas deben ser un numero valido.", "Validacion",
                    JOptionPane.WARNING_MESSAGE); // Avisa.
            return; // Corta ejecución.
        } // Fin de validación numérica.

        try { // Intenta guardar en base de datos.
            if (estudianteExistente == null) { // Modo creación.
                Estudiante nuevo = new Estudiante(); // Crea objeto vacío.
                nuevo.setNombreCompleto(nombre); // Asigna nombre.
                nuevo.setEscuela(escuela); // Asigna escuela.
                nuevo.setCurp(curp); // Asigna CURP.
                nuevo.setFechaNacimiento(fechaNac.toString()); // Asigna fecha nac.
                nuevo.setDireccion(direccion); // Asigna dirección.
                nuevo.setContacto(contacto); // Asigna contacto.
                nuevo.setFechaIngreso(fecha); // Asigna fecha ingreso.
                nuevo.setHorasRequeridas(horas); // Asigna horas.
                String codigo = servicio.registrarEstudiante(nuevo, adminId, fotoSeleccionada); // Registra con foto
                                                                                                // opcional.
                JOptionPane.showMessageDialog(this, "Estudiante registrado.\nCódigo QR: " + codigo, "Éxito",
                        JOptionPane.INFORMATION_MESSAGE); // Muestra código.
            } else { // Modo edición.
                estudianteExistente.setNombreCompleto(nombre); // Actualiza nombre.
                estudianteExistente.setEscuela(escuela); // Actualiza escuela.
                estudianteExistente.setCurp(curp); // Actualiza CURP.
                estudianteExistente.setFechaNacimiento(fechaNac.toString()); // Actualiza fecha nac.
                estudianteExistente.setDireccion(direccion); // Actualiza dirección.
                estudianteExistente.setContacto(contacto); // Actualiza contacto.
                estudianteExistente.setFechaIngreso(fecha); // Actualiza fecha ingreso.
                estudianteExistente.setHorasRequeridas(horas); // Actualiza horas.
                servicio.actualizarEstudiante(estudianteExistente, adminId, fotoSeleccionada); // Actualiza con foto
                                                                                               // opcional.
                JOptionPane.showMessageDialog(this, "Datos actualizados correctamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE); // Confirma.
            } // Fin de evaluación de modo.
            dispose(); // Cierra el diálogo.
        } catch (Exception ex) { // Captura error de BD, CURP duplicada o archivo.
            JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de guardar.
} // Fin de la clase DialogoEstudiante.