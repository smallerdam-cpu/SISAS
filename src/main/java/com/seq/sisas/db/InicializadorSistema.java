package com.seq.sisas.db; // Paquete de base de datos del sistema SISAS.

import com.seq.sisas.seguridad.SeguridadUtil; // Clase que cifra la contraseña inicial con BCrypt.
import java.sql.Connection; // Conexión activa con SQLite.
import java.sql.PreparedStatement; // Sentencia SQL preparada (evita inyección SQL).
import java.sql.ResultSet; // Resultado de consultas SELECT.
import java.sql.SQLException; // Captura de errores de base de datos.
import java.sql.Statement; // Sentencia SQL simple para crear tablas.
import java.time.LocalDateTime; // Fecha y hora actual del sistema.
import java.time.format.DateTimeFormatter; // Formato legible de fecha/hora.

/**
 * InicializadorSistema: crea tablas y siembra el administrador inicial.
 * CONEXIONES:
 * - Usa com.seq.sisas.db.ConexionDB para obtener la conexión.
 * - Usa com.seq.sisas.seguridad.SeguridadUtil para cifrar la contraseña
 * inicial.
 * - Es invocado por com.seq.sisas.app.Main al arrancar.
 */
public class InicializadorSistema { // Clase de instalación/arranque de la BD.

    // Formato de fecha/hora para auditoría: ejemplo 2025-01-15 09:30:00.
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Patrón fijo.

