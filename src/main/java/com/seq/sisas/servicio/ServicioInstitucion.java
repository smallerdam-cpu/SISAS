package com.seq.sisas.servicio; // Paquete de lógica de negocio.

import com.seq.sisas.modelo.Institucion; // Entidad plantel.
import com.seq.sisas.repositorio.AuditoriaDAO; // Registro de eventos.
import com.seq.sisas.repositorio.InstitucionDAO; // Acceso a datos de planteles.
import java.sql.SQLException; // Errores de BD.
import java.util.List; // Listas de Java.

/**
 * ServicioInstitucion: crea y lista prepas y universidades con auditoría.
 * CONEXIONES:
 * - Usa InstitucionDAO y AuditoriaDAO.
 * - Es invocado por la ventana de Gestión de Prepas y Universidades (paso
 * posterior).
 */
public class ServicioInstitucion { // Clase de servicio.
    private InstitucionDAO dao; // DAO de planteles.
    private AuditoriaDAO auditoriaDAO; // DAO de auditoría.

    public ServicioInstitucion() { // Constructor.
        this.dao = new InstitucionDAO(); // Inicializa DAO de planteles.
        this.auditoriaDAO = new AuditoriaDAO(); // Inicializa DAO de auditoría.
    } // Fin del constructor.
      // Devuelve todas las instituciones activas para la tabla.

    public List<Institucion> listar() throws SQLException { // Lanza error si falla.
        return dao.obtenerTodas(); // Delega al DAO.
    } // Fin de listar.
      // Devuelve solo las instituciones activas de un nivel (PREPA o UNIVERSIDAD).

    public List<Institucion> listarPorNivel(String nivel) throws SQLException { // Lanza error si falla.
        return dao.obtenerPorNivel(nivel); // Delega al DAO.
    } // Fin de listarPorNivel.
      // Crea una institución nueva validando nombre y nivel, y audita.

    public void crear(String nombre, String nivel, Integer responsableId) throws Exception { // Lanza Exception
                                                                                             // controlada.
        Institucion inst = new Institucion(); // Crea objeto vacío.
        inst.setNombre(nombre); // Asigna nombre.
        inst.setNivel(nivel); // Asigna nivel.
        crear(inst, responsableId); // Delega a la versión completa.
    } // Fin de crear.

    // Crea una institución nueva con los 6 datos de contacto, validando y
    // auditando.
    public void crear(Institucion inst, Integer responsableId) throws Exception { // Lanza Exception controlada.
        if (inst.getNombre() == null || inst.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        } // Nombre vacío.
        if (!"PREPA".equals(inst.getNivel()) && !"UNIVERSIDAD".equals(inst.getNivel())) {
            throw new IllegalArgumentException("El nivel debe ser PREPA o UNIVERSIDAD.");
        } // Nivel inválido.
        for (Institucion existente : dao.obtenerTodas()) { // Revisa duplicados.
            if (existente.getNombre().equalsIgnoreCase(inst.getNombre().trim())
                    && existente.getNivel().equals(inst.getNivel())) {
                throw new IllegalStateException("La institución ya existe.");
            } // Nombre + nivel duplicado.
        } // Fin de revisión de duplicados.
        inst.setNombre(inst.getNombre().trim()); // Limpia nombre.
        inst.setActivo(1); // Toda institución nueva nace activa.
        dao.insertar(inst); // Guarda en SQLite.
        auditoriaDAO.registrar(responsableId, "CREACION_INSTITUCION",
                "Nombre: " + inst.getNombre() + " | Nivel: " + inst.getNivel()); // Firma la creación.
    } // Fin de crear.

    // Edita una institución existente con los 6 datos de contacto, validando y
    // auditando.
    public void editar(Institucion inst, Integer responsableId) throws Exception { // Lanza Exception controlada.
        if (inst.getId() <= 0) {
            throw new IllegalArgumentException("La institución a editar no existe.");
        } // Sin ID.
        if (inst.getNombre() == null || inst.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        } // Nombre vacío.
        if (!"PREPA".equals(inst.getNivel()) && !"UNIVERSIDAD".equals(inst.getNivel())) {
            throw new IllegalArgumentException("El nivel debe ser PREPA o UNIVERSIDAD.");
        } // Nivel inválido.
        for (Institucion existente : dao.obtenerTodas()) { // Revisa duplicados.
            if (existente.getId() != inst.getId()
                    && existente.getNombre().equalsIgnoreCase(inst.getNombre().trim())
                    && existente.getNivel().equals(inst.getNivel())) {
                throw new IllegalStateException("Ya existe otra institución con ese nombre y nivel.");
            } // Nombre + nivel duplicado en otra.
        } // Fin de revisión de duplicados.
        inst.setNombre(inst.getNombre().trim()); // Limpia nombre.
        dao.actualizar(inst); // Guarda cambios en SQLite.
        auditoriaDAO.registrar(responsableId, "MODIFICACION_INSTITUCION",
                "ID: " + inst.getId() + " | Nombre: " + inst.getNombre() + " | Nivel: " + inst.getNivel()); // Firma la
                                                                                                            // edición.
    } // Fin de editar.
} // Fin de la clase ServicioInstitucion.