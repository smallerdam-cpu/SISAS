package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.utilidad.RespaldoUtil; // Utilidad de pesos, copias y borrado.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Medidas fijas.
import java.awt.GridBagConstraints; // Reglas de GridBagLayout.
import java.awt.GridBagLayout; // Diseño que no estira.
import java.awt.Insets; // Márgenes.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.io.File; // Carpetas de origen y destino.
import java.io.IOException; // Error de resguardo.
import java.time.LocalDate; // Fecha actual para el nombre del periodo.
import javax.swing.JButton; // Botón.
import javax.swing.JComboBox; // Selector de periodo.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFileChooser; // Selector de carpetas.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JTextField; // Campo de ruta destino.

/**
 * DialogoRespaldo (V15): respaldo general, restauración segura y peso informado.
 * CONEXIONES:
 * - Es abierto desde el menú Administración de com.seq.sisas.ui.VentanaPrincipal.
 * - Usa com.seq.sisas.utilidad.RespaldoUtil para medir, copiar y resguardar datos/.
 */
public class DialogoRespaldo extends JDialog { // Hereda de JDialog (modal).

    // ====== MEDIDAS EDITABLES (en píxeles) ======
    private static final int ANCHO_CAMPO = 260; // Ancho fijo del campo de ruta.
    private static final int ALTO_CAMPO = 26; // Alto fijo de los campos.
    private static final int ANCHO_BOTON = 150; // Ancho fijo de los botones.
    private static final int ALTO_BOTON = 30; // Alto fijo de los botones.
    private static final int ESPACIO = 6; // Margen entre componentes.
    // ============================================

    private JComboBox<String> comboPeriodo; // Selector Anual / Semestral.
    private JTextField txtDestino; // Ruta donde se guardará el respaldo.
    private JLabel lblPesoActual; // Etiqueta con el peso actual de datos/.
    private JLabel lblResultado; // Etiqueta con el resultado de la última operación.

