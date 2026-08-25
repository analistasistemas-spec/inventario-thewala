# Guía de aprendizaje — Inventario de PC y periféricos (IPS The Wala)

> **🏆 GUÍA BASE COMPLETADA el 12-ago-2026** (10 pasos): app Spring Boot con CRUD de
> equipos, periféricos relacionados (@ManyToOne), validaciones, buscador general
> (@Query JPQL), Bootstrap y login (Spring Security).
>
> **🚀 FASE 2 COMPLETADA el 13-ago-2026** (ver al final): ficha completa del equipo,
> listas desplegables, exportación a Excel y PDF, dashboard con totales y menú de
> navegación con fragmentos.
>
> **📤 FASE 3 COMPLETADA el 25-ago-2026:** acta de entrega en PDF y proyecto publicado en
> <https://github.com/analistasistemas-spec/inventario-thewala>

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

## Paso 2 — Tu primer controlador ✅ (completado el 10-ago-2026)

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

## Paso 3 — Base de datos y la entidad `Equipo` ✅ (completado el 10-ago-2026)

*Nota de lo vivido: la BD se creó en la instancia local del puerto **5433** (la
"postgresql-x64-17-inventarios", solo accesible desde esta máquina) porque la clave del
postgres del 5432 no se conocía. Y ojo con DataGrip: mirar SIEMPRE a qué conexión
apunta la consola antes de ejecutar SQL — la primera vez la BD se creó por accidente
en el servidor de producción y hubo que borrarla.*

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

## Paso 4 — Listar equipos en la web ✅ (completado el 10-ago-2026)

Aquí se conectó todo: PostgreSQL → Java → HTML. Tres piezas:

**1. El repositorio** (`EquipoRepositorio.java`) — una interface que hereda de
`JpaRepository<Equipo, Long>` (entidad + tipo de su id) y con eso regala `findAll()`,
`save()`, `findById()`, `deleteById()`... Spring escribe el SQL.

**2. El controlador** (`EquipoControlador.java`) — presenta la **inyección de
dependencias**: se declara el repositorio como campo `final` y se pide en el
constructor; Spring lo entrega solo.

```java
@Controller
public class EquipoControlador {
    private final EquipoRepositorio repositorio;

    public EquipoControlador(EquipoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping("/equipos")
    public String listar(Model model) {
        model.addAttribute("equipos", repositorio.findAll());
        return "equipos";
    }
}
```

**3. La plantilla** (`equipos.html`) — la estrella es `th:each`, el "por cada":

```html
<tr th:each="equipo : ${equipos}">
    <td th:text="${equipo.placa}"></td>
    ...
</tr>
```

⚠️ **Tropiezos que vivimos:**
- **`eq` NO sirve como variable del th:each** — es palabra reservada de Thymeleaf (el
  operador "equals", como `gt`, `lt`, `ge`, `le`). Produce el error críptico
  `Iteration variable cannot be null`. Usar nombres largos: `equipo`.
- Todo lo que use `${equipo...}` debe vivir **dentro** de la etiqueta que declara el
  `th:each`; afuera de esa fila la variable no existe (`cannot be found on null`).
- En stack traces largos, la causa real está en el **último `Caused by:`**.

---

## Paso 5 — Formulario para registrar equipos ✅ (completado el 10-ago-2026)

Un formulario son **dos viajes**: GET trae la página con campos vacíos, POST envía lo
escrito. Por eso son dos métodos:

```java
@GetMapping("/equipos/nuevo")
public String mostrarFormulario(Model model) {
    model.addAttribute("equipo", new Equipo());   // molde en blanco
    return "equipo_formulario";
}

@PostMapping("/equipos")
public String guardar(@ModelAttribute Equipo equipo) {
    repositorio.save(equipo);          // el INSERT
    return "redirect:/equipos";        // patrón redirect: guardar y volver al listado
}
```

- **`@ModelAttribute`** — Spring arma el objeto Equipo con lo que el usuario escribió,
  usando los setters (por eso existen los getters/setters).
- En la plantilla: `th:object="${equipo}"` amarra el form al objeto y cada input usa
  `th:field="*{placa}"` (el `*{...}` = "del objeto de este formulario").
- **`redirect:`** en vez de plantilla = patrón POST-redirect-GET: guardas y rediriges,
  así el F5 no reenvía el formulario.

