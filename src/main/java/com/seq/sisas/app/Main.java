package com.seq.sisas.app; // Paquete de arranque del sistema SISAS.

import com.seq.sisas.db.InicializadorSistema; // Garantiza que la BD exista.
import com.seq.sisas.ui.VentanaLogin; // Ventana gráfica de autenticación.
import java.sql.SQLException; // Captura errores de base de datos.
import javax.swing.SwingUtilities; // Utilidad para manejar el hilo de interfaz gráfica (EDT).
import javax.swing.UIManager; // Para cambiar el aspecto visual de las ventanas.

/**
 * Main (V1.3): punto de entrada definitivo con Interfaz Gráfica.
 * CONEXIONES:
 * - Ejecuta com.seq.sisas.db.InicializadorSistema para preparar SQLite.
 * - Lanza com.seq.sisas.ui.VentanaLogin en el hilo gráfico de Java.
 */
public class Main { // Clase principal.

    public static void main(String[] args) { // Punto de entrada.

        // 1. Preparar la base de datos antes de abrir cualquier ventana.
        try { // Intenta inicializar SQLite.
            InicializadorSistema.inicializar(); // Crea tablas y admin si faltan.
        } catch (SQLException e) { // Si falla la BD, el programa no puede continuar.
            System.out.println("ERROR CRITICO AL INICIAR BD: " + e.getMessage()); // Imprime en consola.
            return; // Aborta la ejecución del programa inmediatamente.
        } // Fin de manejo de error crítico.

        // 2. Configurar el aspecto visual de Windows para que no parezca una app antigua de Java.
        try { // Intenta aplicar el tema nativo del sistema operativo.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); // Usa los bordes y fuentes de Windows.
        } catch (Exception e) { // Si falla, usa el tema por defecto de Java (no es crítico).
            e.printStackTrace(); // Imprime la traza del error menor.
        } // Fin de configuración visual.

        // 3. Lanzar la interfaz gráfica en el hilo correcto (Event Dispatch Thread).
        // SwingUtilities.invokeLater garantiza que la ventana se construya de forma segura y sin congelamientos.
        SwingUtilities.invokeLater(new Runnable() { // Crea una tarea para el hilo gráfico.
            @Override
            public void run() { // Método que se ejecuta en el hilo de la interfaz.
                VentanaLogin login = new VentanaLogin(); // Instancia la ventana de acceso.
                login.setVisible(true); // La hace visible en pantalla.
            } // Fin del método run.
        }); // Fin de invokeLater.
    } // Fin del método main.
} // Fin de la clase Main.