    // Constructor: owner = ventana principal.
    public DialogoRespaldo(JFrame owner) { // Recibe al padre.
        super(owner, true); // true = modal.
        setTitle("Respaldo y Restauración de Información"); // Título del diálogo.
        setSize(540, 300); // Tamaño ampliado para el botón de restaurar.
        setLocationRelativeTo(owner); // Centra sobre el padre.
        setResizable(false); // Bloquea redimensionado.

        lblPesoActual = new JLabel(pesoActual()); // Muestra el peso actual de datos/ al abrir.
        comboPeriodo = new JComboBox<>(new String[]{"Anual", "Semestral (6 meses)"}); // Opciones de periodo.
        comboPeriodo.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.
        txtDestino = new JTextField(System.getProperty("user.home") + "\\Documents\\RespaldoSISAS"); // Ruta por defecto en Documentos.
        txtDestino.setPreferredSize(new Dimension(ANCHO_CAMPO, ALTO_CAMPO)); // Medida fija.
        lblResultado = new JLabel("Sin operaciones en esta sesión.", JLabel.CENTER); // Estado inicial.

        JButton btnElegir = new JButton("Elegir carpeta"); // Botón para elegir destino del respaldo.
        btnElegir.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnRealizar = new JButton("Realizar Respaldo"); // Botón que ejecuta la copia de seguridad.
        btnRealizar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.
        JButton btnRestaurar = new JButton("Restaurar Respaldo"); // Botón que importa un respaldo al sistema.
        btnRestaurar.setPreferredSize(new Dimension(ANCHO_BOTON, ALTO_BOTON)); // Medida fija.

        JPanel panel = new JPanel(new GridBagLayout()); // Cuadrícula que no estira.
        GridBagConstraints gbc = new GridBagConstraints(); // Reglas de colocación.
        gbc.insets = new Insets(ESPACIO, ESPACIO, ESPACIO, ESPACIO); // Margen uniforme.

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; // Etiqueta centrada en 2 columnas.
        panel.add(lblPesoActual, gbc); // Muestra el peso actual arriba.
        gbc.gridwidth = 1; // Vuelve a una columna.

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Periodo:"), gbc); // Etiqueta de periodo.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Control a la izquierda.
        panel.add(comboPeriodo, gbc); // Selector de periodo.

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST; // Etiqueta a la derecha.
        panel.add(new JLabel("Destino:"), gbc); // Etiqueta de destino.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Control a la izquierda.
        panel.add(txtDestino, gbc); // Campo de ruta destino.

        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST; // Botón a la derecha.
        panel.add(btnElegir, gbc); // Botón elegir carpeta.
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; // Botón a la izquierda.
        panel.add(btnRealizar, gbc); // Botón realizar respaldo.

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER; // Botón centrado en 2 columnas.
        panel.add(btnRestaurar, gbc); // Botón de restauración abajo.
        gbc.gridwidth = 1; // Restaura una columna.

        add(panel, BorderLayout.CENTER); // Cuadrícula al centro.
        add(lblResultado, BorderLayout.SOUTH); // Resultado abajo.

        btnElegir.addActionListener(new ActionListener() { // Escucha clic en elegir carpeta.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                JFileChooser selector = new JFileChooser(); // Selector del sistema.
                selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Solo carpetas.
                if (selector.showOpenDialog(DialogoRespaldo.this) == JFileChooser.APPROVE_OPTION) { // Si eligió carpeta...
                    txtDestino.setText(selector.getSelectedFile().getAbsolutePath()); // Escribe la ruta elegida.
                } // Fin de validación.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        btnRealizar.addActionListener(new ActionListener() { // Escucha clic en realizar respaldo.
            @Override
            public void actionPerformed(ActionEvent e) { realizarRespaldo(); } // Ejecuta el respaldo.
        }); // Fin del listener.

        btnRestaurar.addActionListener(new ActionListener() { // Escucha clic en restaurar.
            @Override
            public void actionPerformed(ActionEvent e) { restaurarRespaldo(); } // Ejecuta la restauración.
        }); // Fin del listener.
    } // Fin del constructor.

    // Devuelve el texto con el peso actual de la carpeta datos/.
    private String pesoActual() { // Mide la información viva.
        return "Peso actual de la información: " + RespaldoUtil.formatearPeso(RespaldoUtil.calcularTamano(new File("datos"))); // Formato legible.
    } // Fin de pesoActual.

    // Genera el nombre de la carpeta del respaldo según el periodo elegido.
    private String nombrePeriodo() { // Nombre oficial del respaldo.
        LocalDate hoy = LocalDate.now(); // Fecha de hoy.
        int anio = hoy.getYear(); // Año en curso.
        if (comboPeriodo.getSelectedIndex() == 0) { return "SISAS_Respaldo_" + anio; } // Anual: solo el año.
        int semestre = hoy.getMonthValue() <= 6 ? 1 : 2; // Semestre según el mes en curso.
        return "SISAS_Respaldo_" + anio + "_S" + semestre; // Semestral: año y semestre.
    } // Fin de nombrePeriodo.

