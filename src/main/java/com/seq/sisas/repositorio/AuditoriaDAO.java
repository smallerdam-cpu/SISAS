package com.seq.sisas.repositorio; // Paquete de acceso a datos.

import com.seq.sisas.db.ConexionDB; // Proveedor de conexión SQLite.
import com.seq.sisas.modelo.RegistroAuditoria; // Entidad de evento de auditoría.
import java.sql.Connection; // Enlace a BD.
import java.sql.PreparedStatement; // Query segura.
import java.sql.ResultSet; // Resultado de SELECT.
import java.sql.SQLException; // Errores de BD.
import java.time.LocalDateTime; // Fecha y hora del sistema.
import java.time.format.DateTimeFormatter; // Formato de legibilidad.
import java.util.ArrayList; // Lista dinámica.
import java.util.List; // Interfaz de listas.

/**
 * AuditoriaDAO (V14): escribe eventos y consulta el historial con responsable.
 * CONEXIONES:
 * - Usa com.seq.sisas.db.ConexionDB.
 * - Es invocada por todos los servicios (escritura) y por ServicioAuditoria (lectura).
 */
public class AuditoriaDAO { // Clase de persistencia de auditoría.

    // Formato idéntico al usado en el resto del sistema.
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Patrón de fecha.

    // Inserta un nuevo evento de auditoría.
    public void registrar(Integer adminId, String accion, String detalle) throws SQLException { // Lanza error si falla.
        String sql = "INSERT INTO auditoria (administrador_id, accion, detalle, fecha_hora) VALUES (?, ?, ?, ?)"; // Query de inserción.
        try (Connection con = ConexionDB.conectar(); // Abre enlace con SQLite.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara el INSERT.
            if (adminId != null) { ps.setInt(1, adminId); } else { ps.setNull(1, java.sql.Types.INTEGER); } // Responsable o NULL (sistema).
            ps.setString(2, accion); // Asigna el nombre de la acción.
            ps.setString(3, detalle); // Asigna la descripción del evento.
            ps.setString(4, LocalDateTime.now().format(FORMATO)); // Asigna fecha y hora actual.
            ps.executeUpdate(); // Ejecuta el INSERT.
        } // Cierra recursos automáticamente.
    } // Fin de registrar.

    // Devuelve eventos con nombre del responsable; si el filtro está vacío, todos (más recientes primero).
    public List<RegistroAuditoria> buscar(String filtro) throws SQLException { // Lanza error si falla.
        String sql = "SELECT a.id, COALESCE(ad.nombre_completo, 'SISTEMA') AS responsable, a.accion, a.detalle, a.fecha_hora " + // Une auditoría con administradores.
                     "FROM auditoria a LEFT JOIN administradores ad ON ad.id = a.administrador_id"; // LEFT JOIN: conserva eventos de SISTEMA.
        boolean conFiltro = filtro != null && !filtro.trim().isEmpty(); // Detecta si hay criterio de búsqueda.
        if (conFiltro) { sql += " WHERE a.accion LIKE ? OR a.detalle LIKE ? OR ad.nombre_completo LIKE ?"; } // Agrega búsqueda parcial.
        sql += " ORDER BY a.id DESC"; // Más recientes primero.
        String patron = "%" + (filtro == null ? "" : filtro.trim()) + "%"; // Comodines de coincidencia parcial.
        List<RegistroAuditoria> lista = new ArrayList<>(); // Lista vacía para resultados.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            if (conFiltro) { // Si hay criterio...
                ps.setString(1, patron); // Parámetro para acción.
                ps.setString(2, patron); // Parámetro para detalle.
                ps.setString(3, patron); // Parámetro para responsable.
            } // Fin de asignación de parámetros.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                while (rs.next()) { // Mientras haya eventos...
                    RegistroAuditoria r = new RegistroAuditoria(); // Crea objeto vacío.
                    r.setId(rs.getInt("id")); // Mapea ID.
                    r.setResponsable(rs.getString("responsable")); // Mapea responsable o SISTEMA.
                    r.setAccion(rs.getString("accion")); // Mapea acción.
                    r.setDetalle(rs.getString("detalle")); // Mapea detalle.
                    r.setFechaHora(rs.getString("fecha_hora")); // Mapea fecha/hora.
                    lista.add(r); // Agrega el evento a la lista.
                } // Fin del while.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return lista; // Devuelve el historial filtrado.
    } // Fin de buscar.
} // Fin de la clase AuditoriaDAO.