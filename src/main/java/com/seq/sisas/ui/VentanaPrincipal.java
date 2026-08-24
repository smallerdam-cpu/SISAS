package com.seq.sisas.ui; // Paquete de interfaz de usuario.

import com.seq.sisas.modelo.Administrador; // Entidad del admin logueado.
import java.awt.BorderLayout; // Diseño principal.
import java.awt.event.ActionEvent; // Evento de clic.
import java.awt.event.ActionListener; // Escuchador de clics.
import javax.swing.JFrame; // Ventana.
import javax.swing.JLabel; // Etiqueta.
import javax.swing.JMenu; // Menú desplegable de la barra.
import javax.swing.JMenuBar; // Barra de menú superior.
import javax.swing.JMenuItem; // Opción dentro de un menú.

/**
 * VentanaPrincipal (V12): barra de menú ordenada estilo SIOSAD, sin botones flotantes.
 * CONEXIONES:
 * - Recibe Administrador de com.seq.sisas.ui.VentanaLogin.
 * - Desde la barra abre: VentanaGestionEstudiantes, kiosco VentanaAsistencia,
 *   VentanaReportes, DialogoAyuda y DialogoAcercaDe.
 */
public class VentanaPrincipal extends JFrame { // Hereda de JFrame.

    //private Administrador adminLogueado; // Admin actual en memoria.