    // Crea tablas y garantiza el administrador inicial; lanza error de BD al
    // llamador.
    public static void inicializar() throws SQLException { // Main capturará cualquier SQLException.

        // try-with-resources: abre la conexión y la cierra automáticamente al terminar.
        try (Connection con = ConexionDB.conectar()) { // Conexión nueva contra sisas.db.

            // --- Paso 1: crear tabla de administradores si no existe. ---
            try (Statement st = con.createStatement()) { // Statement para DDL simple.
                st.executeUpdate( // Ejecuta el CREATE TABLE.
                        "CREATE TABLE IF NOT EXISTS administradores (" + // Tabla de cuentas de administración.
                                " id INTEGER PRIMARY KEY AUTOINCREMENT," + // Identificador único automático.
                                " nombre_completo TEXT NOT NULL," + // Nombre del administrador.
                                " usuario TEXT NOT NULL UNIQUE," + // Usuario sin duplicados.
                                " password_hash TEXT NOT NULL," + // Hash BCrypt (nunca texto plano).
                                " activo INTEGER NOT NULL DEFAULT 1," + // 1 = activo, 0 = inactivo.
                                " fecha_creacion TEXT NOT NULL," + // Fecha de alta de la cuenta.
                                " ultimo_acceso TEXT" + // Último inicio de sesión (lo usará V1.2).
                                ")" // Cierre de definición de tabla.
                ); // Fin de DDL administradores.
            } // Cierra Statement de administradores.

            // --- Paso 2: crear tabla de auditoría si no existe. ---
            try (Statement st = con.createStatement()) { // Statement para DDL simple.
                st.executeUpdate( // Ejecuta el CREATE TABLE.
                        "CREATE TABLE IF NOT EXISTS auditoria (" + // Tabla de rastro de acciones.
                                " id INTEGER PRIMARY KEY AUTOINCREMENT," + // Identificador único del evento.
                                " administrador_id INTEGER," + // Quién hizo la acción (NULL = sistema).
                                " accion TEXT NOT NULL," + // Nombre de la acción realizada.
                                " detalle TEXT," + // Descripción adicional del cambio.
                                " fecha_hora TEXT NOT NULL" + // Momento exacto del evento.
                                ")" // Cierre de definición de tabla.
                ); // Fin de DDL auditoría.
            } // Cierra Statement de auditoría.

            // --- Paso 3: sembrar administrador inicial solo si no existe ninguno. ---
            try (PreparedStatement ps = con.prepareStatement("SELECT COUNT(*) FROM administradores")) { // Consulta de
                                                                                                        // conteo.
                try (ResultSet rs = ps.executeQuery()) { // Ejecuta el SELECT y obtiene resultado.
                    rs.next(); // Avanza al único registro devuelto.
                    if (rs.getInt(1) == 0) { // Si no hay administradores registrados...
                        crearAdminInicial(con); // ...crea la cuenta inicial segura.
                    } // Fin de condición de siembra.
                } // Cierra ResultSet.
            } // Cierra PreparedStatement de conteo.

            // --- Paso 4: registrar evento de inicialización en auditoría. ---
            try (PreparedStatement ps = con.prepareStatement( // Inserción parametrizada de auditoría.
                    "INSERT INTO auditoria (administrador_id, accion, detalle, fecha_hora) VALUES (NULL, ?, ?, ?)")) { // Parámetros
                                                                                                                       // del
                                                                                                                       // evento.
                ps.setString(1, "INICIALIZACION_SISTEMA"); // Acción: arranque/verificación de BD.
                ps.setString(2, "Estructura verificada y administrador inicial garantizado."); // Detalle descriptivo.
                ps.setString(3, LocalDateTime.now().format(FORMATO)); // Fecha/hora actual formateada.
                ps.executeUpdate(); // Inserta el evento en auditoría.
            } // Cierra PreparedStatement de auditoría.

            // --- Paso nuevo: crear tabla de estudiantes si no existe. ---
            try (Statement st = con.createStatement()) { // Statement para DDL.
                st.executeUpdate( // Ejecuta el CREATE TABLE.
                        "CREATE TABLE IF NOT EXISTS estudiantes (" + // Tabla de alumnos de servicio social.
                                " id INTEGER PRIMARY KEY AUTOINCREMENT," + // ID único.
                                " nombre_completo TEXT NOT NULL," + // Nombre del alumno.
                                " escuela TEXT NOT NULL," + // Escuela de procedencia.
                                " contacto TEXT," + // Teléfono o correo.
                                " fecha_ingreso TEXT NOT NULL," + // Fecha de inicio de servicio.
                                " horas_requeridas INTEGER NOT NULL," + // Meta de horas (ej. 450).
                                " horas_acumuladas REAL NOT NULL DEFAULT 0," + // Horas trabajadas hasta hoy.
                                " codigo_unico TEXT NOT NULL UNIQUE," + // Código para el QR.
                                " estado TEXT NOT NULL DEFAULT 'ACTIVO'" + // ACTIVO, INACTIVO, FINALIZADO.
                                ")" // Cierre de tabla.
                ); // Fin de DDL estudiantes.
            } // Cierra Statement.

            // --- Paso nuevo: crear tabla de asistencias si no existe. ---
            try (Statement st = con.createStatement()) { // Statement para DDL.
                st.executeUpdate( // Ejecuta el CREATE TABLE.
                        "CREATE TABLE IF NOT EXISTS asistencias (" + // Tabla de movimientos entrada/salida.
                                " id INTEGER PRIMARY KEY AUTOINCREMENT," + // ID único del movimiento.
                                " estudiante_id INTEGER NOT NULL," + // A quién pertenece el movimiento.
                                " fecha TEXT NOT NULL," + // Día del movimiento (para reportes).
                                " hora_entrada TEXT NOT NULL," + // Marca de tiempo completa de entrada.
                                " hora_salida TEXT," + // Marca de tiempo de salida (null si sigue abierta).
                                " minutos INTEGER NOT NULL DEFAULT 0," + // Duración calculada en minutos.
                                " estado TEXT NOT NULL DEFAULT 'ABIERTA'" + // ABIERTA o CERRADA.
                                ")" // Cierre de tabla.
                ); // Fin de DDL asistencias.
            } // Cierra Statement.

            // --- Migración V5: agrega columnas nuevas sin borrar datos existentes. ---
            try (Statement st = con.createStatement()) { // Statement para la migración.
                java.util.Set<String> cols = new java.util.HashSet<>(); // Conjunto de columnas actuales.
                try (ResultSet rs = st.executeQuery("PRAGMA table_info(estudiantes)")) { // Lee la estructura actual de
                                                                                         // la tabla.
                    while (rs.next()) { // Recorre cada columna existente.
                        cols.add(rs.getString("name")); // Guarda el nombre de la columna.
                    } // Fin del while.
                } // Cierra ResultSet.
                if (!cols.contains("curp")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN curp TEXT");
                } // Agrega CURP si falta.
                if (!cols.contains("fecha_nacimiento")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN fecha_nacimiento TEXT");
                } // Agrega fecha nac. si falta.
                if (!cols.contains("direccion")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN direccion TEXT");
                } // Agrega dirección si falta.
                st.executeUpdate("CREATE UNIQUE INDEX IF NOT EXISTS idx_est_curp ON estudiantes(curp)"); // Índice
                                                                                                         // único:
                                                                                                         // impide CURPs
                                                                                                         // duplicadas a
                                                                                                         // nivel de BD.
            } // Cierra Statement de migración.

            // --- Migración V7: agrega columna de fotografía sin borrar datos. ---
            try (Statement st = con.createStatement()) { // Statement para la migración.
                java.util.Set<String> cols = new java.util.HashSet<>(); // Conjunto de columnas actuales.
                try (ResultSet rs = st.executeQuery("PRAGMA table_info(estudiantes)")) { // Lee estructura actual.
                    while (rs.next()) {
                        cols.add(rs.getString("name"));
                    } // Guarda cada nombre de columna.
                } // Cierra ResultSet.
                if (!cols.contains("foto")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN foto TEXT");
                } // Agrega columna foto si falta.
            } // Cierra Statement de migración.
            try (Statement st = con.createStatement()) { // Statement para DDL.
                st.executeUpdate("CREATE TABLE IF NOT EXISTS instituciones (" // Catálogo de planteles.
                        + " id INTEGER PRIMARY KEY AUTOINCREMENT," // ID único.
                        + " nombre TEXT NOT NULL UNIQUE," // Nombre sin duplicados.
                        + " nivel TEXT NOT NULL," // PREPA o UNIVERSIDAD.
                        + " activo INTEGER NOT NULL DEFAULT 1)"); // 1 = activo.
                java.util.Set<String> cols = new java.util.HashSet<>(); // Columnas actuales.
                try (ResultSet rs = st.executeQuery("PRAGMA table_info(estudiantes)")) { // Lee estructura.
                    while (rs.next()) {
                        cols.add(rs.getString("name"));
                    } // Guarda cada nombre.
                } // Cierra ResultSet.
                if (!cols.contains("institucion_id")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN institucion_id INTEGER");
                } // FK plantel.
                if (!cols.contains("nivel")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN nivel TEXT");
                } // Nivel académico.
                if (!cols.contains("tipo_servicio")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN tipo_servicio TEXT");
                } // Tipo de servicio.
                if (!cols.contains("matricula")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN matricula TEXT");
                } // Matrícula.
                if (!cols.contains("especialidad")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN especialidad TEXT");
                } // Especialidad.
                if (!cols.contains("fecha_fin")) {
                    st.executeUpdate("ALTER TABLE estudiantes ADD COLUMN fecha_fin TEXT");
                } // Fin del periodo.
            } // Cierra Statement de migración V8.

            // --- Migración V9: agrega datos de contacto a instituciones sin borrar datos.
            // ---
            try (Statement st = con.createStatement()) { // Statement para la migración.
                java.util.Set<String> cols = new java.util.HashSet<>(); // Columnas actuales.
                try (ResultSet rs = st.executeQuery("PRAGMA table_info(instituciones)")) { // Lee estructura.
                    while (rs.next()) {
                        cols.add(rs.getString("name"));
                    } // Guarda cada nombre.
                } // Cierra ResultSet.
                if (!cols.contains("clave_centro_trabajo")) {
                    st.executeUpdate("ALTER TABLE instituciones ADD COLUMN clave_centro_trabajo TEXT");
                } // CCT.
                if (!cols.contains("direccion")) {
                    st.executeUpdate("ALTER TABLE instituciones ADD COLUMN direccion TEXT");
                } // Dirección.
                if (!cols.contains("codigo_postal")) {
                    st.executeUpdate("ALTER TABLE instituciones ADD COLUMN codigo_postal TEXT");
                } // CP.
                if (!cols.contains("director")) {
                    st.executeUpdate("ALTER TABLE instituciones ADD COLUMN director TEXT");
                } // Director o coordinador.
                if (!cols.contains("telefono")) {
                    st.executeUpdate("ALTER TABLE instituciones ADD COLUMN telefono TEXT");
                } // Teléfono.
                if (!cols.contains("correo")) {
                    st.executeUpdate("ALTER TABLE instituciones ADD COLUMN correo TEXT");
                } // Correo.
            } // Cierra Statement de migración V9.

        } // La conexión se cierra automáticamente aquí.
    } // Fin del método inicializar.

