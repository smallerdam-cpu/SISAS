package com.seq.sisas.utilidad; // Paquete de utilidades del sistema.

import java.io.File; // Manejo de carpetas y archivos.
import java.io.IOException; // Errores de copia.
import java.nio.file.Files; // Copia moderna de archivos.
import java.nio.file.StandardCopyOption; // Reemplazar archivos existentes.

/**
 * RespaldoUtil: calcula pesos y copia carpetas completas de forma recursiva.
 * CONEXIONES:
 * - Es usada por com.seq.sisas.ui.DialogoRespaldo.
 * - No depende de ninguna otra clase del sistema.
 */
public class RespaldoUtil { // Clase utilitaria estática.

    // Calcula el peso total en bytes de una carpeta, incluyendo subcarpetas.
    public static long calcularTamano(File carpeta) { // Recibe la carpeta a medir.
        long total = 0; // Acumulador de bytes.
        File[] archivos = carpeta.listFiles(); // Lista el contenido (null si no existe).
        if (archivos == null) { return 0; } // Carpeta inexistente: peso cero.
        for (File f : archivos) { // Recorre cada elemento.
            total += f.isDirectory() ? calcularTamano(f) : f.length(); // Suma subcarpetas en recursión o el peso del archivo.
        } // Fin del recorrido.
        return total; // Devuelve el peso total en bytes.
    } // Fin de calcularTamano.

    // Convierte bytes a texto legible (B, KB, MB o GB).
    public static String formatearPeso(long bytes) { // Recibe bytes.
        if (bytes < 1024) { return bytes + " B"; } // Menos de 1 KB: bytes.
        if (bytes < 1024 * 1024) { return String.format("%.2f KB", bytes / 1024.0); } // Menos de 1 MB: kilobytes.
        if (bytes < 1024L * 1024 * 1024) { return String.format("%.2f MB", bytes / (1024.0 * 1024)); } // Menos de 1 GB: megabytes.
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024)); // Gigabytes con dos decimales.
    } // Fin de formatearPeso.

    // Copia recursivamente una carpeta completa hacia el destino.
    public static void copiarCarpeta(File origen, File destino) throws IOException { // Lanza error si falla la copia.
        if (!destino.exists()) { destino.mkdirs(); } // Crea la carpeta destino si no existe.
        File[] archivos = origen.listFiles(); // Lista el contenido del origen.
        if (archivos == null) { return; } // Origen inexistente: nada que copiar.
        for (File f : archivos) { // Recorre cada elemento.
            File d = new File(destino, f.getName()); // Archivo o carpeta espejo en el destino.
            if (f.isDirectory()) { copiarCarpeta(f, d); } else { Files.copy(f.toPath(), d.toPath(), StandardCopyOption.REPLACE_EXISTING); } // Recursión o copia con reemplazo.
        } // Fin del recorrido.
    } // Fin de copiarCarpeta.

        // Borra recursivamente una carpeta y todo su contenido.
    public static void borrarCarpeta(File carpeta) { // Recibe la carpeta a eliminar.
        File[] archivos = carpeta.listFiles(); // Lista el contenido (null si no existe).
        if (archivos != null) { // Si tiene contenido...
            for (File f : archivos) { // Recorre cada elemento.
                if (f.isDirectory()) { borrarCarpeta(f); } else { f.delete(); } // Recursión en subcarpetas o borra el archivo.
            } // Fin del recorrido.
        } // Fin de validación.
        carpeta.delete(); // Borra la carpeta ya vacía.
    } // Fin de borrarCarpeta.
} // Fin de la clase RespaldoUtil.