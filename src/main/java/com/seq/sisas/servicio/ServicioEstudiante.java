package com.seq.sisas.servicio; // Paquete de lógica de negocio.

import com.seq.sisas.modelo.Estudiante; // Entidad alumno.
import com.seq.sisas.repositorio.AuditoriaDAO; // Registro de eventos.
import com.seq.sisas.repositorio.EstudianteDAO; // Acceso a datos de alumnos.
import com.seq.sisas.utilidad.GeneradorCodigo; // Generador de tokens.
import com.seq.sisas.utilidad.GeneradorQR; // Generador de imágenes QR.
import java.io.File; // Manejo de archivos de foto.
import java.nio.file.Files; // Copia de archivos moderna.
import java.nio.file.Paths; // Rutas de archivo.
import java.nio.file.StandardCopyOption; // Opción para reemplazar archivo existente.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Listas de Java.
import com.seq.sisas.repositorio.AsistenciaDAO; // DAO de movimientos para el ajuste.
import java.time.LocalDate; // Fecha del ajuste.

/**
 * ServicioEstudiante (V7): registro con foto opcional, edición y estados.
 * CONEXIONES:
 * - Conecta EstudianteDAO, AuditoriaDAO, utilidades QR y carpeta datos/fotos.
 * - Es invocado por com.seq.sisas.ui.DialogoEstudiante y VentanaGestionEstudiantes.
 */
public class ServicioEstudiante { // Clase de servicio.

    private AsistenciaDAO asistenciaDAO; // DAO de movimientos.
    private EstudianteDAO estudianteDAO; // DAO de estudiantes.
    private AuditoriaDAO auditoriaDAO; // DAO de auditoría.
    private static final String RUTA_QR = "datos/qr"; // Carpeta de QR.
    private static final String RUTA_FOTOS = "datos/fotos"; // Carpeta de fotografías.

    public ServicioEstudiante() { // Constructor.
        this.estudianteDAO = new EstudianteDAO(); // Inicializa DAO alumnos.
        this.asistenciaDAO = new AsistenciaDAO(); // Inicializa DAO de movimientos.
        this.auditoriaDAO = new AuditoriaDAO(); // Inicializa DAO auditoría.
    } // Fin del constructor.

    // Registra alumno nuevo con foto opcional, genera código y QR, y audita.
    public String registrarEstudiante(Estudiante e, Integer adminId, File foto) throws Exception { // Lanza Exception general.
        if (estudianteDAO.obtenerPorCurp(e.getCurp()) != null) { // Si la CURP ya existe...
            throw new IllegalStateException("La CURP ya esta registrada en otro estudiante."); // ...error controlado.
        } // Fin de validación de CURP.
        String codigo = GeneradorCodigo.generar(); // Genera token de 8 caracteres.
        e.setCodigoUnico(codigo); // Asigna token al objeto.
        if (foto != null) { e.setFoto(codigo + obtenerExtension(foto)); } // Asigna nombre de foto basado en el código.
        estudianteDAO.insertar(e); // Guarda en SQLite.
        if (foto != null) { copiarFoto(foto, e.getFoto()); } // Copia la imagen a datos/fotos.
        GeneradorQR.generarImagen(codigo, RUTA_QR); // Crea el PNG del QR.
        auditoriaDAO.registrar(adminId, "REGISTRO_ESTUDIANTE", "Alumno: " + e.getNombreCompleto() + " | CURP: " + e.getCurp()); // Audita con responsable.
        return codigo; // Devuelve código para mostrarlo.
    } // Fin de registrarEstudiante.

    // Actualiza datos, reemplaza la foto si se carga una nueva, y audita.
    public void actualizarEstudiante(Estudiante e, Integer adminId, File foto) throws Exception { // Lanza Exception general.
        if (foto != null) { // Si se cargó una foto nueva...
            String fotoVieja = e.getFoto(); // Guarda el nombre anterior.
            String fotoNueva = e.getCodigoUnico() + obtenerExtension(foto); // Nuevo nombre basado en el código.
            if (fotoVieja != null && !fotoVieja.equals(fotoNueva)) { new File(RUTA_FOTOS + "/" + fotoVieja).delete(); } // Borra la foto vieja para no ocupar espacio.
            e.setFoto(fotoNueva); // Asigna el nuevo nombre.
            copiarFoto(foto, fotoNueva); // Copia el archivo nuevo.
        } // Fin de manejo de foto.
        estudianteDAO.actualizar(e); // Guarda cambios en SQLite.
        auditoriaDAO.registrar(adminId, "MODIFICACION_ESTUDIANTE", "ID: " + e.getId() + " | " + e.getNombreCompleto()); // Audita con responsable.
    } // Fin de actualizarEstudiante.

    // Devuelve la lista completa de estudiantes.
    public List<Estudiante> listarEstudiantes() throws SQLException { // Lanza error si falla.
        return estudianteDAO.obtenerTodos(); // Delega al DAO.
    } // Fin de listarEstudiantes.

    // Devuelve estudiantes que coincidan con el criterio de búsqueda.
    public List<Estudiante> buscarEstudiantes(String criterio) throws SQLException { // Lanza error si falla.
        return estudianteDAO.buscar(criterio); // Delega al DAO.
    } // Fin de buscarEstudiantes.

