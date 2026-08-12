# Guía de aprendizaje — Inventario de PC y periféricos (IPS The Wala)

> **🏆 GUÍA COMPLETADA el 12-ago-2026.** Los 10 pasos terminados: app Spring Boot con
> CRUD de equipos, periféricos relacionados (@ManyToOne), validaciones, buscador general
> (@Query JPQL), Bootstrap y login (Spring Security). Detalle de gotchas aprendidos en
> los commits de git y en la memoria de Claude.

Vas a construir una página web en **Java** para registrar los computadores y periféricos
de la IPS, y en el camino vas a aprender las bases del desarrollo web con Java.

**Stack elegido (y por qué):**

| Pieza | Qué es | Por qué la elegimos |
|---|---|---|
| Java 21 | El lenguaje | Ya lo tienes instalado, versión moderna LTS |
| Spring Boot | Framework que hace el 90% del trabajo pesado web | Es EL estándar en empresas colombianas |
| Maven | Gestor del proyecto: descarga librerías y compila | Viene incluido en IntelliJ |
| Thymeleaf | Plantillas: HTML con datos de Java adentro | Todo en un solo proyecto, ideal para aprender |
| PostgreSQL | Base de datos | Ya lo usas en el Reporteador |

---

## Conceptos clave antes de empezar (Paso 0)

Cuando alguien abre la página, esto es lo que pasa:

```
Navegador ──petición──▶ Controlador (Java) ──▶ Servicio/Repositorio ──▶ PostgreSQL
   ▲                                                                        │
   └────────── HTML generado por Thymeleaf ◀── datos (List<Equipo>) ◀───────┘
```

- **Controlador**: clase Java que responde a URLs (`/equipos`, `/equipos/nuevo`).
- **Entidad**: clase Java que representa una tabla de la base de datos (ej. `Equipo`).
- **Repositorio**: interfaz que hace los SELECT/INSERT/UPDATE por ti (Spring Data JPA).
- **Plantilla Thymeleaf**: archivo HTML en `src/main/resources/templates/` donde
  insertas los datos con atributos como `th:text` y `th:each`.

**Estructura del proyecto** (la convención de Maven, no se inventa, se respeta):

```
inventario-thewala/
├── pom.xml                        ← receta del proyecto: librerías y versiones
└── src/main/
    ├── java/com/thewala/inventario/
    │   └── InventarioApplication.java   ← el main() que arranca todo
    └── resources/
        ├── application.properties       ← configuración (puerto, base de datos)
        ├── static/                      ← archivos que se sirven tal cual (css, imágenes)
        └── templates/                   ← plantillas Thymeleaf (HTML dinámico)
```

**¿Qué es cada cosa que ves en IntelliJ?** (panel Project, de arriba a abajo):

| Elemento | Qué es | ¿Lo tocas? |
|---|---|---|
| `.idea` | Carpeta privada de IntelliJ (sus preferencias) | ❌ Nunca; si se borra, se regenera |
| `src` | ⭐ **TU código**: Java en `main/java`, config y HTML en `main/resources` | ✅ Aquí vives |
| `target` | Resultado de compilar (los `.class`). Desechable, se regenera | ❌ Nunca editar |
| `.gitignore` | Lista de lo que git NO debe versionar (`target/`, `.idea/`) | Casi nunca |
| `GUIA_APRENDIZAJE.md` | Esta guía. Documentación, no afecta al programa | 📖 Leer |
| `pom.xml` | ⭐ La "receta": identidad del proyecto + librerías (`<dependency>`) | ✅ Al agregar librerías |
| External Libraries | Vista de las librerías que Maven descargó a `C:\Users\thewala\.m2\` | 📖 Solo mirar |
| Scratches and Consoles | Borradores sueltos de IntelliJ | Ignorar por ahora |

Regla mental: *¿necesito una librería nueva? → `pom.xml`. ¿voy a programar? → `src`.*

**Configuración clave — el puerto.** En `application.properties` está la línea:

```properties
server.port=9090
```

Ese archivo es el centro de configuración de toda app Spring Boot (puerto, base de datos,
todo). Usamos 9090 porque el 8080 (el default de Spring) y el 8090 ya estaban ocupados
en esta máquina. Si algún día ves `Port XXXX was already in use`: otro programa usa ese
puerto → cambia esta línea y reinicia.

---

## Paso 1 — Abrir y ejecutar el proyecto ✅ (completado el 06-ago-2026)

1. Abre **IntelliJ IDEA** → `File → Open` → selecciona la carpeta
   `C:\Users\thewala\inventario-thewala` (la carpeta, no un archivo).
2. IntelliJ detecta el `pom.xml` y empieza a **descargar las librerías** (barra de
   progreso abajo a la derecha). La primera vez tarda unos minutos — está bajando
   Spring Boot y sus dependencias a `C:\Users\thewala\.m2\` (el "almacén" de Maven).
3. Abre `InventarioApplication.java` y dale al **triángulo verde** junto a `main`.
4. En la consola verás arrancar Spring Boot; la línea clave es:
   `Tomcat started on port 9090`.
5. Abre el navegador en **http://localhost:9090** → debes ver la tarjeta de bienvenida.

**Qué acabas de aprender:** un proyecto Spring Boot es un programa Java normal con un
`main()`, que levanta un servidor web embebido (Tomcat). No hay que instalar ningún
servidor aparte.

> Para detener la app: cuadrado rojo en IntelliJ. Para reiniciarla: el triángulo otra vez.

**Errores que ya vivimos (y su lección):**

- **Navegador dice "ERR_CONNECTION_REFUSED / la página rechazó la conexión"** → la app
  NO está corriendo. La aplicación solo existe mientras la consola de IntelliJ esté viva
  (cuadrado rojo encendido). Primera pregunta siempre: *¿está corriendo mi app?*
- **Consola dice `Port 9090 was already in use`** → otro programa usa el puerto;
  cambiar `server.port` en `application.properties` y reiniciar.
- **El proceso arranca y muere solo (`Process finished with exit code 1`)** → hubo un
  error al iniciar; buscar en la consola la primera línea que diga `Caused by:` — ahí
  está la causa real.

---

## Paso 2 — Tu primer controlador (lo escribes TÚ)

Vas a crear la página `/hola` escribiendo tu primera clase.

1. En IntelliJ, clic derecho sobre el paquete `com.thewala.inventario` →
   `New → Java Class` → nombre: `HolaControlador`.
2. Escribe esto (escríbelo, no lo pegues — así se aprende):

```java
package com.thewala.inventario;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller                          // le dice a Spring: "esta clase responde peticiones web"
public class HolaControlador {