    // Copia toda la carpeta datos/ al destino y reporta ruta y peso del respaldo.
    private void realizarRespaldo() { // Ejecuta el respaldo completo.
        File origen = new File("datos"); // Carpeta viva del sistema.
        if (!origen.exists()) { // Si no hay información que respaldar...
            JOptionPane.showMessageDialog(this, "No existe la carpeta datos/ para respaldar.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return; // Corta ejecución.
        } // Fin de validación.
        String rutaDestino = txtDestino.getText().trim(); // Lee la ruta destino.
        if (rutaDestino.isEmpty()) { // Si falta destino...
            JOptionPane.showMessageDialog(this, "Elija o escriba una carpeta de destino.", "Aviso", JOptionPane.WARNING_MESSAGE); // ...avisa.
            return; // Corta ejecución.
        } // Fin de validación.
        try { // Intenta copiar todo.
            File destino = new File(rutaDestino, nombrePeriodo()); // Carpeta del respaldo con nombre de periodo.
            RespaldoUtil.copiarCarpeta(origen, destino); // Copia recursiva completa.
            String peso = RespaldoUtil.formatearPeso(RespaldoUtil.calcularTamano(destino)); // Pesa el respaldo generado.
            lblResultado.setText("Respaldo completado: " + destino.getAbsolutePath() + " | Peso: " + peso); // Reporta en pantalla.
            JOptionPane.showMessageDialog(this, "Respaldo completado.\nRuta: " + destino.getAbsolutePath() + "\nPeso del respaldo: " + peso, "Éxito", JOptionPane.INFORMATION_MESSAGE); // Confirma con detalle.
        } catch (Exception ex) { // Captura error de copia o disco.
            JOptionPane.showMessageDialog(this, "Error al respaldar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
        } // Fin del catch.
    } // Fin de realizarRespaldo.

    // Importa un respaldo válido al sistema, resguardando antes la información actual.
    private void restaurarRespaldo() { // Ejecuta la restauración segura.
        JFileChooser selector = new JFileChooser(); // Selector del sistema.
        selector.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Solo carpetas.
        if (selector.showOpenDialog(DialogoRespaldo.this) != JFileChooser.APPROVE_OPTION) { return; } // Corta si canceló.
        File origen = selector.getSelectedFile(); // Carpeta de respaldo elegida.
        if (!new File(origen, "sisas.db").exists()) { // Validación de respaldo válido.
            JOptionPane.showMessageDialog(this, "La carpeta seleccionada no contiene sisas.db.\nNo parece un respaldo válido de SISAS.", "Validacion", JOptionPane.WARNING_MESSAGE); // Rechaza carpetas ajenas.
            return; // Corta ejecución.
        } // Fin de validación.
        int confirmacion = JOptionPane.showConfirmDialog(this, // Advertencia clara antes de pisar datos.
            "Se REEMPLAZARA toda la información actual por la del respaldo elegido." + // Consecuencia principal.
            "\nAntes, lo actual se resguardara en 'datos_respaldo_previo'." + // Red de seguridad.
            "\n¿Desea continuar?", // Pregunta final.
            "Restaurar respaldo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); // Título y tipo.
        if (confirmacion != JOptionPane.YES_OPTION) { return; } // Corta si no confirmó.
        try { // Intenta la restauración segura.
            File datos = new File("datos"); // Carpeta viva del sistema.
            File previo = new File("datos_respaldo_previo"); // Carpeta de resguardo previo.
            if (previo.exists()) { RespaldoUtil.borrarCarpeta(previo); } // Limpia resguardos anteriores.
            if (datos.exists() && !datos.renameTo(previo)) { throw new IOException("No se pudo resguardar la carpeta actual."); } // Mueve lo actual a resguardo.
            RespaldoUtil.copiarCarpeta(origen, datos); // Copia el respaldo como carpeta viva.
            lblResultado.setText("Restauración completada desde: " + origen.getAbsolutePath()); // Reporta en pantalla.
            lblPesoActual.setText(pesoActual()); // Recalcula el peso mostrado.
            JOptionPane.showMessageDialog(this, "Restauración completada.\nREINICIE el programa para aplicar la información restaurada.", "Éxito", JOptionPane.INFORMATION_MESSAGE); // Pide reinicio para aplicar todo.
        } catch (Exception ex) { // Captura error de copia o disco.
            JOptionPane.showMessageDialog(this, "Error al restaurar: " + ex.getMessage() + "\nSu información previa quedó en 'datos_respaldo_previo'.", "Error", JOptionPane.ERROR_MESSAGE); // Informa y señala el resguardo.
        } // Fin del catch.
    } // Fin de restaurarRespaldo.
} // Fin de la clase DialogoRespaldo.
