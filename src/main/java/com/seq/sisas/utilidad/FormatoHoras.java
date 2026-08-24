package com.seq.sisas.utilidad; // Paquete de utilidades del sistema.

/**
 * FormatoHoras: convierte horas decimales a texto legible sin ruido de coma flotante.
 * CONEXIONES:
 * - Es usada por com.seq.sisas.ui.VentanaGestionEstudiantes (columna Horas).
 * - Reutilizable en futuros reportes y pantallas.
 */
public class FormatoHoras { // Clase utilitaria estática.

    // Redondea al minuto más cercano y formatea como "Xh Ym" (ej. 0.3333 -> "0h 20m").
    public static String horasMinutos(double horas) { // Recibe horas decimales acumuladas.
        long totalMinutos = Math.round(horas * 60); // Convierte a minutos y redondea (elimina el ruido decimal).
        long h = totalMinutos / 60; // Extrae las horas completas.
        long m = totalMinutos % 60; // Extrae los minutos restantes.
        return h + "h " + m + "m"; // Devuelve texto humano cerrado, sin decimales infinitos.
    } // Fin de horasMinutos.
} // Fin de la clase FormatoHoras.