    // Cambia el estado de un estudiante y audita.
    public void cambiarEstadoEstudiante(int id, String nuevoEstado, Integer adminId) throws SQLException { // Lanza error si falla.
        estudianteDAO.cambiarEstado(id, nuevoEstado); // Delega al DAO.
        auditoriaDAO.registrar(adminId, "CAMBIO_ESTADO_ESTUDIANTE", "ID: " + id + " -> " + nuevoEstado); // Audita con responsable.
    } // Fin de cambiarEstadoEstudiante.

    // Copia el archivo de imagen elegido a la carpeta datos/fotos.
    private void copiarFoto(File origen, String nombreDestino) throws Exception { // Lanza error de I/O si falla.
        File carpeta = new File(RUTA_FOTOS); // Carpeta destino.
        if (!carpeta.exists()) { carpeta.mkdirs(); } // La crea si no existe.
        Files.copy(origen.toPath(), Paths.get(RUTA_FOTOS, nombreDestino), StandardCopyOption.REPLACE_EXISTING); // Copia reemplazando si ya existe.
    } // Fin de copiarFoto.

    // Extrae la extensión del archivo de imagen (.jpg, .png, etc.).
    private String obtenerExtension(File f) { // Recibe el archivo elegido.
        String nombre = f.getName(); // Nombre original con extensión.
        int i = nombre.lastIndexOf('.'); // Posición del último punto.
        return (i >= 0) ? nombre.substring(i).toLowerCase() : ".jpg"; // Devuelve extensión en minúsculas o .jpg por defecto.
    } // Fin de obtenerExtension.

        // Elimina todo el rastro del alumno: asistencias, registro, QR y foto. Audita al responsable.
    public void eliminarEstudiante(int id, Integer adminId) throws Exception { // Lanza Exception general.
        Estudiante e = estudianteDAO.obtenerPorId(id); // Consulta al alumno antes de borrarlo.
        if (e == null) { throw new IllegalStateException("El estudiante con ese folio no existe."); } // Protección contra folios inexistentes.
        estudianteDAO.eliminarAsistenciasPorEstudiante(id); // Borra sus asistencias de la BD.
        estudianteDAO.eliminar(id); // Borra el registro del alumno (el folio queda retirado para siempre).
        if (e.getCodigoUnico() != null) { new File(RUTA_QR + "/" + e.getCodigoUnico() + ".png").delete(); } // Borra su archivo QR del disco.
        if (e.getFoto() != null) { new File(RUTA_FOTOS + "/" + e.getFoto()).delete(); } // Borra su fotografía del disco.
        auditoriaDAO.registrar(adminId, "ELIMINACION_ESTUDIANTE", "Folio: " + id + " | " + e.getNombreCompleto() + " | CURP: " + e.getCurp()); // Firma la acción con el responsable.
    } // Fin de eliminarEstudiante.

        // Aplica un ajuste manual de horas con reflejo en reportes, estado y auditoría.
    public void ajustarHoras(int estudianteId, int deltaMinutos, String motivo, Integer adminId) throws Exception { // Lanza Exception controlada.
        Estudiante est = estudianteDAO.obtenerPorId(estudianteId); // Consulta al alumno antes de ajustar.
        if (est == null) { throw new IllegalStateException("El estudiante no existe."); } // Protección de folio inexistente.
        double deltaHoras = deltaMinutos / 60.0; // Convierte el delta a horas decimales.
        double nuevoTotal = est.getHorasAcumuladas() + deltaHoras; // Total proyectado tras el ajuste.
        if (nuevoTotal < 0) { throw new IllegalStateException("No se pueden quitar mas horas de las acumuladas."); } // Regla de seguridad.
        asistenciaDAO.registrarAjuste(estudianteId, LocalDate.now().toString(), deltaMinutos); // Fila AJUSTE visible en reportes.
        estudianteDAO.sumarHoras(estudianteId, deltaHoras); // Actualiza el acumulado del alumno.
        if (nuevoTotal >= est.getHorasRequeridas() && est.getEstado().equals("ACTIVO")) { // Si con el ajuste alcanza la meta...
            estudianteDAO.cambiarEstado(estudianteId, "FINALIZADO"); // ...finaliza automáticamente.
        } else if (nuevoTotal < est.getHorasRequeridas() && est.getEstado().equals("FINALIZADO")) { // Si quitando horas baja de la meta...
            estudianteDAO.cambiarEstado(estudianteId, "ACTIVO"); // ...reabre el servicio automáticamente.
        } // Fin de recálculo de estado.
        String signo = deltaMinutos > 0 ? "+" : ""; // Signo legible para la auditoría.
        auditoriaDAO.registrar(adminId, "AJUSTE_HORAS", "Folio: " + estudianteId + " | " + est.getNombreCompleto() + " | " + signo + deltaMinutos + " min | Motivo: " + motivo); // Firma el ajuste con responsable y motivo.
    } // Fin de ajustarHoras.
} // Fin de la clase ServicioEstudiante.