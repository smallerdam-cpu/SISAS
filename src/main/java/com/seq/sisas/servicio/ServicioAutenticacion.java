package com.seq.sisas.servicio; // Paquete de lógica de negocio (Servicios).

import com.seq.sisas.modelo.Administrador; // Entidad de datos.
import com.seq.sisas.repositorio.AdministradorDAO; // Capa de acceso a datos de admins.
import com.seq.sisas.repositorio.AuditoriaDAO; // Capa de registro de eventos.
import com.seq.sisas.seguridad.SeguridadUtil; // Utilidad de cifrado BCrypt.
import java.sql.SQLException; // Manejo de errores de BD.

/**
 * ServicioAutenticacion: orquesta la validación de credenciales.
 * CONEXIONES:
 * - Conecta el DAO de administradores con el DAO de auditoría.
 * - Usa com.seq.sisas.seguridad.SeguridadUtil para comparar hashes.
 * - Es llamado por la capa de presentación (Consola o Swing).
 */
public class ServicioAutenticacion { // Clase de servicio de seguridad.

    // Instancias de los DAOs inyectadas manualmente (sin frameworks por ahora).
    private AdministradorDAO adminDAO; // Acceso a tabla administradores.
    private AuditoriaDAO auditoriaDAO; // Acceso a tabla auditoria.

    // Constructor que inicializa las dependencias del servicio.
    public ServicioAutenticacion() { // Inicialización al crear el servicio.
        this.adminDAO = new AdministradorDAO(); // Crea instancia del DAO de admins.
        this.auditoriaDAO = new AuditoriaDAO(); // Crea instancia del DAO de auditoría.
    } // Fin del constructor.

    // Método principal de validación de credenciales.
    public Administrador iniciarSesion(String usuario, String password) throws SQLException { // Lanza error de BD si falla.
        
        // 1. Buscar el usuario en la base de datos.
        Administrador admin = adminDAO.obtenerPorUsuario(usuario); // Llamada al DAO.

        // 2. Validar si el usuario existe.
        if (admin == null) { // Si no se encontró en la base de datos.
            auditoriaDAO.registrar(null, "INTENTO_FALLIDO", "Usuario no encontrado: " + usuario); // Registra el fallo.
            return null; // Devuelve nulo indicando fallo de autenticación.
        } // Fin de validación de existencia.

        // 3. Validar si la cuenta está activa.
        if (!admin.isActivo()) { // Si el administrador fue deshabilitado.
            auditoriaDAO.registrar(admin.getId(), "INTENTO_FALLIDO", "Cuenta inactiva."); // Registra el evento.
            return null; // Rechaza el acceso.
        } // Fin de validación de estado.

        // 4. Comparar la contraseña escrita contra el hash guardado usando BCrypt.
        boolean passwordValido = SeguridadUtil.verificar(password, admin.getPasswordHash()); // Verificación criptográfica.

        // 5. Evaluar el resultado de la comparación.
        if (passwordValido) { // Si la contraseña es correcta.
            auditoriaDAO.registrar(admin.getId(), "INICIO_SESION", "Acceso exitoso."); // Auditoría de éxito.
            adminDAO.actualizarUltimoAcceso(admin.getId()); // Registra la fecha/hora de este inicio de sesión.
            return admin; // Devuelve el objeto Administrador (sesión iniciada).
        } else { // Si la contraseña es incorrecta.
            auditoriaDAO.registrar(admin.getId(), "INTENTO_FALLIDO", "Contraseña incorrecta."); // Auditoría de fracaso.
            return null; // Rechaza el acceso.
        } // Fin de la evaluación de contraseña.

    } // Fin del método iniciarSesion.
} // Fin de la clase ServicioAutenticacion.