package com.seq.sisas.servicio; // Paquete de lógica de negocio.

import com.seq.sisas.modelo.RegistroAuditoria; // Entidad de evento.
import com.seq.sisas.repositorio.AuditoriaDAO; // DAO de auditoría.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Listas de Java.

/**
 * ServicioAuditoria: consulta el historial de seguridad de solo lectura.
 * CONEXIONES:
 * - Usa com.seq.sisas.repositorio.AuditoriaDAO.
 * - Es invocado por com.seq.sisas.ui.VentanaAuditoria.
 */
public class ServicioAuditoria { // Clase de servicio.

    private AuditoriaDAO auditoriaDAO; // DAO de auditoría.

    public ServicioAuditoria() { // Constructor.
        this.auditoriaDAO = new AuditoriaDAO(); // Inicializa el DAO.
    } // Fin del constructor.

    // Devuelve el historial filtrado por acción, detalle o responsable.
    public List<RegistroAuditoria> buscar(String filtro) throws SQLException { // Lanza error si falla.
        return auditoriaDAO.buscar(filtro); // Delega al DAO.
    } // Fin de buscar.
} // Fin de la clase ServicioAuditoria.