    @GetMapping("/hola")             // cuando alguien visite /hola, ejecuta este método
    public String saludar(Model model) {
        model.addAttribute("nombre", "IPS The Wala");   // dato que viaja al HTML
        return "hola";               // busca la plantilla templates/hola.html
    }
}
```

3. Crea la carpeta `templates` dentro de `src/main/resources` (clic derecho →
   `New → Directory`) y adentro el archivo `hola.html`:

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head><meta charset="UTF-8"><title>Hola</title></head>
<body>
    <!-- th:text reemplaza el contenido con el dato que mandó el controlador -->
    <h1 th:text="'Hola, ' + ${nombre}">texto de relleno</h1>
</body>
</html>
```

4. Reinicia la app (cuadrado rojo → triángulo verde) y visita **http://localhost:9090/hola**.

**Trucos de IntelliJ mientras escribes:**

- Si `@Controller` (o cualquier cosa) queda subrayado en rojo: cursor encima →
  **Alt+Enter → Import class** (elige el de `org.springframework...`).
- Si al probar da **error 404**: o la carpeta no se llama exactamente `templates`,
  o el `return "hola"` no coincide con el nombre del archivo `hola.html`.

**Qué aprendes aquí:** el ciclo completo petición → controlador → modelo → plantilla.
Todo lo demás del proyecto es este mismo ciclo repetido con más datos.

---

## Paso 3 — Base de datos y la entidad `Equipo`

*(Este lo hacemos juntos cuando termines el Paso 2 — aquí va el resumen.)*

1. Crear la base de datos en tu PostgreSQL local: `CREATE DATABASE inventario_thewala;`
2. Agregar al `pom.xml` dos dependencias: `spring-boot-starter-data-jpa` y el driver
   `postgresql` (te enseño dónde van y qué hace cada una).
3. Descomentar y completar las líneas de `spring.datasource.*` en
   `application.properties`.
4. Escribir la entidad `Equipo`:

```
Equipo
├── id           (Long, autogenerado)
├── placa        (String, ej: "PC-IBAGUE-001" — única)
├── tipo         (ESCRITORIO | PORTATIL | TODO_EN_UNO | SERVIDOR)
├── marca, modelo, serial
├── procesador, ramGb, discoGb
├── sede         (Ibagué, Chaparral, Ortega, Natagaima, San Antonio...)
├── responsable  (quién lo tiene asignado)
├── estado       (ACTIVO | MANTENIMIENTO | DADO_DE_BAJA)
└── fechaCompra
```

Con `spring.jpa.hibernate.ddl-auto=update`, **JPA crea la tabla sola** a partir de la
clase — verás en DataGrip cómo aparece.

## Paso 4 — Listar equipos (tabla HTML con `th:each`)
## Paso 5 — Formulario para registrar un equipo (`th:object`, POST)
## Paso 6 — Editar y eliminar (rutas con variable: `/equipos/{id}/editar`)
## Paso 7 — Periféricos (relación @ManyToOne: un equipo tiene muchos periféricos)
## Paso 8 — Validaciones (@NotBlank, placa única) y buscador por sede/estado
## Paso 9 — Estilos con Bootstrap para que se vea profesional
## Paso 10 — Extras: login sencillo (Spring Security), exportar a Excel, hoja de vida del equipo

---

## Reglas del juego

- Un paso a la vez; no avances si el anterior no corre.
- Los errores en la consola de IntelliJ son tus amigos: lee la **primera** línea que
  diga `Caused by:` — casi siempre ahí está la causa real.
- Cuando termines un paso, me cuentas y hacemos el siguiente juntos, con la
  explicación línea por línea.
