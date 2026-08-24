package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Asistencia; // Entidad de movimiento.
import com.seq.sisas.modelo.Estudiante; // Entidad de alumno.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.Color; // Color de fondo del visor.
import java.awt.Dimension; // Medidas fijas.
import java.awt.Graphics; // Lápiz de impresión.
import java.awt.Graphics2D; // Lápiz avanzado de impresión.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import java.awt.print.PageFormat; // Formato de página de la impresora.
import java.awt.print.Printable; // Interfaz de documento imprimible.
import java.awt.print.PrinterException; // Error de impresión.
import java.awt.print.PrinterJob; // Trabajo de impresión del sistema.
import java.util.List; // Lista de movimientos.
import javax.swing.JButton; // Botón.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JOptionPane; // Diálogos.
import javax.swing.JPanel; // Contenedor.
import javax.swing.JScrollPane; // Scroll para la hoja.

/**
 * DialogoVistaPrevia: muestra la hoja oficial y la imprime tal cual se ve (WYSIWYG).
 * CONEXIONES:
 * - Es abierto por com.seq.sisas.ui.VentanaReportes tras consultar.
 * - Contiene y dibuja com.seq.sisas.ui.PanelHojaReporte.
 */
public class DialogoVistaPrevia extends JDialog { // Hereda de JDialog.

    private PanelHojaReporte hoja; // La hoja oficial dibujada.

    // Constructor: recibe alumno y movimientos consultados.
    public DialogoVistaPrevia(JFrame owner, Estudiante estudiante, List<Asistencia> asistencias) { // Recibe datos.
        super(owner, false); // false = no modal: permite moverse entre ventanas.
        setTitle("Vista Previa - Reporte Oficial"); // Título del visor.
        setSize(780, 900); // Tamaño del visor.
        setLocationRelativeTo(owner); // Centra sobre el padre.

        hoja = new PanelHojaReporte(estudiante, asistencias); // Crea la hoja con los datos.
        JScrollPane scroll = new JScrollPane(hoja); // Scroll por si la hoja es más alta que el visor.
        scroll.getViewport().setBackground(new Color(120, 120, 120)); // Fondo gris tipo visor de impresión.
        scroll.setViewportView(hoja); // La hoja como contenido.

        JButton btnImprimir = new JButton("Imprimir"); // Botón de impresión real.
        btnImprimir.setPreferredSize(new Dimension(130, 30)); // Medida fija.
        JButton btnCerrar = new JButton("Cerrar"); // Botón de cierre.
        btnCerrar.setPreferredSize(new Dimension(130, 30)); // Medida fija.
        JPanel panelBotones = new JPanel(); // Fila inferior centrada.
        panelBotones.add(btnImprimir); // Agrega imprimir.
        panelBotones.add(btnCerrar); // Agrega cerrar.

        add(scroll, BorderLayout.CENTER); // Hoja al centro.
        add(panelBotones, BorderLayout.SOUTH); // Botones abajo.

        btnImprimir.addActionListener(new ActionListener() { // Escucha clic en imprimir.
            @Override
            public void actionPerformed(ActionEvent e) { imprimir(); } // Lanza la impresión.
        }); // Fin del listener.

        btnCerrar.addActionListener(new ActionListener() { // Escucha clic en cerrar.
            @Override
            public void actionPerformed(ActionEvent e) { dispose(); } // Cierra el visor.
        }); // Fin del listener.
    } // Fin del constructor.

    // Envía la hoja a la impresora exactamente como se ve en pantalla.
    private void imprimir() { // Impresión WYSIWYG.
        PrinterJob job = PrinterJob.getPrinterJob(); // Crea el trabajo de impresión.
        job.setPrintable(new Printable() { // Define el documento imprimible.
            @Override
            public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) { // Pinta cada página.
                if (pageIndex > 0) { return Printable.NO_SUCH_PAGE; } // Solo existe una página.
                Graphics2D g2 = (Graphics2D) graphics; // Lápiz avanzado de la impresora.
                g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY()); // Margen imprimible.
                double escala = Math.min(pageFormat.getImageableWidth() / PanelHojaReporte.ANCHO, pageFormat.getImageableHeight() / PanelHojaReporte.ALTO); // Ajusta la hoja al papel.
                g2.scale(escala, escala); // Escala sin deformar.
                hoja.printAll(g2); // Dibuja la hoja completa en el papel.
                return Printable.PAGE_EXISTS; // Confirma que hay página.
            } // Fin de print.
        }); // Fin del Printable.
        if (job.printDialog()) { // Abre el diálogo de impresora del sistema.
            try { // Intenta imprimir.
                job.print(); // Envía a la impresora seleccionada.
            } catch (PrinterException ex) { // Captura error de impresión.
                JOptionPane.showMessageDialog(this, "Error al imprimir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); // Muestra error.
            } // Fin del catch.
        } // Fin de validación de diálogo.
    } // Fin de imprimir.
} // Fin de la clase DialogoVistaPrevia.