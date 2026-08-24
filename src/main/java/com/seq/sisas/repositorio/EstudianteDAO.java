package com.seq.sisas.repositorio; // Paquete de acceso a datos.

import com.seq.sisas.db.ConexionDB; // Proveedor de conexión SQLite.
import com.seq.sisas.modelo.Estudiante; // Entidad a persistir.
import java.sql.Connection; // Enlace a BD.
import java.sql.PreparedStatement; // Query segura.
import java.sql.ResultSet; // Resultado de SELECT.
import java.sql.SQLException; // Errores de BD.
import java.util.ArrayList; // Lista dinámica.
import java.util.List; // Interfaz de listas.

/**
 * EstudianteDAO (V7): persistencia completa incluyendo fotografía.
 * CONEXIONES:
 * - Usa com.seq.sisas.db.ConexionDB.
 * - Es llamada por com.seq.sisas.servicio.ServicioEstudiante y ServicioAsistencia.
 */
public class EstudianteDAO { // Clase DAO.

    // Lista de columnas completa reutilizada en todos los SELECT.
    private static final String COLUMNAS = "id, nombre_completo, escuela, contacto, fecha_ingreso, horas_requeridas, horas_acumuladas, codigo_unico, estado, curp, fecha_nacimiento, direccion, foto"; // Columnas de la tabla.

