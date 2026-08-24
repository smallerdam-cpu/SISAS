package com.seq.sisas.repositorio; // Paquete de acceso a datos.

import com.seq.sisas.db.ConexionDB; // Proveedor de conexión SQLite.
import com.seq.sisas.modelo.Asistencia; // Entidad de movimiento.
import java.sql.Connection; // Enlace a BD.
import java.sql.PreparedStatement; // Query segura.
import java.sql.ResultSet; // Resultado de SELECT.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Listas de Java para devolver varios movimientos.

/**
 * AsistenciaDAO: guarda y consulta movimientos de entrada/salida.
 * CONEXIONES:
 * - Usa com.seq.sisas.db.ConexionDB.
 * - Es llamada por com.seq.sisas.servicio.ServicioAsistencia.
 */
public class AsistenciaDAO { // Clase DAO.

    // Registra una ENTRADA nueva con estado ABIERTA.
    public void registrarEntrada(int estudianteId, String fecha, String horaEntrada) throws SQLException { // Lanza error si falla.
        String sql = "INSERT INTO asistencias (estudiante_id, fecha, hora_entrada, estado) VALUES (?, ?, ?, 'ABIERTA')"; // Query INSERT.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, estudianteId); // ID del alumno.
            ps.setString(2, fecha); // Día actual.
            ps.setString(3, horaEntrada); // Timestamp de entrada.
            ps.executeUpdate(); // Ejecuta inserción.
        } // Cierra recursos.
    } // Fin de registrarEntrada.

    // Devuelve la sesión ABIERTA del alumno, o null si no tiene.
    public Asistencia obtenerAbierta(int estudianteId) throws SQLException { // Lanza error si falla.
        String sql = "SELECT id, estudiante_id, fecha, hora_entrada, hora_salida, minutos, estado FROM asistencias WHERE estudiante_id = ? AND estado = 'ABIERTA' ORDER BY id DESC LIMIT 1"; // Busca sesión abierta.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, estudianteId); // ID del alumno.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) { // Si encontró sesión abierta...
                    return mapear(rs); // ...devuelve el objeto.
                } // Fin de validación.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // No hay sesión abierta.
    } // Fin de obtenerAbierta.

    // Devuelve el último movimiento del alumno (para anti-duplicados).
    public Asistencia obtenerUltima(int estudianteId) throws SQLException { // Lanza error si falla.
        String sql = "SELECT id, estudiante_id, fecha, hora_entrada, hora_salida, minutos, estado FROM asistencias WHERE estudiante_id = ? ORDER BY id DESC LIMIT 1"; // Último movimiento por ID.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, estudianteId); // ID del alumno.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) { // Si existe al menos un movimiento...
                    return mapear(rs); // ...devuelve el objeto.
                } // Fin de validación.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // El alumno nunca ha registrado asistencia.
    } // Fin de obtenerUltima.

    // Cierra una sesión: guarda salida, minutos y cambia estado a CERRADA.
    public void cerrar(int id, String horaSalida, int minutos) throws SQLException { // Lanza error si falla.
        String sql = "UPDATE asistencias SET hora_salida = ?, minutos = ?, estado = 'CERRADA' WHERE id = ?"; // Query UPDATE de cierre.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, horaSalida); // Timestamp de salida.
            ps.setInt(2, minutos); // Minutos calculados.
            ps.setInt(3, id); // ID del movimiento a cerrar.
            ps.executeUpdate(); // Ejecuta actualización.
        } // Cierra recursos.
    } // Fin de cerrar.

    // Método privado: convierte una fila de ResultSet en objeto Asistencia.
    private Asistencia mapear(ResultSet rs) throws SQLException { // Recibe la fila actual.
        Asistencia a = new Asistencia(); // Crea objeto vacío.
        a.setId(rs.getInt("id")); // Mapea ID.
        a.setEstudianteId(rs.getInt("estudiante_id")); // Mapea ID del alumno.
        a.setFecha(rs.getString("fecha")); // Mapea fecha.
        a.setHoraEntrada(rs.getString("hora_entrada")); // Mapea entrada.
        a.setHoraSalida(rs.getString("hora_salida")); // Mapea salida (puede ser null).
        a.setMinutos(rs.getInt("minutos")); // Mapea minutos.
        a.setEstado(rs.getString("estado")); // Mapea estado.
        return a; // Devuelve el objeto lleno.
    } // Fin de mapear.
    
        // Devuelve las asistencias de un alumno, opcionalmente filtradas por rango de fechas.
    public List<Asistencia> obtenerPorEstudiante(int estudianteId, String desde, String hasta) throws SQLException { // Lanza error si falla.
        String sql = "SELECT id, estudiante_id, fecha, hora_entrada, hora_salida, minutos, estado FROM asistencias WHERE estudiante_id = ?"; // Query base por alumno.
        if (desde != null && !desde.isEmpty()) { sql += " AND fecha >= ?"; } // Agrega filtro de fecha inicial si existe.
        if (hasta != null && !hasta.isEmpty()) { sql += " AND fecha <= ?"; } // Agrega filtro de fecha final si existe.
        sql += " ORDER BY id"; // Orden cronológico por ID.

        List<Asistencia> lista = new java.util.ArrayList<>(); // Lista vacía para llenar.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query dinámica.
            ps.setInt(1, estudianteId); // Parámetro 1: ID del alumno.
            int indice = 2; // Índice del siguiente parámetro libre.
            if (desde != null && !desde.isEmpty()) { ps.setString(indice++, desde); } // Asigna fecha inicial si existe.
            if (hasta != null && !hasta.isEmpty()) { ps.setString(indice++, hasta); } // Asigna fecha final si existe.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                while (rs.next()) { // Mientras haya filas...
                    lista.add(mapear(rs)); // ...convierte y agrega cada fila.
                } // Fin del while.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return lista; // Devuelve la lista de movimientos.   
    } // Fin de obtenerPorEstudiante.

        // Registra un ajuste manual de horas como movimiento especial visible en reportes.
    public void registrarAjuste(int estudianteId, String fecha, int minutos) throws SQLException { // Lanza error si falla.
        String sql = "INSERT INTO asistencias (estudiante_id, fecha, hora_entrada, hora_salida, minutos, estado) VALUES (?, ?, 'AJUSTE', 'AJUSTE', ?, 'AJUSTE')"; // Inserta fila tipo AJUSTE.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, estudianteId); // ID del alumno ajustado.
            ps.setString(2, fecha); // Día del ajuste.
            ps.setInt(3, minutos); // Minutos positivos o negativos.
            ps.executeUpdate(); // Ejecuta inserción.
        } // Cierra recursos.
    } // Fin de registrarAjuste.
} // Fin de la clase AsistenciaDAO.