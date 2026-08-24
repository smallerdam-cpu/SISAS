package com.seq.sisas.utilidad; // Paquete de utilidades del sistema.

import java.io.File; // Manejo del archivo de configuración.
import java.io.FileInputStream; // Lectura del archivo.
import java.io.FileOutputStream; // Escritura del archivo.
import java.io.InputStreamReader; // Lectura con codificación UTF-8 (acentos).
import java.io.OutputStreamWriter; // Escritura con codificación UTF-8.
import java.nio.charset.StandardCharsets; // Constante de codificación UTF-8.
import java.util.Properties; // Clase de Java para archivos clave=valor.

/**
 * ConfiguracionSistema: datos editables SIN reinstalar ni recompilar.
 * CONEXIONES:
 * - Es leída por com.seq.sisas.ui.PanelHojaReporte y com.seq.sisas.servicio.ServicioReportes.
 * - Es escrita por com.seq.sisas.ui.DialogoConfiguracion.
 * - El archivo vive en datos/configuracion.properties, junto a la base de datos.
 */
public class ConfiguracionSistema { // Clase utilitaria estática.

    // Ruta del archivo editable que viaja con la instalación.
    private static final String RUTA = "datos/configuracion.properties"; // Archivo de configuración en disco.

    // Devuelve el nombre de la directora configurado (o el valor por defecto).
    public static String obtenerNombreDirectora() { // Lectura segura.
        return obtener("nombre_directora", "Mtra. Nombre Apellidos"); // Clave y valor por defecto.
    } // Fin de obtenerNombreDirectora.

    // Devuelve el cargo configurado (o el valor por defecto).
    public static String obtenerCargoDirectora() { // Lectura segura.
        return obtener("cargo_directora", "Directora de Educación Media Superior y Preparatoria Abierta"); // Clave y valor por defecto.
    } // Fin de obtenerCargoDirectora.

    // Guarda nombre y cargo en el archivo; crea la carpeta si no existe.
    public static void guardar(String nombre, String cargo) throws Exception { // Lanza Exception si falla I/O.
        Properties p = new Properties(); // Contenedor clave=valor.
        p.setProperty("nombre_directora", nombre); // Asigna el nombre nuevo.
        p.setProperty("cargo_directora", cargo); // Asigna el cargo nuevo.
        File f = new File(RUTA); // Archivo destino.
        File carpeta = f.getParentFile(); // Carpeta "datos".
        if (carpeta != null && !carpeta.exists()) { carpeta.mkdirs(); } // La crea si no existe.
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) { // Escritor UTF-8.
            p.store(writer, "Configuracion SISAS - editable sin reinstalar"); // Escribe el archivo con comentario.
        } // Cierra el escritor.
    } // Fin de guardar.

    // Lee una clave del archivo; si el archivo no existe o falla, devuelve el valor por defecto.
    private static String obtener(String clave, String porDefecto) { // Lectura con respaldo.
        Properties p = new Properties(); // Contenedor de lectura.
        File f = new File(RUTA); // Archivo de configuración.
        if (f.exists()) { // Si el archivo ya existe...
            try (InputStreamReader reader = new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8)) { // Lector UTF-8.
                p.load(reader); // Carga las claves guardadas.
            } catch (Exception ex) { // Si falla la lectura...
                // ...se ignora y se usa el valor por defecto (el sistema nunca se rompe).
            } // Fin del catch.
        } // Fin de validación de existencia.
        return p.getProperty(clave, porDefecto); // Devuelve lo guardado o el defecto.
    } // Fin de obtener.
} // Fin de la clase ConfiguracionSistema.