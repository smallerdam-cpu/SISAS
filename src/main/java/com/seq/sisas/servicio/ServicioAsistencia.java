package com.seq.sisas.servicio; // Paquete de lógica de negocio.

import com.seq.sisas.modelo.Asistencia; // Entidad de movimiento.
import com.seq.sisas.modelo.Estudiante; // Entidad de alumno.
import com.seq.sisas.repositorio.AsistenciaDAO; // DAO de movimientos.
import com.seq.sisas.repositorio.AuditoriaDAO; // DAO de auditoría.
import com.seq.sisas.repositorio.EstudianteDAO; // DAO de alumnos.
import java.sql.SQLException; // Errores de BD.
import java.time.Duration; // Cálculo de diferencias de tiempo.
import java.time.LocalDateTime; // Fecha y hora actual.
import java.time.format.DateTimeFormatter; // Formato de timestamps.

/**
 * ServicioAsistencia: decide si un escaneo es ENTRADA o SALIDA y calcula horas.
 * CONEXIONES:
 * - Usa EstudianteDAO, AsistenciaDAO y AuditoriaDAO.
 * - Es invocado por com.seq.sisas.ui.VentanaAsistencia.
 */
public class ServicioAsistencia { // Clase de servicio.

    private EstudianteDAO estudianteDAO; // DAO de alumnos.
    private AsistenciaDAO asistenciaDAO; // DAO de movimientos.
    private AuditoriaDAO auditoriaDAO; // DAO de auditoría.

    // Formato completo para timestamps guardados en BD.
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Patrón de timestamp.
    // Formato corto para la columna fecha (reportes por día).
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Patrón de día.

    public ServicioAsistencia() { // Constructor.
        this.estudianteDAO = new EstudianteDAO(); // Inicializa DAO alumnos.
        this.asistenciaDAO = new AsistenciaDAO(); // Inicializa DAO movimientos.
        this.auditoriaDAO = new AuditoriaDAO(); // Inicializa DAO auditoría.
    } // Fin del constructor.
    

    // Método central: procesa un código y devuelve el mensaje para pantalla.
    public String registrarMovimiento(String codigo, Integer adminId) throws SQLException { // Lanza error si falla.

        codigo = codigo.trim(); // Limpia espacios del código.
        if (codigo.isEmpty()) { return "Escriba o escanee un codigo."; } // Validación de vacío.

        LocalDateTime ahora = LocalDateTime.now(); // Momento exacto del escaneo.

        // 1. Buscar al estudiante por su código único.
        Estudiante est = estudianteDAO.obtenerPorCodigo(codigo); // Consulta por código.
        if (est == null) { return "Estudiante no encontrado: " + codigo; } // Código inexistente.

        // 2. Validar estado del estudiante.
        if (est.getEstado().equals("INACTIVO")) { return "Estudiante INACTIVO. No puede registrar asistencia."; } // Bloquea inactivos.
        if (est.getEstado().equals("FINALIZADO")) { return "El estudiante ya finalizo sus horas de servicio."; } // Bloquea finalizados.

        // 3. Revisar si tiene una sesión ABIERTA.
        Asistencia abierta = asistenciaDAO.obtenerAbierta(est.getId()); // Busca sesión abierta.

        if (abierta != null) { // Hay sesión abierta: este escaneo es una SALIDA.

            // Calcula los minutos transcurridos desde la entrada (maneja cruces de medianoche).
            LocalDateTime entrada = LocalDateTime.parse(abierta.getHoraEntrada(), FORMATO); // Convierte el texto a fecha/hora.
            long minutos = Duration.between(entrada, ahora).toMinutes(); // Diferencia en minutos.
            if (minutos < 0) { minutos = 0; } // Protección contra relojes alterados.

            asistenciaDAO.cerrar(abierta.getId(), ahora.format(FORMATO), (int) minutos); // Cierra la sesión con minutos.

            double horas = minutos / 60.0; // Convierte minutos a horas decimales.
            estudianteDAO.sumarHoras(est.getId(), horas); // Acumula horas al estudiante.
            double nuevoTotal = est.getHorasAcumuladas() + horas; // Total actualizado en memoria.

            auditoriaDAO.registrar(adminId, "REGISTRO_SALIDA", est.getNombreCompleto() + " | " + minutos + " min"); // Audita la salida.

            String msg = "SALIDA de " + est.getNombreCompleto() + ". Tiempo de hoy: " + minutos + " min. Total: " + String.format("%.2f", nuevoTotal) + " hrs."; // Mensaje de éxito.

            // 4. Verificar si completó su meta de horas.
            if (nuevoTotal >= est.getHorasRequeridas()) { // Si alcanzó la meta...
                estudianteDAO.cambiarEstado(est.getId(), "FINALIZADO"); // ...marca como finalizado.
                msg += " SERVICIO FINALIZADO."; // Agrega aviso al mensaje.
            } // Fin de verificación de meta.

            return msg; // Devuelve mensaje de salida.

        } else { // No hay sesión abierta: este escaneo es una ENTRADA.

            // 5. Anti-duplicado: bloquea abrir sesión si cerró hace menos de 2 minutos.
            Asistencia ultima = asistenciaDAO.obtenerUltima(est.getId()); // Último movimiento del alumno.
            if (ultima != null && ultima.getHoraSalida() != null && !ultima.getHoraSalida().equals("AJUSTE")) { // Ignora ajustes manuales en el anti-duplicado.
                LocalDateTime salida = LocalDateTime.parse(ultima.getHoraSalida(), FORMATO); // Convierte la salida a fecha/hora.
                if (Duration.between(salida, ahora).toMinutes() < 2) { // Si pasaron menos de 2 minutos...
                    return "Registro reciente detectado. Espere antes de volver a escanear."; // ...bloquea duplicado.
                } // Fin de validación de tiempo.
            } // Fin de validación de último movimiento.

            // 6. Registrar la ENTRADA.
            asistenciaDAO.registrarEntrada(est.getId(), ahora.format(FORMATO_FECHA), ahora.format(FORMATO)); // Inserta sesión ABIERTA.
            auditoriaDAO.registrar(adminId, "REGISTRO_ENTRADA", est.getNombreCompleto()); // Audita la entrada.
            return "ENTRADA de " + est.getNombreCompleto() + " a las " + ahora.format(FORMATO); // Mensaje de entrada.
        } // Fin de evaluación de sesión.
        
    } // Fin de registrarMovimiento.
        // Devuelve la ficha del estudiante (nombre y foto) para mostrarla en pantalla.
    public Estudiante obtenerEstudiantePorCodigo(String codigo) throws SQLException { // Lanza error si falla.
        return estudianteDAO.obtenerPorCodigo(codigo); // Delega al DAO.
    } // Fin de obtenerEstudiantePorCodigo.
} // Fin de la clase ServicioAsistencia.