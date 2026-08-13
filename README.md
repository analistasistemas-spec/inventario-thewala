# Inventario de PC y periféricos — IPS The Wala

Aplicación web para llevar el inventario de equipos de cómputo y sus periféricos en una
IPS con varias sedes: registro de equipos, hoja de vida de cada uno, exportación a
Excel y PDF, y acta de entrega lista para firmar.

Proyecto construido paso a paso como ejercicio de aprendizaje de Java web. El recorrido
completo, con los conceptos y los errores que salieron en el camino, está documentado en
[GUIA_APRENDIZAJE.md](GUIA_APRENDIZAJE.md).

## Tecnologías

| Pieza | Uso |
|---|---|
| Java 21 (Temurin) | Lenguaje |
| Spring Boot 3.5.4 | Framework web (MVC, Data JPA, Security, Validation) |
| Thymeleaf | Plantillas HTML del lado del servidor |
| PostgreSQL | Base de datos |
| Hibernate / JPA | Mapeo objeto-relacional (crea las tablas desde las entidades) |
| Bootstrap 5.3 | Estilos |
| Apache POI | Exportación a Excel |
| OpenPDF | Exportación a PDF |
| Maven | Gestión de dependencias y compilación |

## Funcionalidades

- **Tablero de inicio** con totales de equipos y periféricos, y conteos por sede, estado y tipo.
- **Gestión de equipos** (CRUD completo): placa, tipo, marca, modelo, procesador, RAM,
  disco, sede, responsable, estado y fecha de compra.
- **Hoja de vida por equipo** con sus periféricos asociados (mouse, teclado, monitor…),
  que se pueden agregar y quitar.
- **Buscador general** sobre placa, marca, modelo, sede y responsable.
- **Validaciones** de campos obligatorios con mensajes en pantalla.
- **Listas desplegables** para sede, tipo y estado, para mantener los datos consistentes.
- **Exportación a Excel** (con formato: encabezados, filtros automáticos, filas alternas)
  y **a PDF**, respetando el filtro de búsqueda activo.
- **Acta de entrega en PDF** por equipo, con sus periféricos y espacios de firma.
- **Login** de acceso.

## Estructura

```
src/main/
├── java/com/thewala/inventario/
│   ├── InventarioApplication.java   arranque de la aplicación
│   ├── Equipo.java                  entidad principal
│   ├── Periferico.java              entidad relacionada (@ManyToOne a Equipo)
│   ├── EquipoRepositorio.java       consultas de equipos (JpaRepository + @Query)
│   ├── PerifericoRepositorio.java   consultas de periféricos
│   ├── EquipoControlador.java       pantallas y acciones del inventario
│   ├── InicioControlador.java       tablero de inicio
│   ├── ExcelExportador.java         generación del archivo .xlsx
│   └── PdfExportador.java           listado en PDF y acta de entrega
└── resources/
    ├── application.properties       puerto, base de datos y usuario de acceso
    └── templates/                   plantillas Thymeleaf
```

## Cómo ejecutarlo

Requisitos: Java 21 y una base de datos PostgreSQL.

1. Crear la base de datos:

   ```sql
   CREATE DATABASE inventario_thewala;
   ```

2. Ajustar la conexión y el usuario de acceso en
   `src/main/resources/application.properties` (URL, usuario y contraseña de PostgreSQL).

3. Arrancar la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

   O desde el IDE, ejecutando `InventarioApplication`.

4. Abrir <http://localhost:9090> e iniciar sesión con el usuario configurado.

Las tablas se crean solas en el primer arranque (`spring.jpa.hibernate.ddl-auto=update`).
