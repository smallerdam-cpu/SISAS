package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import java.awt.BorderLayout; // Diseño principal.
import java.awt.Dimension; // Tamaño fijo del recuadro de imagen.
import java.awt.GridLayout; // Diseño en cuadrícula para textos.
import java.awt.Image; // Para escalar la imagen.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.awt.image.BufferedImage; // Imagen decodificada en memoria.
import java.io.InputStream; // Flujo de lectura del recurso embebido.
import javax.imageio.ImageIO; // Decodificador de png/jpg.
import javax.swing.ImageIcon; // Envoltorio de imagen para Swing.
import javax.swing.JButton; // Botón.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JPanel; // Contenedor.

/**
 * DialogoAcercaDe: cuadro institucional con imagen EMBEBIDA en el sistema.
 * CONEXIONES:
 * - Es abierto desde el menú Ayuda de com.seq.sisas.ui.VentanaPrincipal.
 * - Lee la imagen compilada en src/main/resources/imagenes/logo.png (o .jpg).
 */
public class DialogoAcercaDe extends JDialog { // Hereda de JDialog (modal).

    // ====== EDITA ESTAS DOS LÍNEAS CON TUS DATOS REALES ======
    private static final String CREADOR = "Infra-Red - ROBERTO SMITH HERNANDEZ RAMIREZ"; // Nombre del creador del sistema.
    private static final String VERSION = "Ver. 1.0.0"; // Versión oficial del sistema.
    // ==========================================================

    private JLabel lblImagen; // Recuadro que muestra la imagen institucional.

    // Constructor: owner = ventana principal.
    public DialogoAcercaDe(JFrame owner) { // Recibe al padre.
        super(owner, true); // true = modal.
        setTitle("Acerca de SISAS"); // Título del cuadro.
        setSize(430, 480); // Tamaño del cuadro.
        setLocationRelativeTo(owner); // Centra sobre el padre.
        setResizable(false); // Bloquea redimensionado.

        lblImagen = new JLabel("SIN IMAGEN", JLabel.CENTER); // Recuadro por defecto si no hay recurso.
        lblImagen.setOpaque(true); // Fondo sólido.
        lblImagen.setBackground(new java.awt.Color(240, 240, 240)); // Gris muy claro.
        lblImagen.setPreferredSize(new Dimension(220, 140)); // Tamaño fijo del recuadro.
        cargarImagenEmbebida(); // Busca la imagen dentro del sistema.

        JLabel lblNombre = new JLabel("SISAS", JLabel.CENTER); // Nombre del sistema.
        lblNombre.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 22)); // Fuente grande.
        JLabel lblDescripcion = new JLabel("Software estatal desarrollado especial mente para SEQ", JLabel.CENTER); // Descripción oficial.
        JLabel lblVersion = new JLabel(VERSION, JLabel.CENTER); // Etiqueta de versión.
        JLabel lblCreador = new JLabel("Creador: " + CREADOR, JLabel.CENTER); // Etiqueta de creador.
        JLabel lblDependencia = new JLabel("Depto. de Media Superior y Superior - SEQ", JLabel.CENTER); // Dependencia de gobierno.

        JPanel panelTextos = new JPanel(new GridLayout(5, 1, 3, 3)); // Cuadrícula vertical de textos.
        panelTextos.add(lblNombre); // Agrega nombre.
        panelTextos.add(lblDescripcion); // Agrega descripción.
        panelTextos.add(lblVersion); // Agrega versión.
        panelTextos.add(lblCreador); // Agrega creador.
        panelTextos.add(lblDependencia); // Agrega dependencia.

        JButton btnAceptar = new JButton("Aceptar"); // Único botón, cierra el cuadro (como en SIOSAD).
        btnAceptar.addActionListener(new ActionListener() { // Escucha clic en aceptar.
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); } // Cierra el cuadro.
        }); // Fin del listener.
        JPanel panelBotones = new JPanel(); // Panel inferior centrado.
        panelBotones.add(btnAceptar); // Agrega aceptar.

        JPanel panelImagen = new JPanel(); // Panel centrado para el recuadro.
        panelImagen.add(lblImagen); // Agrega el recuadro de imagen.

        add(panelImagen, BorderLayout.NORTH); // Imagen arriba.
        add(panelTextos, BorderLayout.CENTER); // Textos al centro.
        add(panelBotones, BorderLayout.SOUTH); // Botón abajo.
    } // Fin del constructor.

    // Lee la imagen compilada dentro del sistema (recurso de classpath).
    private void cargarImagenEmbebida() { // Carga el logo embebido.
        String[] candidatos = {"/imagenes/logo.png", "/imagenes/logo.jpg"}; // Nombres aceptados, en orden de búsqueda.
        for (String ruta : candidatos) { // Prueba cada candidato.
            InputStream is = getClass().getResourceAsStream(ruta); // Busca el recurso dentro de classes/jar.
            if (is != null) { // Si la imagen existe en el sistema...
                try { // Intenta decodificarla.
                    BufferedImage img = ImageIO.read(is); // Convierte el flujo en imagen.
                    Image escalada = img.getScaledInstance(210, 130, Image.SCALE_SMOOTH); // Escala al recuadro.
                    lblImagen.setIcon(new ImageIcon(escalada)); // Pone la imagen.
                    lblImagen.setText(""); // Quita el texto por defecto.
                    is.close(); // Cierra el flujo de lectura.
                } catch (Exception ex) { // Si falla la decodificación...
                    // ...se mantiene el texto "SIN IMAGEN" sin romper el cuadro.
                } // Fin del catch.
                return; // Termina con la primera imagen encontrada.
            } // Fin de validación de recurso.
        } // Fin del recorrido de candidatos.
    } // Fin de cargarImagenEmbebida.
} // Fin de la clase DialogoAcercaDe.