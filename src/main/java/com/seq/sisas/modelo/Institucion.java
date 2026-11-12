package com.seq.sisas.modelo; // Paquete de modelos de datos.

/**
 * POJO para representar un plantel o institución educativa.
 */

public class Institucion { // POJO catálogo de planteles.
    private int id; // ID en base de datos.
    private String nombre; // Nombre del plantel (único).
    private String nivel; // PREPA o UNIVERSIDAD.
    private int activo; // 1 = activo, 0 = inactivo.

    public Institucion() {
    } // Constructor vacío.

    public int getId() {
        return id;
    } // Obtiene ID.

    public void setId(int id) {
        this.id = id;
    } // Asigna ID.

    public String getNombre() {
        return nombre;
    } // Obtiene nombre.

    public void setNombre(String nombre) {
        this.nombre = nombre;
    } // Asigna nombre.

    public String getNivel() {
        return nivel;
    } // Obtiene nivel.

    public void setNivel(String nivel) {
        this.nivel = nivel;
    } // Asigna nivel.

    public int getActivo() {
        return activo;
    } // Obtiene activo.

    public void setActivo(int activo) {
        this.activo = activo;
    } // Asigna activo.

    @Override
    public String toString() {
        return nombre;
    } // Muestra nombre en JComboBox.
} // Fin de la clase Institucion.