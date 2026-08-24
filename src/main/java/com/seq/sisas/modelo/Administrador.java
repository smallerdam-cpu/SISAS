package com.seq.sisas.modelo; // Paquete de modelos de datos (entidades) del sistema.

/**
 * Administrador: clase POJO (Plain Old Java Object) que representa la tabla 'administradores'.
 * CONEXIONES:
 * - Es utilizada por com.seq.sisas.repositorio.AdministradorDAO (mapeo de BD a Objeto).
 * - Es utilizada por com.seq.sisas.servicio.ServicioAutenticacion (para validar sesión).
 * - No contiene lógica de base de datos, solo transporta datos.
 */
public class Administrador { // Clase pública de modelo.

    // Atributos privados que reflejan las columnas de la tabla en SQLite.
    private int id; // Identificador único (INTEGER PRIMARY KEY).
    private String nombreCompleto; // Nombre real del usuario (TEXT).
    private String usuario; // Nombre de login (TEXT UNIQUE).
    private String passwordHash; // Contraseña cifrada con BCrypt (TEXT).
    private boolean activo; // Estado de la cuenta (1=activo, 0=inactivo).

    // Constructor vacío requerido para instanciar objetos desde la base de datos.
    public Administrador() { // Constructor por defecto.
    } // Fin del constructor.

    // --- MÉTODOS GETTERS Y SETTERS (Acceso controlado a los atributos) ---

    public int getId() { return id; } // Obtiene el ID.
    public void setId(int id) { this.id = id; } // Asigna el ID.

    public String getNombreCompleto() { return nombreCompleto; } // Obtiene el nombre.
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; } // Asigna el nombre.

    public String getUsuario() { return usuario; } // Obtiene el usuario.
    public void setUsuario(String usuario) { this.usuario = usuario; } // Asigna el usuario.

    public String getPasswordHash() { return passwordHash; } // Obtiene el hash (nunca se imprime).
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; } // Asigna el hash.

    public boolean isActivo() { return activo; } // Obtiene el estado (true/false).
    public void setActivo(boolean activo) { this.activo = activo; } // Asigna el estado.

} // Fin de la clase Administrador.