# SISAS

**SISAS** = **Sistema Integral de Seguimiento de Asistencia de Servicio Social**.

Sistema de escritorio desarrollado para el Departamento de Educación Media Superior y
Preparatoria Abierta de la Secretaría de Educación de Quintana Roo (SEQ).
Controla el registro de asistencia, horas acumuladas y expedientes oficiales de los
estudiantes de servicio social, con auditoría y seguridad de nivel institucional.

## ¿Qué hace SISAS?

- **Acceso seguro**: login de administradores con contraseña cifrada (BCrypt).
- **Gestión de estudiantes**: registro con CURP y fecha de nacimiento obligatorias,
  edad en vivo, fotografía opcional, código QR único y **folio oficial secuencial
  que jamás se reutiliza**.
- **Kiosco de asistencia**: el alumno registra ENTRADA/SALIDA con su código o lector
  QR USB; muestra ficha con fotografía que se oculta a los 5 segundos por privacidad;
  la pantalla queda bloqueada y solo sale con contraseña del administrador.
- **Ajustes de horas**: agrega o quita horas (pagos dobles/triples o descuentos) con
  motivo y contraseña; queda firmado en auditoría y reflejado en reportes.
- **Reportes oficiales**: vista previa e impresión de hoja con logos SEQ/SEP y firma
  de la dirección (discreta: los ajustes solo se detallan en el anexo Excel), y
  exportación del expediente a **Excel con logos y firma incrustados**.
- **Administración**: creación y control de administradores, visor de auditoría de
  solo lectura, respaldo general (anual/semestral) con peso informado y restauración
  con resguardo automático.

## Compatibilidad

- **Sistemas operativos**: Windows 10 y Windows 11 (64 bits).
- **Instalador autónomo**: incluye Java embebido; la PC de oficina NO necesita
  instalar Java ni nada más.
- **Hardware**: compatible con lectores de QR USB (modo teclado) y cualquier
  impresora de Windows (incluye "Imprimir en PDF").
- **Archivos que genera/lee**: `.xlsx` (Excel), `.png`/`.jpg` (QR, fotos, logos),
  `.properties` (configuración editable sin reinstalar).
- **Sin internet**: toda la información vive localmente en la carpeta `datos/`.

## Historia del desarrollo

SISAS nació en 2026 como proyecto ante la necesidad real del Departamento
de sustituir el control manual de asistencia de estudiantes de servicio social.
Se construyó con la metodología de **versiones incrementales validadas una por una**:

1. Login seguro, base de datos SQLite y auditoría con responsable.
2. Estudiantes con código QR y folios oficiales.
3. Kiosco de asistencia con cálculo automático de horas.
4. Reportes y expedientes en Excel.
5. Fotografía del estudiante y ficha visual en el kiosco.
6. Búsquedas por nombre/apellidos/CURP.
7. Modo kiosco bloqueado con contraseña y privacidad de 5 segundos.
8. Eliminación segura con folios no reutilizables.
9. Rediseño de interfaz con medidas fijas independientes de la ventana.
10. Hoja oficial con logos y firma (pantalla, impresión y Excel).
11. Gestión de administradores, auditoría, respaldos y ajustes de horas.
12. Empaquetado final como instalador `.exe` para Windows.

Tecnologías: Java 17 (Swing), SQLite, Apache POI, ZXing, BCrypt y jpackage.

## Creador

Desarrollado por **Roberto Smith Hernandez Ramirez**,
proyecto, Quintana Roo, México, 2026.
