package com.seq.sisas.modelo; // Paquete de modelos de datos.

/**
 * Estudiante: entidad que representa a un alumno de servicio social.
 * CONEXIONES:
 * - Usada por com.seq.sisas.repositorio.EstudianteDAO.
 * - Usada por com.seq.sisas.servicio.ServicioEstudiante y la capa ui.
 */
public class Estudiante { // Clase POJO.

    private int id; // ID en base de datos.
    private String nombreCompleto; // Nombre real (obligatorio).
    private String escuela; // Institución educativa (obligatorio).
    private String contacto; // Teléfono o correo (opcional).
    private String fechaIngreso; // Fecha de inicio de servicio (obligatorio).
    private int horasRequeridas; // Meta total de horas (obligatorio).
    private double horasAcumuladas; // Horas registradas actualmente.
    private String codigoUnico; // Token para el QR.
    private String estado; // ACTIVO/INACTIVO/FINALIZADO.
    private String curp; // CURP única del alumno (obligatorio).
    private String fechaNacimiento; // Fecha de nacimiento para calcular edad (obligatorio).
    private String direccion; // Domicilio del alumno (opcional).
    private String foto; // Nombre del archivo de foto en datos/fotos (null si no tiene).

    public Estudiante() {} // Constructor vacío.

    public int getId() { return id; } // Obtiene ID.
    public void setId(int id) { this.id = id; } // Asigna ID.

    public String getNombreCompleto() { return nombreCompleto; } // Obtiene nombre.
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; } // Asigna nombre.

    public String getEscuela() { return escuela; } // Obtiene escuela.
    public void setEscuela(String escuela) { this.escuela = escuela; } // Asigna escuela.

    public String getContacto() { return contacto; } // Obtiene contacto.
    public void setContacto(String contacto) { this.contacto = contacto; } // Asigna contacto.

    public String getFechaIngreso() { return fechaIngreso; } // Obtiene fecha ingreso.
    public void setFechaIngreso(String fechaIngreso) { this.fechaIngreso = fechaIngreso; } // Asigna fecha ingreso.

    public int getHorasRequeridas() { return horasRequeridas; } // Obtiene horas meta.
    public void setHorasRequeridas(int horasRequeridas) { this.horasRequeridas = horasRequeridas; } // Asigna horas meta.

    public double getHorasAcumuladas() { return horasAcumuladas; } // Obtiene horas acumuladas.
    public void setHorasAcumuladas(double horasAcumuladas) { this.horasAcumuladas = horasAcumuladas; } // Asigna horas acumuladas.

    public String getCodigoUnico() { return codigoUnico; } // Obtiene código QR.
    public void setCodigoUnico(String codigoUnico) { this.codigoUnico = codigoUnico; } // Asigna código QR.

    public String getEstado() { return estado; } // Obtiene estado.
    public void setEstado(String estado) { this.estado = estado; } // Asigna estado.

    public String getCurp() { return curp; } // Obtiene CURP.
    public void setCurp(String curp) { this.curp = curp; } // Asigna CURP.

    public String getFechaNacimiento() { return fechaNacimiento; } // Obtiene fecha nacimiento.
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; } // Asigna fecha nacimiento.

    public String getDireccion() { return direccion; } // Obtiene dirección.
    public void setDireccion(String direccion) { this.direccion = direccion; } // Asigna dirección.
    
    public String getFoto() { return foto; } // Obtiene nombre de archivo de foto.
    public void setFoto(String foto) { this.foto = foto; } // Asigna nombre de archivo de foto.
} // Fin de la clase Estudiante.