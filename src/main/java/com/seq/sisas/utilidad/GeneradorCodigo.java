package com.seq.sisas.utilidad; // Paquete de utilidades del sistema.

import java.util.UUID; // Clase de Java para generar identificadores únicos universales.

/**
 * GeneradorCodigo: crea tokens alfanuméricos únicos para los QR.
 * CONEXIONES:
 * - Invocada por com.seq.sisas.servicio.ServicioEstudiante al registrar un alumno.
 */
public class GeneradorCodigo { // Clase utilitaria estática.

    // Genera un código de 8 caracteres (ej. "A1B2-C3D4" sin guiones).
    public static String generar() { // Método estático, no requiere instanciar la clase.
        String uuid = UUID.randomUUID().toString(); // Genera un UUID largo (36 caracteres).
        return uuid.replace("-", "").substring(0, 8).toUpperCase(); // Limpia guiones, toma 8 chars y los hace mayúsculas.
    }
}