    // Constructor que recibe al administrador logueado.
    public VentanaPrincipal(Administrador adminLogueado) { // Recibe al admin.
        //this.adminLogueado = adminLogueado; // Guarda al admin.

        setTitle("SISAS - Panel Principal"); // Título.
        setSize(800, 450); // Tamaño inicial ordenado y ligero.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Cierra todo al salir.
        setLocationRelativeTo(null); // Centra en pantalla.

        // --- Barra de menú superior: todas las opciones al lado de Ayuda ---
        JMenuBar barraMenu = new JMenuBar(); // Barra estilo SIOSAD.

        JMenu menuGestion = new JMenu("Gestión"); // Menú de gestión.
        JMenuItem itemGestion = new JMenuItem("Gestión de Estudiantes"); // Opción que abre el módulo.
        menuGestion.add(itemGestion); // Agrega la opción al menú.

        JMenu menuAsistencia = new JMenu("Asistencia"); // Menú de asistencia.
        JMenuItem itemAsistencia = new JMenuItem("Registro de Asistencia (Kiosco)"); // Opción que abre el kiosco.
        menuAsistencia.add(itemAsistencia); // Agrega la opción al menú.

        JMenu menuReportes = new JMenu("Reportes"); // Menú de reportes.
        JMenuItem itemReportes = new JMenuItem("Reportes y Excel"); // Opción que abre reportes.
        menuReportes.add(itemReportes); // Agrega la opción al menú.

        JMenuItem itemConfig = new JMenuItem("Configuración de Firma y Cargo"); // Opción para editar directora sin reinstalar.
        menuReportes.add(itemConfig); // Agrega la opción al menú Reportes.

        JMenu menuAyuda = new JMenu("Ayuda"); // Menú de ayuda.
        JMenuItem itemAyuda = new JMenuItem("Ayuda"); // Opción manual en pantalla.
        JMenuItem itemAcerca = new JMenuItem("Acerca de..."); // Opción cuadro institucional.
        menuAyuda.add(itemAyuda); // Agrega manual.
        menuAyuda.add(itemAcerca); // Agrega acerca de.

        JMenu menuSalir = new JMenu("Salir"); // Menú de salida.
        JMenuItem itemCerrar = new JMenuItem("Cerrar Sesión"); // Opción que cierra la sesión.
        menuSalir.add(itemCerrar); // Agrega la opción al menú.

        JMenu menuAdmin = new JMenu("Administración"); // Menú de administración de cuentas.
        JMenuItem itemAdmins = new JMenuItem("Gestión de Administradores"); // Opción que abre el módulo.

        JMenuItem itemAuditoria = new JMenuItem("Visor de Auditoría"); // Opción que abre el historial.
        menuAdmin.add(itemAuditoria); // Agrega la opción al menú Administración.

        JMenuItem itemRespaldo = new JMenuItem("Respaldo de Información"); // Opción que abre el módulo de respaldos.
        menuAdmin.add(itemRespaldo); // Agrega la opción al menú Administración.
        
        menuAdmin.add(itemAdmins); // Agrega la opción al menú.
        barraMenu.add(menuAdmin); // Coloca Administración antes de Ayuda.
        
        barraMenu.add(menuGestion); // Coloca Gestión primero.
        barraMenu.add(menuAsistencia); // Coloca Asistencia al lado.
        barraMenu.add(menuReportes); // Coloca Reportes al lado.
        barraMenu.add(menuAyuda); // Coloca Ayuda al lado.
        barraMenu.add(menuSalir); // Coloca Salir al final.
        setJMenuBar(barraMenu); // Instala la barra en la ventana.

        // --- Acciones de cada opción (abren el módulo directamente) ---
        itemGestion.addActionListener(new ActionListener() { // Escucha clic en Gestión de Estudiantes.
            @Override
            public void actionPerformed(ActionEvent e) { new VentanaGestionEstudiantes(adminLogueado).setVisible(true); } // Abre el módulo de alumnos.
        }); // Fin del listener.

        itemAsistencia.addActionListener(new ActionListener() { // Escucha clic en Registro de Asistencia.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                setVisible(false); // Oculta el panel: el alumno no ve otras opciones.
                new VentanaAsistencia(adminLogueado, VentanaPrincipal.this).setVisible(true); // Abre el kiosco seguro.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        itemReportes.addActionListener(new ActionListener() { // Escucha clic en Reportes y Excel.
            @Override
            public void actionPerformed(ActionEvent e) { new VentanaReportes(adminLogueado).setVisible(true); } // Abre el módulo de reportes.
        }); // Fin del listener.

        itemConfig.addActionListener(new ActionListener() { // Escucha clic en configuración.
            @Override
            public void actionPerformed(ActionEvent e) { new DialogoConfiguracion(VentanaPrincipal.this).setVisible(true); } // Abre el diálogo de configuración.
        }); // Fin del listener.

        itemAdmins.addActionListener(new ActionListener() { // Escucha clic en Gestión de Administradores.
            @Override
            public void actionPerformed(ActionEvent e) { new VentanaGestionAdministradores(adminLogueado).setVisible(true); } // Abre el módulo de cuentas.
        }); // Fin del listener.

        itemAuditoria.addActionListener(new ActionListener() { // Escucha clic en Visor de Auditoría.
            @Override
            public void actionPerformed(ActionEvent e) { new VentanaAuditoria().setVisible(true); } // Abre el historial de solo lectura.
        }); // Fin del listener.

        itemRespaldo.addActionListener(new ActionListener() { // Escucha clic en Respaldo de Información.
            @Override
            public void actionPerformed(ActionEvent e) { new DialogoRespaldo(VentanaPrincipal.this).setVisible(true); } // Abre el diálogo de respaldo.
        }); // Fin del listener.

        itemAyuda.addActionListener(new ActionListener() { // Escucha clic en Ayuda.
            @Override
            public void actionPerformed(ActionEvent e) { new DialogoAyuda(VentanaPrincipal.this).setVisible(true); } // Abre el manual.
        }); // Fin del listener.

        itemAcerca.addActionListener(new ActionListener() { // Escucha clic en Acerca de.
            @Override
            public void actionPerformed(ActionEvent e) { new DialogoAcercaDe(VentanaPrincipal.this).setVisible(true); } // Abre el cuadro institucional.
        }); // Fin del listener.

        itemCerrar.addActionListener(new ActionListener() { // Escucha clic en Cerrar Sesión.
            @Override
            public void actionPerformed(ActionEvent e) { // Al hacer clic...
                dispose(); // Cierra el panel.
                new VentanaLogin().setVisible(true); // Vuelve al login.
            } // Fin de actionPerformed.
        }); // Fin del listener.

        // --- Zona central limpia: solo el saludo ---
        JLabel lblBienvenida = new JLabel("Bienvenido, " + adminLogueado.getNombreCompleto(), JLabel.CENTER); // Saludo personalizado.
        lblBienvenida.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20)); // Fuente destacada.
        add(lblBienvenida, BorderLayout.NORTH); // Saludo arriba; el resto de la ventana queda despejada.
    } // Fin del constructor.
} // Fin de la clase VentanaPrincipal.