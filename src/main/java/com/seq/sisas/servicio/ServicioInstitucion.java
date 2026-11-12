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
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        } // Nombre vacío.
        if (!"PREPA".equals(nivel) && !"UNIVERSIDAD".equals(nivel)) {
            throw new IllegalArgumentException("El nivel debe ser PREPA o UNIVERSIDAD.");
        } // Nivel inválido.
        for (Institucion existente : dao.obtenerTodas()) { // Revisa duplicados.
            if (existente.getNombre().equalsIgnoreCase(nombre.trim()) && existente.getNivel().equals(nivel)) {
                throw new IllegalStateException("La institución ya existe.");
            } // Nombre + nivel duplicado.
        } // Fin de revisión de duplicados.
        Institucion inst = new Institucion(); // Crea objeto vacío.
        inst.setNombre(nombre.trim()); // Asigna nombre.
        inst.setNivel(nivel); // Asigna nivel.
        inst.setActivo(1); // Toda institución nueva nace activa.
        dao.insertar(inst); // Guarda en SQLite.
        auditoriaDAO.registrar(responsableId, "CREACION_INSTITUCION",
                "Nombre: " + inst.getNombre() + " | Nivel: " + nivel); // Firma la creación.
    } // Fin de crear.
} // Fin de la clase ServicioInstitucion.