package com.seq.sisas.servicio; // Paquete de lógica de negocio.

import com.seq.sisas.modelo.Administrador; // Entidad de administrador.
import com.seq.sisas.repositorio.AdministradorDAO; // DAO de cuentas.
import com.seq.sisas.repositorio.AuditoriaDAO; // Registro de eventos.
import com.seq.sisas.seguridad.SeguridadUtil; // Cifrado BCrypt.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Listas de Java.

/**
 * ServicioAdministradores: crea, edita, desactiva y cambia contraseñas con auditoría.
 * CONEXIONES:
 * - Usa AdministradorDAO y AuditoriaDAO.
 * - Es invocado por com.seq.sisas.ui.VentanaGestionAdministradores.
 */
public class ServicioAdministradores { // Clase de servicio.

    private AdministradorDAO dao; // DAO de cuentas.
    private AuditoriaDAO auditoriaDAO; // DAO de auditoría.

    public ServicioAdministradores() { // Constructor.
        this.dao = new AdministradorDAO(); // Inicializa DAO de cuentas.
        this.auditoriaDAO = new AuditoriaDAO(); // Inicializa DAO de auditoría.
    } // Fin del constructor.

    // Devuelve todas las cuentas para la tabla.
    public List<Administrador> listar() throws SQLException { // Lanza error si falla.
        return dao.obtenerTodos(); // Delega al DAO.
    } // Fin de listar.

    // Crea una cuenta nueva validando usuario único y contraseña mínima.
    public void crear(String nombre, String usuario, String password, Integer responsableId) throws Exception { // Lanza Exception controlada.
        if (dao.obtenerPorUsuario(usuario) != null) { throw new IllegalStateException("El usuario ya existe."); } // Usuario único.
        Administrador a = new Administrador(); // Crea objeto vacío.
        a.setNombreCompleto(nombre); // Asigna nombre real.
        a.setUsuario(usuario); // Asigna usuario.
        a.setPasswordHash(SeguridadUtil.cifrar(password)); // Cifra la contraseña con BCrypt.
        a.setActivo(true); // Toda cuenta nueva nace activa.
        dao.insertar(a); // Guarda en SQLite.
        auditoriaDAO.registrar(responsableId, "CREACION_ADMINISTRADOR", "Usuario: " + usuario); // Firma la creación.
    } // Fin de crear.

    // Actualiza nombre, usuario y estado; impide desactivar la propia cuenta.
    public void actualizar(Administrador a, Integer responsableId) throws Exception { // Lanza Exception controlada.
        Administrador previo = dao.obtenerPorUsuario(a.getUsuario()); // Busca si el usuario nuevo ya existe.
        if (previo != null && previo.getId() != a.getId()) { throw new IllegalStateException("El usuario ya existe."); } // Usuario único ajeno.
        if (a.getId() == responsableId && !a.isActivo()) { throw new IllegalStateException("No puedes desactivar tu propia cuenta."); } // Regla de seguridad.
        dao.actualizar(a); // Guarda cambios.
        auditoriaDAO.registrar(responsableId, "MODIFICACION_ADMINISTRADOR", "ID: " + a.getId() + " | " + a.getUsuario()); // Firma la modificación.
    } // Fin de actualizar.

    // Cambia la contraseña de una cuenta y audita.
    public void cambiarPassword(int id, String nuevaPassword, Integer responsableId) throws Exception { // Lanza Exception controlada.
        dao.cambiarPassword(id, SeguridadUtil.cifrar(nuevaPassword)); // Cifra y guarda el nuevo hash.
        auditoriaDAO.registrar(responsableId, "CAMBIO_CONTRASENA_ADMINISTRADOR", "ID de cuenta: " + id); // Firma el cambio.
    } // Fin de cambiarPassword.
} // Fin de la clase ServicioAdministradores.