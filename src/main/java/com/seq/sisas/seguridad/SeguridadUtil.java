package com.seq.sisas.seguridad; // Paquete de seguridad del sistema SISAS.

import org.mindrot.jbcrypt.BCrypt; // Librería BCrypt para cifrado de contraseñas.

/**
 * SeguridadUtil: cifrado y verificación de contraseñas de administradores.
 * CONEXIONES:
 * - Es usada por com.seq.sisas.db.InicializadorSistema (cifra la contraseña inicial).
 * - Será usada por com.seq.sisas.seguridad.ServicioAutenticacion en V1.2.
 * - No depende de ninguna otra clase del sistema.
 */
public class SeguridadUtil { // Clase utilitaria de seguridad.

    // Costo BCrypt: 12 rondas (equilibrio seguridad/rendimiento en escritorio).
    private static final int COSTO = 12; // Constante de trabajo del algoritmo.

    // Recibe la contraseña en texto plano y devuelve su hash seguro.
    public static String cifrar(String password) { // password: texto plano escrito por el administrador.
        return BCrypt.hashpw(password, BCrypt.gensalt(COSTO)); // Genera salt automático y devuelve el hash.
    } // Fin del método cifrar.

    // Compara un texto plano contra un hash guardado; devuelve true si coinciden.
    public static boolean verificar(String password, String hashGuardado) { // password escrito vs hash en BD.
        return BCrypt.checkpw(password, hashGuardado); // Comparación segura sin descifrar nada.
    } // Fin del método verificar.
} // Fin de la clase SeguridadUtil.