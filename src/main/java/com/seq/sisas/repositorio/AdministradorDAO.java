package com.seq.sisas.repositorio; // Paquete de acceso a datos.

import com.seq.sisas.db.ConexionDB; // Proveedor de conexión SQLite.
import com.seq.sisas.modelo.Administrador; // Entidad de administrador.
import java.sql.Connection; // Enlace a BD.
import java.sql.PreparedStatement; // Query segura.
import java.sql.ResultSet; // Resultado de SELECT.
import java.sql.SQLException; // Errores de BD.
import java.time.LocalDateTime; // Fecha y hora para último acceso.
import java.time.format.DateTimeFormatter; // Formato de fecha/hora.
import java.util.ArrayList; // Lista dinámica.
import java.util.List; // Interfaz de listas.

/**
 * AdministradorDAO (V13): CRUD completo de cuentas de administración.
 * CONEXIONES:
 * - Usa com.seq.sisas.db.ConexionDB.
 * - Es llamado por ServicioAutenticacion y ServicioAdministradores.
 */
public class AdministradorDAO { // Clase DAO.

    // Formato de fecha/hora para el último acceso.
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Patrón fijo.

    // Lista de columnas reutilizada en todos los SELECT.
    private static final String COLUMNAS = "id, nombre_completo, usuario, password_hash, activo, fecha_creacion, ultimo_acceso"; // Columnas de la tabla.

    // Busca un administrador por su nombre de usuario.
    public Administrador obtenerPorUsuario(String usuario) throws SQLException { // Lanza error si falla.
        String sql = "SELECT " + COLUMNAS + " FROM administradores WHERE usuario = ?"; // Query por usuario.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, usuario); // Usuario a buscar.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) { return mapear(rs); } // Devuelve si existe.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // Usuario inexistente.
    } // Fin de obtenerPorUsuario.

    // Busca un administrador por su ID.
    public Administrador obtenerPorId(int id) throws SQLException { // Lanza error si falla.
        String sql = "SELECT " + COLUMNAS + " FROM administradores WHERE id = ?"; // Query por ID.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, id); // ID a buscar.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) { return mapear(rs); } // Devuelve si existe.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // ID inexistente.
    } // Fin de obtenerPorId.

    // Devuelve todos los administradores ordenados por ID.
    public List<Administrador> obtenerTodos() throws SQLException { // Lanza error si falla.
        List<Administrador> lista = new ArrayList<>(); // Lista vacía.
        String sql = "SELECT " + COLUMNAS + " FROM administradores ORDER BY id"; // Query completa ordenada.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql); // Prepara query.
             ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
            while (rs.next()) { lista.add(mapear(rs)); } // Agrega cada cuenta.
        } // Cierra recursos.
        return lista; // Devuelve la lista.
    } // Fin de obtenerTodos.

    // Inserta una cuenta nueva con contraseña ya cifrada.
    public void insertar(Administrador a) throws SQLException { // Lanza error si falla.
        String sql = "INSERT INTO administradores (nombre_completo, usuario, password_hash, activo, fecha_creacion) VALUES (?, ?, ?, ?, ?)"; // Query INSERT.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, a.getNombreCompleto()); // Nombre real.
            ps.setString(2, a.getUsuario()); // Usuario de acceso.
            ps.setString(3, a.getPasswordHash()); // Hash BCrypt (nunca texto plano).
            ps.setInt(4, a.isActivo() ? 1 : 0); // Estado activo/inactivo.
            ps.setString(5, LocalDateTime.now().format(FORMATO)); // Fecha de creación de la cuenta.
            ps.executeUpdate(); // Ejecuta inserción.
        } // Cierra recursos.
    } // Fin de insertar.

    // Actualiza nombre, usuario y estado de una cuenta existente.
    public void actualizar(Administrador a) throws SQLException { // Lanza error si falla.
        String sql = "UPDATE administradores SET nombre_completo=?, usuario=?, activo=? WHERE id=?"; // Query UPDATE sin tocar contraseña.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, a.getNombreCompleto()); // Nuevo nombre.
            ps.setString(2, a.getUsuario()); // Nuevo usuario.
            ps.setInt(3, a.isActivo() ? 1 : 0); // Nuevo estado.
            ps.setInt(4, a.getId()); // ID de la cuenta.
            ps.executeUpdate(); // Ejecuta actualización.
        } // Cierra recursos.
    } // Fin de actualizar.

    // Cambia únicamente la contraseña (hash) de una cuenta.
    public void cambiarPassword(int id, String hash) throws SQLException { // Lanza error si falla.
        String sql = "UPDATE administradores SET password_hash=? WHERE id=?"; // Query UPDATE de contraseña.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, hash); // Nuevo hash BCrypt.
            ps.setInt(2, id); // ID de la cuenta.
            ps.executeUpdate(); // Ejecuta actualización.
        } // Cierra recursos.
    } // Fin de cambiarPassword.

    // Registra la fecha/hora del último inicio de sesión.
    public void actualizarUltimoAcceso(int id) throws SQLException { // Lanza error si falla.
        String sql = "UPDATE administradores SET ultimo_acceso=? WHERE id=?"; // Query UPDATE de último acceso.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, LocalDateTime.now().format(FORMATO)); // Momento del acceso.
            ps.setInt(2, id); // ID de la cuenta.
            ps.executeUpdate(); // Ejecuta actualización.
        } // Cierra recursos.
    } // Fin de actualizarUltimoAcceso.

    // Convierte una fila de ResultSet en objeto Administrador.
    private Administrador mapear(ResultSet rs) throws SQLException { // Recibe la fila actual.
        Administrador a = new Administrador(); // Crea objeto vacío.
        a.setId(rs.getInt("id")); // Mapea ID.
        a.setNombreCompleto(rs.getString("nombre_completo")); // Mapea nombre.
        a.setUsuario(rs.getString("usuario")); // Mapea usuario.
        a.setPasswordHash(rs.getString("password_hash")); // Mapea hash.
        a.setActivo(rs.getInt("activo") == 1); // Mapea estado a booleano.
        return a; // Devuelve el objeto lleno.
    } // Fin de mapear.
} // Fin de la clase AdministradorDAO.