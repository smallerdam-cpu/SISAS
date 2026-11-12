package com.seq.sisas.repositorio; // Paquete de acceso a datos.

import com.seq.sisas.db.ConexionDB; // Proveedor de conexión SQLite.
import com.seq.sisas.modelo.Institucion; // Entidad a persistir.
import java.sql.Connection; // Enlace a BD.
import java.sql.PreparedStatement; // Query segura.
import java.sql.ResultSet; // Resultado de SELECT.
import java.sql.SQLException; // Errores de BD.
import java.util.ArrayList; // Lista dinámica.
import java.util.List; // Interfaz de listas.

/**
 * InstitucionDAO: persistencia del catálogo de prepas y universidades.
 * CONEXIONES:
 * - Usa com.seq.sisas.db.ConexionDB.
 * - Es llamada por el servicio de instituciones (paso posterior) y la capa ui.
 */
public class InstitucionDAO { // Clase DAO.
    // Inserta una institución nueva con nombre y nivel.
    public void insertar(Institucion inst) throws SQLException { // Lanza error si falla.
        String sql = "INSERT INTO instituciones (nombre, nivel) VALUES (?, ?)"; // Query INSERT.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
                PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, inst.getNombre()); // Asigna nombre.
            ps.setString(2, inst.getNivel()); // Asigna nivel PREPA o UNIVERSIDAD.
            ps.executeUpdate(); // Ejecuta inserción.
        } // Cierra recursos.
    } // Fin de insertar.
      // Devuelve todas las instituciones activas ordenadas por nivel y nombre.

    public List<Institucion> obtenerTodas() throws SQLException { // Lanza error si falla.
        List<Institucion> lista = new ArrayList<>(); // Lista vacía.
        String sql = "SELECT id, nombre, nivel, activo FROM instituciones WHERE activo = 1 ORDER BY nivel, nombre"; // Query
                                                                                                                    // completa.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
                PreparedStatement ps = con.prepareStatement(sql); // Prepara query.
                ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
            while (rs.next()) {
                lista.add(mapear(rs));
            } // Convierte y agrega cada fila.
        } // Cierra recursos.
        return lista; // Devuelve la lista.
    } // Fin de obtenerTodas.
      // Devuelve solo las instituciones activas de un nivel (PREPA o UNIVERSIDAD).

    public List<Institucion> obtenerPorNivel(String nivel) throws SQLException { // Lanza error si falla.
        List<Institucion> lista = new ArrayList<>(); // Lista vacía.
        String sql = "SELECT id, nombre, nivel, activo FROM instituciones WHERE nivel = ? AND activo = 1 ORDER BY nombre"; // Query
                                                                                                                           // por
                                                                                                                           // nivel.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
                PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, nivel); // Nivel a filtrar.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                while (rs.next()) {
                    lista.add(mapear(rs));
                } // Agrega coincidencias.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return lista; // Devuelve coincidencias.
    } // Fin de obtenerPorNivel.
      // Devuelve una institución por su ID.

    public Institucion obtenerPorId(int id) throws SQLException { // Lanza error si falla.
        String sql = "SELECT id, nombre, nivel, activo FROM instituciones WHERE id = ?"; // Query por ID.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
                PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, id); // ID a buscar.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) {
                    return mapear(rs);
                } // Devuelve si existe.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // ID inexistente.
    } // Fin de obtenerPorId.
      // Convierte una fila de ResultSet en objeto Institucion.

    private Institucion mapear(ResultSet rs) throws SQLException { // Recibe la fila actual.
        Institucion inst = new Institucion(); // Crea objeto vacío.
        inst.setId(rs.getInt("id")); // Mapea ID.
        inst.setNombre(rs.getString("nombre")); // Mapea nombre.
        inst.setNivel(rs.getString("nivel")); // Mapea nivel.
        inst.setActivo(rs.getInt("activo")); // Mapea activo.
        return inst; // Devuelve el objeto lleno.
    } // Fin de mapear.
} // Fin de la clase InstitucionDAO.