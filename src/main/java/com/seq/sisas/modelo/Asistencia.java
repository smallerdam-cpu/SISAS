package com.seq.sisas.modelo; // Paquete de modelos de datos.

/**
 * Asistencia: entidad que representa un movimiento de entrada/salida.
 * CONEXIONES:
 * - Usada por com.seq.sisas.repositorio.AsistenciaDAO.
 * - Usada por com.seq.sisas.servicio.ServicioAsistencia.
 */
public class Asistencia { // Clase POJO.

    private int id; // ID en base de datos.
    private int estudianteId; // ID del alumno propietario.
    private String fecha; // Día en formato yyyy-MM-dd.
    private String horaEntrada; // Timestamp completo de entrada.
    private String horaSalida; // Timestamp completo de salida (null si abierta).
    private int minutos; // Duración calculada en minutos.
    private String estado; // ABIERTA o CERRADA.

    public Asistencia() {} // Constructor vacío.

    public int getId() { return id; } // Obtiene ID.
    public void setId(int id) { this.id = id; } // Asigna ID.

    public int getEstudianteId() { return estudianteId; } // Obtiene ID del alumno.
    public void setEstudianteId(int estudianteId) { this.estudianteId = estudianteId; } // Asigna ID del alumno.

    public String getFecha() { return fecha; } // Obtiene fecha.
    public void setFecha(String fecha) { this.fecha = fecha; } // Asigna fecha.

    public String getHoraEntrada() { return horaEntrada; } // Obtiene hora de entrada.
    public void setHoraEntrada(String horaEntrada) { this.horaEntrada = horaEntrada; } // Asigna hora de entrada.

    public String getHoraSalida() { return horaSalida; } // Obtiene hora de salida.
    public void setHoraSalida(String horaSalida) { this.horaSalida = horaSalida; } // Asigna hora de salida.

    public int getMinutos() { return minutos; } // Obtiene minutos.
    public void setMinutos(int minutos) { this.minutos = minutos; } // Asigna minutos.

    public String getEstado() { return estado; } // Obtiene estado.
    public void setEstado(String estado) { this.estado = estado; } // Asigna estado.
} // Fin de la clase Asistencia.