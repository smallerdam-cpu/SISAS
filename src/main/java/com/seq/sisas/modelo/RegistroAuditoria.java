package com.seq.sisas.modelo; // Paquete de modelos de datos.

/**
 * RegistroAuditoria: entidad de un evento del historial de seguridad.
 * CONEXIONES:
 * - Usada por com.seq.sisas.repositorio.AuditoriaDAO.
 * - Usada por com.seq.sisas.servicio.ServicioAuditoria y com.seq.sisas.ui.VentanaAuditoria.
 */
public class RegistroAuditoria { // Clase POJO.

    private int id; // ID del evento.
    private String responsable; // Nombre del administrador o "SISTEMA".
    private String accion; // Nombre de la acción realizada.
    private String detalle; // Descripción adicional del cambio.
    private String fechaHora; // Momento exacto del evento.

    public RegistroAuditoria() {} // Constructor vacío.

    public int getId() { return id; } // Obtiene ID.
    public void setId(int id) { this.id = id; } // Asigna ID.

    public String getResponsable() { return responsable; } // Obtiene responsable.
    public void setResponsable(String responsable) { this.responsable = responsable; } // Asigna responsable.

    public String getAccion() { return accion; } // Obtiene acción.
    public void setAccion(String accion) { this.accion = accion; } // Asigna acción.

    public String getDetalle() { return detalle; } // Obtiene detalle.
    public void setDetalle(String detalle) { this.detalle = detalle; } // Asigna detalle.

    public String getFechaHora() { return fechaHora; } // Obtiene fecha/hora.
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; } // Asigna fecha/hora.
} // Fin de la clase RegistroAuditoria.