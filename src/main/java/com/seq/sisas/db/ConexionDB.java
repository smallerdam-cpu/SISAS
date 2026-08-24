package com.seq.sisas.db; // Paquete de acceso a datos del sistema SISAS.

import java.io.File; // Para crear la carpeta "datos" si no existe.
import java.sql.Connection; // Representa la conexión activa con la base de datos.
import java.sql.DriverManager; // Fábrica de conexiones JDBC.
import java.sql.SQLException; // Para capturar errores de base de datos.

/**
 * ConexionDB: administra la conexión con la base de datos local SQLite.
 * CONEXIONES:
 * - Es usada por com.seq.sisas.db.InicializadorSistema (creación de tablas).
 * - Será usada por com.seq.sisas.repositorio.* (DAOs) en V1.2.
 * - No depende de ninguna otra clase del sistema.
 */
public class ConexionDB { // Clase utilitaria de conexión.

    // Ruta del archivo de base de datos: carpeta "datos" dentro del proyecto.
    private static final String RUTA_DB = "datos/sisas.db"; // Constante: ubicación única de la BD.

    // Método estático que entrega una conexión lista para usarse.
    public static Connection conectar() throws SQLException { // Lanza SQLException al llamador si falla.

        // Objeto File apuntando a la carpeta contenedora de la BD.
        File carpeta = new File("datos"); // Carpeta "datos" del proyecto.

        // Si la carpeta no existe, la crea para evitar errores de escritura.
        if (!carpeta.exists()) { // Comprueba existencia de la carpeta.
            carpeta.mkdirs(); // Crea "datos" (y carpetas padre si faltaran).
        } // Fin de creación de carpeta.

        // Abre y devuelve la conexión JDBC contra el archivo SQLite.
        return DriverManager.getConnection("jdbc:sqlite:" + RUTA_DB); // Si el archivo no existe, SQLite lo crea.
    } // Fin del método conectar.
} // Fin de la clase ConexionDB.