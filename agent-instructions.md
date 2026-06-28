# 📋 Instrucciones para el Agente de Desarrollo

## Reglas Generales

### No inventar cosas
- **Nunca** generes código, funciones, rutas o componentes que no hayan sido pedidos explícitamente en la tarea.
- Si algo no está claro, **pregunta antes de asumir**.
- No añadas librerías externas sin que se indique en la tarea. Usa siempre las que ya están instaladas en el proyecto.
- No generes datos de prueba inventados. Si hacen falta datos de ejemplo, usa nombres simples y obvios como `usuario1`, `habito-ejemplo`, etc.

### Código simple y legible (nivel junior)
- Escribe el código **más sencillo posible** que cumpla el requisito. No optimices antes de tiempo.
- Prefiere claridad sobre elegancia. Si hay dos formas de hacer algo, elige la más fácil de leer.
- Evita patrones avanzados como decoradores complejos, rxjs con muchos operadores encadenados, o generics complicados. Úsalos solo si es estrictamente necesario.
- Divide el código en **funciones pequeñas** con nombres descriptivos en español (mantenlo consistente en todo el proyecto).
- Una función = una responsabilidad. Si una función hace más de una cosa, divídela.

### Comentarios obligatorios
- **Comenta cada función** explicando qué hace, qué recibe y qué devuelve.
- **Comenta cada clase** explicando su propósito.
- **Comenta bloques de lógica** que no sean obvios a primera vista.
- Los comentarios deben estar en **español**, que es el idioma del proyecto.
- Ejemplo de comentario correcto:
  ```typescript
  /**
   * Obtiene la lista de hábitos del usuario actual.
   * @param userId - El id del usuario logueado
   * @returns Un array de objetos Habit
   */
  getHabitsByUser(userId: number): Observable<Habit[]> { ... }
  ```

---

## Reglas por Capa Técnica

### Angular (Frontend)
- Usa **Angular standalone components** (sin NgModule).
- Para peticiones HTTP usa `HttpClient` inyectado con `inject()`.
- Para el estado local del componente usa variables simples o señales (`signal()`), nada de NgRx hasta que se indique.
- Los servicios van en la carpeta `src/app/services/`.
- Los componentes van en `src/app/components/` o en una subcarpeta con el nombre del módulo funcional.
- Los modelos (interfaces) van en `src/app/models/`.
- Sigue esta estructura de carpetas:
  ```
  src/
  └── app/
      ├── components/
      │   ├── habits/
      │   ├── stats/
      │   ├── chat/
      │   └── shared/
      ├── services/
      ├── models/
      └── pages/
  ```
- Cada componente tiene su propio archivo `.ts`, `.html` y `.scss`.
- No pongas lógica de negocio en los `.html`. La lógica va siempre en el `.ts`.

### Java (Backend — Java puro con Servlets y JDBC)
- Usa **Java 17** sin frameworks. Solo la librería estándar de Java + Servlets + JDBC.
- El servidor web es **Apache Tomcat 10** (desplegando un archivo `.war` o usando Tomcat embebido vía Maven).
- Sigue la arquitectura en capas: `Servlet → Service → DAO`.
  - El **Servlet** recibe la petición HTTP y devuelve la respuesta JSON. No tiene lógica de negocio.
  - El **Service** contiene toda la lógica de negocio.
  - El **DAO** (Data Access Object) tiene todas las queries SQL usando JDBC. No tiene lógica de negocio.
- Para leer y escribir JSON usa la librería **Gson** (es sencilla y suficiente).
- Usa POJOs (clases simples sin anotaciones) para representar los modelos y los DTOs.
- Gestiona los errores con bloques `try/catch` claros. Devuelve siempre un JSON con el error al frontend.
- Estructura de paquetes:
  ```
  com.habittracker/
  ├── servlet/     ← recibe peticiones HTTP (equivalente al Controller)
  ├── service/     ← lógica de negocio
  ├── dao/         ← queries SQL con JDBC
  ├── model/       ← clases POJO que representan las tablas
  ├── dto/         ← objetos de entrada/salida de los servlets
  └── util/        ← utilidades: conexión a BD, helpers de JSON, etc.
  ```
- La conexión a la base de datos se gestiona en una clase `DatabaseConnection.java` en el paquete `util`.
- Todos los Servlets extienden `HttpServlet` y sobreescriben `doGet`, `doPost`, `doPut` o `doDelete` según lo que necesiten.
- Activa CORS en un filtro `CorsFilter.java` para que Angular pueda llamar al backend.

### PostgreSQL (Base de Datos)
- Los nombres de tablas y columnas en **snake_case** y en inglés.
- Toda tabla tiene una columna `id` de tipo `SERIAL PRIMARY KEY`.
- Incluye siempre `created_at TIMESTAMP DEFAULT NOW()` en las tablas principales.
- Los scripts SQL van en `src/main/resources/db/migration/` usando Flyway (si se indica) o en archivos `.sql` nombrados claramente.
- Documenta el esquema con comentarios en el propio SQL:
  ```sql
  -- Tabla que almacena los hábitos creados por cada usuario
  CREATE TABLE habits (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    name VARCHAR(100) NOT NULL,
    ...
  );
  ```

---

## Flujo de Trabajo por Tarea

1. **Lee la tarea completa** antes de empezar a escribir código.
2. **Identifica qué archivos** hay que crear o modificar.
3. **Crea o modifica solo esos archivos**. No toques nada más.
4. **Añade los comentarios** antes de entregar el código.
5. **Verifica** que el código compila o tiene la sintaxis correcta antes de mostrarlo.
6. **Responde en español**.

---

## Lo que el Agente NO debe hacer

- ❌ No crear tablas, DAOs o Servlets que no estén en la tarea actual.
- ❌ No refactorizar código de tareas anteriores salvo que se pida.
- ❌ No instalar dependencias nuevas sin avisar y justificar.
- ❌ No usar Spring, Spring Boot, Hibernate ni ningún framework de Java. Solo Java puro, Servlets y JDBC.
- ❌ No generar tests automáticamente salvo que la tarea lo pida.
- ❌ No usar `any` en TypeScript. Siempre tipar correctamente.
- ❌ No hacer commits ni modificar configuraciones de entorno.