---

## Paso 6 — Editar y eliminar (CRUD completo) ✅ (completado el 12-ago-2026)

**Concepto clave:** el id viaja en la URL (`/equipos/4/editar`) y se captura con
`@PathVariable`. Y `save()` es inteligente: objeto CON id → UPDATE; sin id → INSERT.

```java
@GetMapping("/equipos/{id}/editar")
public String mostrarEdicion(@PathVariable Long id, Model model) {
    Equipo equipo = repositorio.findById(id).orElseThrow();
    model.addAttribute("equipo", equipo);
    return "equipo_formulario";        // ¡reutiliza el formulario del Paso 5!
}

@PostMapping("/equipos/{id}/eliminar")
public String eliminar(@PathVariable Long id) {
    repositorio.deleteById(id);
    return "redirect:/equipos";
}
```

- El formulario compartido lleva `<input type="hidden" th:field="*{id}">` — el id viaja
  escondido: si viene lleno, save() hace UPDATE; vacío, INSERT.
- URLs con variable en Thymeleaf: `@{/equipos/{id}/editar(id=${equipo.id})}`.
- **Regla de oro:** las acciones que CAMBIAN datos van por POST (mini-form con botón),
  nunca como enlace GET — un enlace se puede disparar por accidente.
- Para borrar archivos/clases en IntelliJ: clic derecho → Delete con **Safe delete**
  (busca usos antes de borrar).

---

## Paso 7 — Periféricos: relación entre tablas ✅ (completado el 12-ago-2026)

EL tema de las bases de datos: MUCHOS periféricos pertenecen a UN equipo.

**1. La entidad** (`Periferico.java`): campos id, tipo, marca, serial y la estrella:

```java
@ManyToOne
private Equipo equipo;    // Hibernate lo convierte en la columna equipo_id (FK)
```

**2. El repositorio** (`PerifericoRepositorio.java`) presenta las **derived queries**:
Spring genera el SQL a partir del NOMBRE del método:

```java
List<Periferico> findByEquipoId(Long equipoId);   // = WHERE equipo_id = ?
```

**3. El controlador**: la hoja de vida `/equipos/{id}` (detalle + periféricos + form),
agregar (`periferico.setEquipo(equipo)` ANTES de save — así se amarra), y eliminar
volviendo al equipo dueño. La placa del listado se volvió enlace al detalle.

⚠️ **Tropiezos que vivimos:**
- Faltaron los getters/setters de `Periferico` → `cannot find symbol setEquipo` al
  compilar. Sin getters no funciona ni el binding ni Thymeleaf.
- **GOTCHA grande:** `@ModelAttribute` también rellena el objeto con las variables de
  la URL que coincidan por nombre. La ruta `/equipos/{id}/perifericos` metía el id del
  EQUIPO en `periferico.id` → JPA intentaba UPDATE de una fila inexistente
  (`ObjectOptimisticLockingFailureException`). **Fix:** renombrar la variable de ruta a
  `{equipoId}`. Regla: con @ModelAttribute, las path variables nunca deben llamarse
  como campos de la entidad.

---

## Paso 8 — Validaciones y buscador general ✅ (completado el 12-ago-2026)

**A. Validaciones** — dependencia `spring-boot-starter-validation` + anotar la entidad:

```java
@NotBlank(message = "La placa es obligatoria")
private String placa;
```

Activarlas en el controlador (el `BindingResult` DEBE ir justo después del `@Valid`):

```java
public String guardar(@Valid @ModelAttribute Equipo equipo, BindingResult resultado) {
    if (resultado.hasErrors()) {
        return "equipo_formulario";    // vuelve al form sin guardar
    }
    ...
```

Y mostrar los mensajes bajo cada campo: `<div class="text-danger" th:errors="*{placa}"></div>`.

**B. Buscador general** — cuando la condición es compleja, el nombre-de-método se vuelve
eterno; para eso existe **`@Query` con JPQL** (como SQL, pero sobre CLASES, no tablas):

```java
@Query("""
    SELECT e FROM Equipo e
    WHERE lower(e.placa) LIKE lower(concat('%', :texto, '%'))
       OR lower(e.marca) LIKE lower(concat('%', :texto, '%'))
       OR lower(e.modelo) LIKE lower(concat('%', :texto, '%'))
       OR lower(e.sede) LIKE lower(concat('%', :texto, '%'))
       OR lower(e.responsable) LIKE lower(concat('%', :texto, '%'))
    """)
List<Equipo> buscar(@Param("texto") String texto);
```

