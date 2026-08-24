package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import java.awt.BorderLayout; // Diseño principal.
import javax.swing.JDialog; // Ventana modal.
import javax.swing.JFrame; // Ventana padre.
import javax.swing.JScrollPane; // Barra de desplazamiento.
import javax.swing.JTextArea; // Área de texto con el manual.

/**
 * DialogoAyuda (V17): manual completo y actualizado de todos los módulos del sistema.
 * CONEXIONES:
 * - Es abierto desde el menú Ayuda de com.seq.sisas.ui.VentanaPrincipal.
 */
public class DialogoAyuda extends JDialog { // Hereda de JDialog (modal).

    // Texto del manual, organizado por módulo y actualizado con todas las funciones.
    private static final String TEXTO = // Contenido estático del manual.
        "=== AYUDA SISAS ===\n" + // Título del manual.
        "Sistema de Asistencia de Servicio Social\n" + // Nombre oficial del sistema.
        "Depto. de Media Superior y Superior - SEQ\n\n" + // Dependencia institucional.
        "BARRA DE MENUS\n" + // Encabezado de navegación.
        "   Gestion | Asistencia | Reportes | Administracion | Ayuda | Salir.\n\n" + // Menús disponibles.
        "1. GESTION DE ESTUDIANTES\n" + // Encabezado del módulo.
        "   - Buscar: filtra en vivo por nombre, apellidos o CURP.\n" + // Búsqueda en vivo.
        "   - Nuevo: registra alumno con CURP y fecha de nacimiento obligatorias,\n" + // Obligatorios.
        "     direccion opcional, foto de perfil opcional y edad en vivo.\n" + // Opcionales y edad.
        "   - Editar: actualiza datos y fotografia.\n" + // Edición.
        "   - Cambiar Estado: activa o inactiva al alumno.\n" + // Estados.
        "   - Ver QR: muestra el codigo QR del alumno.\n" + // Visor de QR.
        "   - Eliminar: borra todo el rastro (asistencias, QR y foto);\n" + // Borrado seguro.
        "     el folio eliminado NO se reutiliza.\n" + // Regla de folios.
        "   - La columna Horas se muestra cerrada (ej. 0h 20m / 450h).\n\n" + // Formato humano.
        "2. REGISTRO DE ASISTENCIA (MODO KIOSCO)\n" + // Encabezado del kiosco.
        "   - El alumno escribe su codigo o usa lector USB y presiona Enter.\n" + // Formas de escaneo.
        "   - Primer escaneo = ENTRADA, segundo = SALIDA con calculo de horas.\n" + // Movimientos.
        "   - La ficha centrada muestra foto y 'ENTRADA - Bienvenido' o 'SALIDA',\n" + // Ficha visual.
        "     y se oculta sola a los 5 segundos por privacidad.\n" + // Privacidad automática.
        "   - La X no cierra el kiosco; solo 'Acceso Administrador' con contrasena.\n\n" + // Seguridad del kiosco.
        "3. REPORTES Y EXCEL\n" + // Encabezado de reportes.
        "   - Busque y seleccione al alumno; consulte por rango de fechas.\n" + // Consulta con filtros.
        "   - Vista Previa: hoja oficial con logos, datos, tabla, total y firma\n" + // Hoja oficial.
        "     de la directora; boton Imprimir (o guardar como PDF).\n" + // Impresión.
        "   - Exportar Excel: expediente .xlsx con logos y firma incrustados.\n" + // Excel oficial.
        "   - Configuracion del Sistema: nombre y cargo de la directora editables\n" + // Configuración viva.
        "     sin reinstalar (se guardan en datos/configuracion.properties).\n\n" + // Archivo de configuración.
        "4. ADMINISTRACION\n" + // Encabezado de administración.
        "   - Gestion de Administradores: crear, editar, desactivar cuentas y\n" + // CRUD de cuentas.
        "     cambiar contrasena; nadie puede desactivar su propia cuenta.\n" + // Regla de seguridad.
        "   - Visor de Auditoria: historial de solo lectura de quien hizo que y\n" + // Auditoría.
        "     cuando, con busqueda por accion, detalle o responsable.\n" + // Búsqueda de auditoría.
        "   - Respaldo de Informacion: copia general anual o semestral con peso\n" + // Respaldo con periodo.
        "     informado; Restaurar valida el respaldo (sisas.db) y resguarda lo\n" + // Restauración segura.
        "     actual en datos_respaldo_previo antes de aplicar.\n\n" + // Resguardo automático.
        "5. AYUDA\n" + // Encabezado de ayuda.
        "   - Ayuda: este manual. Acerca de: version e imagen institucional.\n\n" + // Opciones del menú.
        "6. SEGURIDAD Y DATOS\n" + // Encabezado de seguridad.
        "   - Toda la informacion vive en la PC: carpeta datos/ (base de datos,\n" + // Almacenamiento local.
        "     fotos, QR, reportes y configuracion).\n" + // Contenido de datos/.
        "   - Cada accion queda firmada en auditoria con el administrador\n" + // Firma de responsabilidad.
        "     responsable.\n" + // Cierre de la firma.
        "   - Procedimiento de actualizacion del sistema: 1) Respaldo, 2) Cerrar,\n" + // Pasos de actualización.
        "     3) Copiar el sistema nuevo conservando datos/, 4) Abrir (las\n" + // Reemplazo seguro.
        "     migraciones de base de datos corren solas al arrancar).\n"; // Migraciones automáticas.

    // Constructor: owner = ventana principal.
    public DialogoAyuda(JFrame owner) { // Recibe al padre.
        super(owner, true); // true = modal.
        setTitle("Ayuda SISAS"); // Título del panel.
        setSize(560, 480); // Tamaño ampliado para el manual completo.
        setLocationRelativeTo(owner); // Centra sobre el padre.

        JTextArea area = new JTextArea(TEXTO); // Área con el manual actualizado.
        area.setEditable(false); // Solo lectura: el usuario no puede alterarlo.
        area.setLineWrap(true); // Ajuste de línea automático.
        area.setWrapStyleWord(true); // Corta por palabras, no por letras.
        area.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13)); // Fuente clara tipo manual.

        add(new JScrollPane(area), BorderLayout.CENTER); // Área con scroll al centro.
    } // Fin del constructor.
} // Fin de la clase DialogoAyuda.



