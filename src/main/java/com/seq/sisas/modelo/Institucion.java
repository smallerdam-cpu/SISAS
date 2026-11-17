package com.seq.sisas.modelo; // Paquete de modelos de datos.

/**
 * POJO para representar un plantel o institución educativa.
 */

public class Institucion { // POJO catálogo de planteles.
    private int id; // ID en base de datos.
    private String nombre; // Nombre del plantel (único).
    private String nivel; // PREPA o UNIVERSIDAD.
    private int activo; // 1 = activo, 0 = inactivo.
    private String claveCentroTrabajo; // Número de centro de trabajo (CCT).
    private String direccion; // Dirección del plantel.
    private String codigoPostal; // Código postal.
    private String director; // Nombre del director o coordinador.
    private String telefono; // Número de la escuela.
    private String correo; // Correo electrónico del plantel.

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

    public String getClaveCentroTrabajo() {
        return claveCentroTrabajo;
    } // Obtiene CCT.

    public void setClaveCentroTrabajo(String claveCentroTrabajo) {
        this.claveCentroTrabajo = claveCentroTrabajo;
    } // Asigna CCT.

    public String getDireccion() {
        return direccion;
    } // Obtiene dirección.

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    } // Asigna dirección.

    public String getCodigoPostal() {
        return codigoPostal;
    } // Obtiene CP.

    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    } // Asigna CP.

    public String getDirector() {
        return director;
    } // Obtiene director o coordinador.

    public void setDirector(String director) {
        this.director = director;
    } // Asigna director o coordinador.

    public String getTelefono() {
        return telefono;
    } // Obtiene teléfono.

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    } // Asigna teléfono.

    public String getCorreo() {
        return correo;
    } // Obtiene correo.

    public void setCorreo(String correo) {
        this.correo = correo;
    } // Asigna correo.

    @Override
    public String toString() {
        return nombre;
    } // Muestra nombre en JComboBox.
} // Fin de la clase Institucion.