En el controlador, el filtro llega como **`@RequestParam(required = false)`** (parámetro
de consulta: `/equipos?texto=lenovo` — distinto del @PathVariable que es parte de la
ruta). La cajita en la plantilla es un form **GET** cuyo `name="texto"` debe coincidir
con el @RequestParam.

Mapa mental: consulta simple → derived query; varias condiciones → @Query JPQL.

⚠️ **Tropiezos que vivimos:** anotar campos = escribir la anotación SOBRE el campo
existente (no pegar campos nuevos → "variable already defined"); los imports se escriben
`import java.util.List;` exacto (mejor Alt+Enter); y tras tocar el pom.xml SIEMPRE
recargar Maven — editar el pom es escribir el pedido, la recarga es ir a la tienda.

---

## Paso 9 — Estilo profesional con Bootstrap ✅ (completado el 12-ago-2026)

Bootstrap = CSS hecho por profesionales que se usa poniendo **clases**. Una línea en el
`<head>` de cada plantilla:

```html
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css">
```

Las clases que usamos: `container mt-4` (centra y da aire), `table table-striped
table-bordered table-hover` + `<thead class="table-dark">` (tablas), `btn btn-primary /
btn-success / btn-warning / btn-danger / btn-secondary` + `btn-sm` (botones),
`form-label` + `form-control` + `mb-3` (formularios), `text-danger` (mensajes de error).

Requiere internet (el CSS viene de un CDN).

---

## Paso 10 — Login con Spring Security ✅ (completado el 12-ago-2026)

Sorprendentemente fácil en su versión básica:

1. Dependencia `spring-boot-starter-security` (+ recarga de Maven).
2. Con solo existir, protege TODA la app: login automático, usuario `user`, clave
   generada en la consola en cada arranque.
3. Usuario fijo en `application.properties`:

```properties
spring.security.user.name=admin
spring.security.user.password=LaClaveElegida
```

4. Salir: visitar `/logout`.

Nivel siguiente (opcional): varios usuarios con roles guardados en la base de datos —
el mismo mundo que Keycloak en el Reporteador, en miniatura.

---

## FASE 2 — La aplicación de verdad (13-ago-2026)

Terminada la guía base, el inventario se convirtió en una herramienta usable.

### 2.1 — Ficha completa del equipo ✅

Campos nuevos en la entidad: `tipo`, `procesador`, `ramGb` (Integer), `discoGb`
(Integer), `estado` y `fechaCompra` (**LocalDate**, import de `java.time`).

Al reiniciar, la consola mostró `Hibernate: alter table equipo add column ...` —
**`ddl-auto=update` agrega columnas a una tabla existente SIN borrar los datos**. Y
`ramGb` se volvió `ram_gb`: Hibernate traduce camelCase a snake_case solo.

En el formulario aparecen dos tipos de input nuevos: `type="number"` (solo dígitos) y
`type="date"` (calendario del navegador; Spring lo convierte solo a LocalDate).

⚠️ **Tropiezo:** escribir `th:list` en vez de `th:text` → la celda queda vacía **sin dar
error**. Un atributo `th:` mal escrito se ignora en silencio. Cuando algo "no aparece"
pero tampoco falla, revisa la ortografía del atributo.

### 2.2 — Listas desplegables ✅

Cambiar `<input type="text">` por `<select class="form-select" th:field="*{sede}">` con
sus `<option>`. El mismo `th:field` funciona igual y además **marca sola** la opción
guardada al editar.

Por qué importa: con texto libre aparecieron "Ibagué" y "Ibague" en la misma base —
datos inconsistentes que rompen filtros y reportes. Con listas, todos escriben igual.

Bonus: `repositorio.findAll(Sort.by("placa"))` para que el listado no cambie de orden
al editar (import `Sort` de `org.springframework.data.domain`).

### 2.3 — Exportar a Excel ✅ (Apache POI)

Dependencia `org.apache.poi:poi-ooxml:5.4.0` — lleva `<version>` porque no es de Spring
(las de Spring heredan la versión del "padre").

