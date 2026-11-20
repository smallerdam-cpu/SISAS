# AGENTS.md — Nota para la IA que trabaje en SISAS

Lee este archivo ANTES de proponer o aplicar cualquier cambio. Es el contexto
del proyecto y las reglas de trabajo. El README.md es para humanos; este
archivo es para la IA.

## Proyecto
- *SISAS* — Sistema Integral de Seguimiento de Asistencia de Servicio Social, SEQ Quintana Roo.
- Repo: smallerdam-cpu/SISAS. Java 17, Swing, SQLite, Maven, Apache POI, ZXing, BCrypt, jpackage.
- Paquete base: com.seq.sisas (src/main/java/com/seq/sisas): app, db, modelo, repositorio, seguridad, servicio, ui, utilidad.
- Base de datos: datos/sisas.db (ruta relativa; la app que corre desde VS Code usa C:\Users\smith\clones\SISAS\datos\sisas.db).
- Dueño: Roberto Smith Hernandez Ramirez. Trabaja en VS Code en su clon local. La IA NO modifica el repo directamente: el dueño aplica cada paso en VS Code y sube con git.

## Cómo correr y verificar
- Verdad de tierra: mvn clean compile (BUILD SUCCESS). Los marcadores amarillos/rojos de VS Code NO son la verdad.
- Correr: F5 en VS Code (Main). Cerrar la app por completo antes de recompilar.
- Verificar cada paso en GitHub (commits) antes de avanzar al siguiente.

## Reglas de trabajo (obligatorias)
1. Un solo paso por respuesta. Si hay un bloqueo, resolverlo antes del siguiente paso.
2. Explicar QUÉ se hará y POR QUÉ antes de enviar código.
3. Archivos completos o bloques completos; nunca shorthand ("+ getters/setters igual que los demás").
4. Cada creación o modificación indica nombre del archivo y RUTA COMPLETA.
5. Solución más simple y directa, sin sobreingeniería.
6. Git obligatorio en cada paso: git add + git commit + git push con mensaje claro.
7. *Cada modificación actualiza el README.md*: agregar entrada fechada en la sección ## Actualizaciones describiendo el cambio. Sin entrada en README, el paso no está cerrado.
8. Formato de respuesta: [PASO n] / [CÓDIGO] / [GIT] / [MEMORIA] / [SIGUIENTE].
9. Respuestas concisas, directas, sin relleno. Español.
10. No borrar datos de la base (alumnos, instituciones, asistencias) sin orden explícita del dueño. El folio 1 es el propio dueño (Roberto Smith Hernandez Ramirez): nunca borrarlo.

## Estado actual (2026-11-20)
- Feature 1 COMPLETA (2026-11-12/13): registro prepa/universidad — Nivel PREPA/UNIVERSIDAD, Escuela como lista filtrada por nivel, Tipo solo para UNIVERSIDAD, matrícula, especialidad, fecha fin; reporte con los campos nuevos; se quitó Exportar Excel (solo Vista Previa para imprimir y guardar PDF).
- Feature 2 COMPLETA (2026-11-17/18): Gestión de Prepas y Universidades permite EDITAR instituciones con CCT, dirección, CP, director o coordinador, teléfono y correo (solo en agregar/editar, NO en el reporte). Migración V9 agrega las 6 columnas a instituciones sin borrar datos. Instituciones: Cecyte 4 (PREPA) y Grupo Tecnologico Cancun (UNIVERSIDAD).
- BUG V9 RESUELTO (2026-11-20): "no such column: 'clave_centro_trabajo'" al abrir Gestión. Causa real: los SELECTs de InstitucionDAO (obtenerTodas, obtenerPorNivel, obtenerPorId) solo pedían 4 columnas, pero mapear() lee 10. La tabla SÍ tenía las 10 columnas. Fix: los 3 SELECTs ya piden las 6 columnas de contacto. Lección: verificar el texto real del SQL, no solo conteos.
- Otros fixes 2026-11-20: DialogoEstudiante inicializa listaEscuelas (evita NullPointerException al dar Editar si falla la carga); PanelHojaReporte sin menciones al anexo Excel.
- Diagnóstico: InicializadorSistema imprime en terminal las columnas reales de instituciones y la confirmación de V9 (println de diagnóstico, se pueden quitar cuando ya no hagan falta).
- Respaldo: NO hay restauración automática al arrancar. Restaurar es manual (botón "Restaurar Respaldo" en DialogoRespaldo) y mueve datos/ actual a datos_respaldo_previo/. No borrar respaldos sin preguntar.

## Archivos clave
- src/main/java/com/seq/sisas/db/InicializadorSistema.java — migraciones V8 (tabla instituciones + 6 campos en estudiantes) y V9 (6 columnas de contacto en instituciones).
- src/main/java/com/seq/sisas/repositorio/InstitucionDAO.java — insertar (8 col), actualizar, obtenerTodas/obtenerPorNivel/obtenerPorId (10 col), mapear (10 campos).
- src/main/java/com/seq/sisas/repositorio/EstudianteDAO.java — insertar 16 col, actualizar con executeUpdate.
- src/main/java/com/seq/sisas/servicio/ServicioInstitucion.java — crear/editar con auditoría.
- src/main/java/com/seq/sisas/ui/VentanaGestionInstituciones.java — tabla + Nueva/Editar Institución (formulario 8 campos).
- src/main/java/com/seq/sisas/ui/DialogoEstudiante.java — Nivel/Escuela/Tipo, matrícula, especialidad, fecha fin, foto.
- src/main/java/com/seq/sisas/db/ConexionDB.java — datos/sisas.db, ruta relativa.