    // Inserta un nuevo estudiante con todos sus campos.
    public void insertar(Estudiante e) throws SQLException { // Lanza error si falla.
        String sql = "INSERT INTO estudiantes (nombre_completo, escuela, contacto, fecha_ingreso, horas_requeridas, codigo_unico, curp, fecha_nacimiento, direccion, foto) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // Query INSERT completo.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, e.getNombreCompleto()); // Asigna nombre.
            ps.setString(2, e.getEscuela()); // Asigna escuela.
            ps.setString(3, e.getContacto()); // Asigna contacto.
            ps.setString(4, e.getFechaIngreso()); // Asigna fecha ingreso.
            ps.setInt(5, e.getHorasRequeridas()); // Asigna horas meta.
            ps.setString(6, e.getCodigoUnico()); // Asigna código QR.
            ps.setString(7, e.getCurp()); // Asigna CURP.
            ps.setString(8, e.getFechaNacimiento()); // Asigna fecha nac.
            ps.setString(9, e.getDireccion()); // Asigna dirección.
            ps.setString(10, e.getFoto()); // Asigna nombre de foto (puede ser null).
            ps.executeUpdate(); // Ejecuta inserción.
        } // Cierra recursos.
    } // Fin de insertar.

    // Devuelve todos los estudiantes ordenados por ID.
    public List<Estudiante> obtenerTodos() throws SQLException { // Lanza error si falla.
        List<Estudiante> lista = new ArrayList<>(); // Lista vacía.
        String sql = "SELECT " + COLUMNAS + " FROM estudiantes ORDER BY id"; // Query completa.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql); // Prepara query.
             ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
            while (rs.next()) { lista.add(mapear(rs)); } // Convierte y agrega cada fila.
        } // Cierra recursos.
        return lista; // Devuelve la lista.
    } // Fin de obtenerTodos.

    // Busca por código único de QR.
    public Estudiante obtenerPorCodigo(String codigo) throws SQLException { // Lanza error si falla.
        String sql = "SELECT " + COLUMNAS + " FROM estudiantes WHERE codigo_unico = ?"; // Query por código.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, codigo); // Código a buscar.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) { return mapear(rs); } // Devuelve si existe.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // Código inexistente.
    } // Fin de obtenerPorCodigo.

    // Busca por CURP para impedir duplicados.
    public Estudiante obtenerPorCurp(String curp) throws SQLException { // Lanza error si falla.
        String sql = "SELECT " + COLUMNAS + " FROM estudiantes WHERE curp = ?"; // Query por CURP.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, curp); // CURP a buscar.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) { return mapear(rs); } // Devuelve si existe.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // CURP libre.
    } // Fin de obtenerPorCurp.

    // Busca por nombre, apellidos o CURP (coincidencia parcial).
    public List<Estudiante> buscar(String criterio) throws SQLException { // Lanza error si falla.
        String sql = "SELECT " + COLUMNAS + " FROM estudiantes WHERE nombre_completo LIKE ? OR curp LIKE ? ORDER BY nombre_completo"; // Query con LIKE.
        String patron = "%" + criterio + "%"; // Comodines de coincidencia parcial.
        List<Estudiante> lista = new ArrayList<>(); // Lista vacía.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, patron); // Parámetro para nombre.
            ps.setString(2, patron); // Parámetro para CURP.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                while (rs.next()) { lista.add(mapear(rs)); } // Agrega coincidencias.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return lista; // Devuelve coincidencias.
    } // Fin de buscar.

    // Actualiza los datos editables incluyendo la foto.
    public void actualizar(Estudiante e) throws SQLException { // Lanza error si falla.
        String sql = "UPDATE estudiantes SET nombre_completo=?, escuela=?, contacto=?, fecha_ingreso=?, horas_requeridas=?, curp=?, fecha_nacimiento=?, direccion=?, foto=? WHERE id=?"; // Query UPDATE completo.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, e.getNombreCompleto()); // Nuevo nombre.
            ps.setString(2, e.getEscuela()); // Nueva escuela.
            ps.setString(3, e.getContacto()); // Nuevo contacto.
            ps.setString(4, e.getFechaIngreso()); // Nueva fecha ingreso.
            ps.setInt(5, e.getHorasRequeridas()); // Nuevas horas.
            ps.setString(6, e.getCurp()); // Nueva CURP.
            ps.setString(7, e.getFechaNacimiento()); // Nueva fecha nac.
            ps.setString(8, e.getDireccion()); // Nueva dirección.
            ps.setString(9, e.getFoto()); // Nuevo nombre de foto.
            ps.setInt(10, e.getId()); // ID a actualizar.
            ps.executeUpdate(); // Ejecuta actualización.
        } // Cierra recursos.
    } // Fin de actualizar.

    // Cambia el estado del estudiante.
    public void cambiarEstado(int id, String estado) throws SQLException { // Lanza error si falla.
        String sql = "UPDATE estudiantes SET estado=? WHERE id=?"; // Query de estado.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setString(1, estado); // Nuevo estado.
            ps.setInt(2, id); // ID del estudiante.
            ps.executeUpdate(); // Ejecuta actualización.
        } // Cierra recursos.
    } // Fin de cambiarEstado.

    // Suma horas al acumulado del estudiante.
    public void sumarHoras(int id, double horas) throws SQLException { // Lanza error si falla.
        String sql = "UPDATE estudiantes SET horas_acumuladas = horas_acumuladas + ? WHERE id=?"; // Query de suma.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setDouble(1, horas); // Horas a sumar.
            ps.setInt(2, id); // ID del estudiante.
            ps.executeUpdate(); // Ejecuta actualización.
        } // Cierra recursos.
    } // Fin de sumarHoras.

    // Convierte una fila de ResultSet en objeto Estudiante.
    private Estudiante mapear(ResultSet rs) throws SQLException { // Recibe la fila actual.
        Estudiante e = new Estudiante(); // Crea objeto vacío.
        e.setId(rs.getInt("id")); // Mapea ID.
        e.setNombreCompleto(rs.getString("nombre_completo")); // Mapea nombre.
        e.setEscuela(rs.getString("escuela")); // Mapea escuela.
        e.setContacto(rs.getString("contacto")); // Mapea contacto.
        e.setFechaIngreso(rs.getString("fecha_ingreso")); // Mapea fecha ingreso.
        e.setHorasRequeridas(rs.getInt("horas_requeridas")); // Mapea horas meta.
        e.setHorasAcumuladas(rs.getDouble("horas_acumuladas")); // Mapea acumuladas.
        e.setCodigoUnico(rs.getString("codigo_unico")); // Mapea código QR.
        e.setEstado(rs.getString("estado")); // Mapea estado.
        e.setCurp(rs.getString("curp")); // Mapea CURP.
        e.setFechaNacimiento(rs.getString("fecha_nacimiento")); // Mapea fecha nac.
        e.setDireccion(rs.getString("direccion")); // Mapea dirección.
        e.setFoto(rs.getString("foto")); // Mapea nombre de foto.
        return e; // Devuelve el objeto lleno.
    } // Fin de mapear.

        // Devuelve un estudiante por su folio (ID).
    public Estudiante obtenerPorId(int id) throws SQLException { // Lanza error si falla.
        String sql = "SELECT " + COLUMNAS + " FROM estudiantes WHERE id = ?"; // Query por folio.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, id); // Folio a buscar.
            try (ResultSet rs = ps.executeQuery()) { // Ejecuta consulta.
                if (rs.next()) { return mapear(rs); } // Devuelve si existe.
            } // Cierra ResultSet.
        } // Cierra recursos.
        return null; // Folio inexistente.
    } // Fin de obtenerPorId.

    // Elimina todas las asistencias de un estudiante (borra su rastro de movimientos).
    public void eliminarAsistenciasPorEstudiante(int id) throws SQLException { // Lanza error si falla.
        String sql = "DELETE FROM asistencias WHERE estudiante_id = ?"; // Query de borrado en cascada.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, id); // Folio del estudiante.
            ps.executeUpdate(); // Ejecuta el borrado de movimientos.
        } // Cierra recursos.
    } // Fin de eliminarAsistenciasPorEstudiante.

    // Elimina el registro del estudiante. Su folio jamás será reutilizado (AUTOINCREMENT).
    public void eliminar(int id) throws SQLException { // Lanza error si falla.
        String sql = "DELETE FROM estudiantes WHERE id = ?"; // Query de borrado del registro.
        try (Connection con = ConexionDB.conectar(); // Abre conexión.
             PreparedStatement ps = con.prepareStatement(sql)) { // Prepara query.
            ps.setInt(1, id); // Folio a retirar.
            ps.executeUpdate(); // Ejecuta el borrado.
        } // Cierra recursos.
    } // Fin de eliminar.
} // Fin de la clase EstudianteDAO.