Clase `ExcelExportador`: crea `Workbook` → `Sheet` → filas y celdas. Diseño aplicado:
título combinado (`addMergedRegion`), encabezado verde institucional con letra blanca,
bordes, filas alternas, `createFreezePane(0,2)` (inmovilizar), `setAutoFilter` (las
flechitas de filtro) y fecha con `setDataFormat("dd/MM/yyyy")`.

Colores: `IndexedColors` es una paleta fija de 56 colores; para un color exacto se usa
`XSSFColor` con RGB y un **cast**: `((XSSFCellStyle) estilo).setFillForegroundColor(...)`.

El endpoint es distinto a todos los anteriores — devuelve un **archivo**, no una plantilla:

```java
@GetMapping("/equipos/excel")
public ResponseEntity<byte[]> exportarExcel(@RequestParam(required = false) String texto) {
    ...
    return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=inventario_thewala.xlsx")
            .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            .body(archivo);
}
```

`Content-Disposition: attachment` = "no lo muestres, descárgalo". Y como recibe el mismo
`texto` del buscador, **exporta respetando el filtro activo**.

⚠️ **Tropiezo (concepto de ÁMBITO):** las variables de color se declararon como campos de
la clase y se usaban desde un método `static` → *"non-static variable cannot be
referenced from a static context"*. Una variable declarada dentro de un método existe
solo ahí; una de clase pertenece a cada objeto; un método `static` no puede ver las de
objeto. Solución: declararlas dentro del método.

### 2.4 — Exportar a PDF ✅ (OpenPDF)

Dependencia `com.github.librepdf:openpdf:1.3.30`. Clase `PdfExportador` y endpoint
`/equipos/pdf` **calcado** del de Excel (cambian la clase, el nombre del archivo y el
`contentType`).

Diferencia conceptual: un PDF no tiene filas y columnas como Excel — se construye
agregando **elementos** al documento (`Paragraph`, `PdfPTable`). Se usó A4 horizontal
(`PageSize.A4.rotate()`) por la cantidad de columnas y `setHeaderRows(1)` para que el
encabezado se repita en cada página.

### 2.5 — Dashboard de inicio ✅

Consultas de **agregación** en el repositorio:

```java
@Query("SELECT e.sede, COUNT(e) FROM Equipo e GROUP BY e.sede ORDER BY e.sede")
List<Object[]> contarPorSede();
```

Cuando la consulta no devuelve entidades completas sino columnas sueltas, el resultado
llega como **`List<Object[]>`**: `fila[0]` = sede, `fila[1]` = conteo. En la plantilla se
leen como `${fila[0]}`, y el operador *elvis* `?: 'Sin definir'` cubre los nulos.
`count()` viene gratis de JpaRepository.

⚠️ **Concepto clave que costó un 404:** hubo que borrar `static/index.html` para que la
raíz `/` llegara al controlador. Y las plantillas **no se abren por su nombre de
archivo**:

| Carpeta | Cómo se accede |
|---|---|
| `resources/static` | por nombre de archivo: `/index.html`, `/logo.png` |
| `resources/templates` | SOLO a través de un controlador: `/` → InicioControlador → inicio.html |

### 2.6 — Menú de navegación con fragmentos ✅

`templates/fragmentos.html` con `<nav th:fragment="menu" ...>` y en cada plantilla:

```html
<div th:replace="~{fragmentos :: menu}"></div>
```