    // Inserta el administrador inicial con contraseña cifrada (método privado de
    // apoyo).
    private static void crearAdminInicial(Connection con) throws SQLException { // Recibe la conexión ya abierta.

        // Cifra la contraseña temporal con BCrypt antes de guardarla.
        String hash = SeguridadUtil.cifrar("admin123"); // Contraseña temporal de primer arranque.

        // Inserta la cuenta inicial del sistema con valores parametrizados.
        try (PreparedStatement ps = con.prepareStatement( // Inserción segura del admin inicial.
                "INSERT INTO administradores (nombre_completo, usuario, password_hash, activo, fecha_creacion) " + // Columnas
                                                                                                                   // a
                                                                                                                   // llenar.
                        "VALUES (?, ?, ?, 1, ?)")) { // activo = 1 (cuenta habilitada).
            ps.setString(1, "Administrador SEQ"); // Nombre visible en auditoría y panel.
            ps.setString(2, "admin"); // Usuario inicial de acceso.
            ps.setString(3, hash); // Hash BCrypt, nunca texto plano.
            ps.setString(4, LocalDateTime.now().format(FORMATO)); // Fecha de creación de la cuenta.
            ps.executeUpdate(); // Ejecuta la inserción del admin inicial.
        } // Cierra PreparedStatement de inserción.
    } // Fin de crearAdminInicial.
} // Fin de la clase InicializadorSistema.