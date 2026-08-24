# GUÍA TÉCNICA: PC NUEVA Y ACTUALIZACIONES

Repositorio: https://github.com/smallerdam-cpu/SISAS.git

## A) Configurar una PC nueva para desarrollar (paso a paso)

1. **Java**: instalar JDK 17 (Eclipse Adoptium). Verificar en CMD: `java -version`.
2. **VS Code**: instalar y agregar las extensiones:
   - "Extension Pack for Java"
   - "Maven for Java"
3. **Maven**: descargar el zip binario de Maven 3.9.x, descomprimir en
   `C:\apache-maven-3.9.16`. En VS Code: `Ctrl + ,` → buscar
   `maven.executable.path` → pegar `C:\apache-maven-3.9.16\bin\mvn.cmd`.
4. **Git**: configurar identidad en CMD:
   git config --global user.name "Smith"
   git config --global user.email "tucorreo@ejemplo.com"
5. **Clonar el proyecto**:
   git clone https://github.com/smallerdam-cpu/SISAS.git
6. Abrir la carpeta en VS Code, esperar sincronización Java y ejecutar con **F5**
   (clase Main). El sistema crea su carpeta `datos/` nueva con `admin / admin123`.
7. **Información real**: copiar la carpeta `datos/` de la PC original, o usar el
   módulo Respaldo/Restaurar del sistema.
8. **(Opcional) Instalador**: descomprimir WiX 3.14 en `C:\wix` y en CMD:
   set PATH=%PATH%;C:\wix   (antes de usar jpackage).

## B) Qué se SUBE a Git en una actualización

SI se sube: `src/` (código y recursos), `pom.xml`, `.gitignore`, archivos `.md`.
NUNCA se sube: `datos/`, `target/`, `instalador/`, `datos_respaldo_previo/`
(el `.gitignore` ya lo bloquea automáticamente).

## C) Flujo de una actualización

En la PC de desarrollo:
1. git pull                      (baja lo más reciente antes de tocar nada)
2. Modificar código y probar con F5.
3. C:\apache-maven-3.9.16\bin\mvn.cmd package
4. jpackage (comando del manual) para regenerar el instalador.
5. git add .  →  git commit -m "Version X.Y.Z: descripcion"  →  git push

En la PC de oficina (producción):
1. Abrir SISAS → Administración → Respaldo de Información → Realizar Respaldo.
2. Cerrar el programa.
3. Instalar el nuevo `SISAS-x.y.z.exe` eligiendo `C:\SISAS`
   (conservando la carpeta `datos/` existente).
4. Abrir: las migraciones de base de datos corren solas al arrancar.