El menú se escribe **una vez** y aparece en todas las pantallas; se cambia en un solo
lugar. Es el principio **DRY** (*Don't Repeat Yourself*), uno de los pilares del oficio.

---

## FASE 3 — Documento imprimible y publicación (25-ago-2026)

### 3.1 — Acta de entrega en PDF ✅

Un segundo método `generarActa(Equipo, List<Periferico>)` **dentro de la clase
`PdfExportador` que ya existía**, reutilizando sus colores (`VERDE`, `VERDE_CLARO`) y sus
métodos auxiliares (`agregar`, `textoDe`). Esa es la ganancia de haber separado métodos
pequeños: el segundo documento costó la mitad del trabajo.

El acta lleva: título, fecha, párrafo de compromiso, ficha del equipo en tabla de dos
columnas, tabla de periféricos y dos líneas de firma (ENTREGA / RECIBE). Se descarga
desde la hoja de vida del equipo con el nombre `acta_<placa>.pdf`.

Elementos nuevos de PDF: `setColspan` (combinar celdas, para el "sin periféricos"),
`Rectangle.NO_BORDER` (celdas sin borde, para las firmas) y un `Paragraph` compuesto por
varios `Phrase` con fuentes distintas.

### 3.2 — Publicación en GitHub ✅

El proyecto vive en <https://github.com/analistasistemas-spec/inventario-thewala>.

**Antes de publicar — sacar las claves del código.** En `application.properties`:

```properties
spring.security.user.password=${INVENTARIO_PASSWORD:12345}
spring.datasource.password=${DB_PASSWORD:postgres}
```

Se lee: "usa la variable de entorno `INVENTARIO_PASSWORD`; si no existe, usa este valor
de desarrollo". La app sigue funcionando igual en la máquina local, pero en un servidor
real se define la variable y **la clave nunca vive en el repositorio**. Es el mismo
patrón del `.env` del Reporteador.

**La identidad de los commits.** Cada commit guarda un nombre y un correo que salen de la
configuración de git. En esta máquina la configuración *global* era de otra persona (de
quien instaló el equipo), así que los 25 primeros commits salieron a su nombre.

```bash
git config user.name "Nombre Apellido"      # SIN --global = solo este repositorio
git config user.email "correo@ejemplo.com"
```

Para saber cómo está algo configurado:

| Comando | Qué responde |
|---|---|
| `git config user.email` | con qué identidad se firmarán los próximos commits |
| `git config --show-origin user.email` | de qué archivo sale ese valor (global o del repo) |
| `git shortlog -sne` | quién firma los commits ya hechos |
| `git remote -v` | a qué cuenta y repositorio de GitHub apunta el proyecto |

**Reescribir la autoría del historial.** El autor de un commit no se edita: se reescribe
el historial creando commits nuevos con el mismo contenido. Solo es seguro cuando el
repositorio es propio y nadie más lo ha clonado — que era el caso. Con red de seguridad:

```bash
git branch respaldo-autoria-original
git filter-branch -f --env-filter '
export GIT_AUTHOR_NAME="Nombre Apellido"
export GIT_AUTHOR_EMAIL="correo@ejemplo.com"
export GIT_COMMITTER_NAME="Nombre Apellido"
export GIT_COMMITTER_EMAIL="correo@ejemplo.com"
' main
```

Conserva fechas y mensajes; solo cambia el autor. La rama de respaldo sigue apuntando a
los commits viejos por si hay que devolverse.

**Publicar.** Lo más simple resultó crear el repositorio vacío desde la web
(github.com/new, **sin** marcar README/gitignore/license, porque el proyecto ya los
trae) y conectarlo:

```bash
git remote add origin https://USUARIO@github.com/USUARIO/repositorio.git
git config --local credential.useHttpPath true
git push -u origin main
```

El `credential.useHttpPath true` hace que las credenciales se guarden **por repositorio**
y no por servidor: así conviven varias cuentas de GitHub en la misma máquina sin
pisarse. En el push aparece el diálogo de Git Credential Manager → *Sign in with your
browser* → autorizar con la cuenta dueña del repositorio.

⚠️ **Tropiezo:** `gh auth login` usa el *device flow* — muestra un código de 8 caracteres
**en la terminal** (no llega por correo) que se pega en github.com/login/device. Y en su
pregunta *"Authenticate Git with your GitHub credentials?"* conviene responder **No**
cuando ya hay otra cuenta configurada en la máquina, para no reemplazarla.

**El día a día a partir de ahora:**

```bash
git add .
git commit -m "descripcion del cambio"
git push
```

---

## Fase 4 — Caminos abiertos (por hacer)

- **Historial de movimientos**: traslados entre sedes y cambios de responsable, con fecha.
- **Usuarios con roles** (consulta vs administrador) desde la base de datos.
- **Desplegarlo** en un servidor de la IPS para que lo use todo el mundo.
- Detalle pendiente: ponerle las tildes a los textos fijos del acta de entrega.

---

## Reglas del juego

- Un paso a la vez; no avances si el anterior no corre.
- Los errores en la consola de IntelliJ son tus amigos: lee la **primera** línea que
  diga `Caused by:` — casi siempre ahí está la causa real.
- Cuando termines un paso, me cuentas y hacemos el siguiente juntos, con la
  explicación línea por línea.
