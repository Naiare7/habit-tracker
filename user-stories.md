# 📖 Historias de Usuario — Habit Tracker

> Cada tarea general representa una historia de usuario principal.
> Las subtareas son los pasos técnicos concretos que hay que completar, en orden.
> Mándalas al agente de una en una.

---

## TAREA 1 — Configuración inicial del proyecto

**Historia**: Como desarrolladora, quiero tener el proyecto estructurado y funcionando en local antes de empezar a programar funcionalidades.

### Subtareas

#### 1.1 Crear el proyecto Angular ✅
- [x] Crear un nuevo proyecto Angular con `ng new habit-tracker-frontend`
- [x] Elegir: sin routing (lo añadimos manual), SCSS como preprocesador
- [x] Instalar Angular 17+ con standalone components activado por defecto
- [x] Verificar que la app arranca con `ng serve`

#### 1.2 Instalar dependencias del frontend ✅
- [x] Instalar Chart.js y su wrapper para Angular: `ng2-charts`
- [x] Instalar Lucide Icons: `lucide-angular`
- [x] Añadir las fuentes Nunito y Nunito Sans de Google Fonts en `index.html`
- No instalar nada más por ahora

#### 1.3 Crear la estructura de carpetas del frontend ✅
- [x] Crear las carpetas: `components/habits`, `components/stats`, `components/chat`, `components/shared`, `services`, `models`, `pages`
- [x] Crear un archivo `README.md` dentro de cada carpeta explicando para qué sirve

#### 1.4 Crear el proyecto Java con Maven ✅
- [x] Crear un proyecto Maven estándar con Java 17
- [x] Configurar el `pom.xml` con las dependencias:
  - `javax.servlet-api` (o `jakarta.servlet-api`) para los Servlets
  - `gson` para parsear y generar JSON
  - `postgresql` driver JDBC para conectar con la BD
- [x] El empaquetado del proyecto es `war` para desplegarlo en Tomcat
- [x] Nombre del artefacto: `habit-tracker-backend`
- [x] Verificar que el proyecto compila con `mvn clean package`

#### 1.5 Crear la estructura de paquetes del backend ✅
- [x] Crear los paquetes: `servlet`, `service`, `dao`, `model`, `dto`, `util`
- [x] Crear una clase vacía de ejemplo en cada paquete con un comentario explicando para qué sirve ese paquete
- [x] Crear la clase `DatabaseConnection.java` en el paquete `util` con un método estático `getConnection()` que devuelve una `Connection` JDBC

#### 1.6 Configurar la base de datos PostgreSQL ✅
- [x] Crear la base de datos en PostgreSQL: `habit_tracker_db`
- [x] Crear un usuario de base de datos con contraseña para la app (no usar el usuario root/postgres)
- [x] Guardar la URL, usuario y contraseña en un archivo `db.properties` dentro de `src/main/resources`
- [x] En `DatabaseConnection.java`, leer ese archivo con `Properties` y devolver la conexión
- [x] Verificar que la conexión funciona ejecutando una query simple de prueba
- [x] Instalar Apache Tomcat 10 localmente para desplegar el proyecto


---

## TAREA 2 — Registro e inicio de sesión de usuarios ✅

**Historia**: Como usuaria, quiero poder crear una cuenta y acceder a la aplicación con mi email y contraseña para que mis hábitos sean privados y personales.

### Subtareas

#### 2.1 Crear la tabla de usuarios en PostgreSQL ✅
- [x] Crear el script SQL para la tabla `users` con los campos: `id`, `name`, `email`, `password_hash`, `avatar_emoji`, `created_at`
- [x] Ejecutar el script en la base de datos
- [x] Añadir comentarios en el SQL explicando cada campo

#### 2.2 Crear la clase User y su DAO en Java ✅
- [x] Crear la clase `User.java` en el paquete `model` con los campos: `id`, `name`, `email`, `passwordHash`, `avatarEmoji`, `createdAt`
- [x] Crear la clase `UserDAO.java` en el paquete `dao` con los métodos:
  - `findByEmail(String email)` — busca un usuario por email usando JDBC
  - `save(User user)` — inserta un nuevo usuario en la BD
- [x] Usar `PreparedStatement` en todas las queries para evitar SQL Injection
- [x] Comentar cada método

#### 2.3 Crear los DTOs y el servicio de usuarios ✅
- [x] Crear `UserRegisterDTO.java` (campos: name, email, password)
- [x] Crear `UserLoginDTO.java` (campos: email, password)
- [x] Crear `UserResponseDTO.java` (campos: id, name, email, avatarEmoji — sin contraseña)
- [x] Crear `UserService.java` con los métodos `register` y `login`
- [x] El método `register` encripta la contraseña antes de guardarla. Usar `BCrypt` (añadir la librería `jbcrypt` al `pom.xml`)
- [x] Comentar cada método

#### 2.4 Crear el Servlet de autenticación ✅
- [x] Crear `AuthServlet.java` en el paquete `servlet`
- [x] Mapear la ruta `/api/auth` con la anotación `@WebServlet`
- [x] Implementar `doPost` para diferenciar entre `/api/auth/register` y `/api/auth/login` leyendo la URL
- [x] Leer el cuerpo de la petición como JSON usando Gson y parsearlo al DTO correspondiente
- [x] El login devuelve un token UUID como token de sesión
- [x] Comentar cada parte del Servlet

#### 2.5 Crear la pantalla de registro en Angular ✅
- [x] Crear el componente `pages/register` con formulario: nombre, email, contraseña
- [x] Validaciones básicas: campos obligatorios, email con formato válido, contraseña mínimo 6 caracteres
- [x] Llamada al endpoint `POST /api/auth/register`
- [x] Mensaje de éxito o error con el estilo cozy definido en la guía

#### 2.6 Crear la pantalla de login en Angular ✅
- [x] Crear el componente `pages/login` con formulario: email, contraseña
- [x] Llamada al endpoint `POST /api/auth/login`
- [x] Guardar el token de sesión en `localStorage`
- [x] Redirigir al dashboard al entrar correctamente
- [x] Mostrar mensaje de error si las credenciales son incorrectas

#### 2.7 Crear el servicio de autenticación en Angular ✅
- [x] Crear `AuthService` en `services/auth.service.ts`
- [x] Métodos: `register()`, `login()`, `logout()`, `isLoggedIn()`, `getCurrentUserId()`
- [x] El servicio lee y escribe el token en `localStorage`
- [x] Crear un guard de ruta para proteger las páginas que requieren login


---

## TAREA 3 — Gestión de hábitos (crear, ver, editar, borrar)

**Historia**: Como usuaria, quiero poder crear mis propios hábitos, editarlos y eliminarlos para tener el control total de lo que quiero trabajar.

### Subtareas

#### 3.1 Crear la tabla de hábitos en PostgreSQL
- Crear el script SQL para la tabla `habits` con los campos: `id`, `user_id`, `name`, `description`, `emoji`, `frequency` (daily/weekly), `target_days` (array de días si es semanal), `color`, `is_active`, `created_at`
- Añadir la clave foránea hacia la tabla `users`
- Comentar cada campo

#### 3.2 Crear la clase Habit y su DAO
- Crear la clase `Habit.java` en el paquete `model` con todos los campos de la tabla
- Crear `HabitDAO.java` en el paquete `dao` con los métodos:
  - `findByUserId(int userId)` — devuelve los hábitos activos del usuario
  - `save(Habit habit)` — inserta un nuevo hábito
  - `update(Habit habit)` — actualiza un hábito existente
  - `deactivate(int habitId)` — pone `is_active = false` (borrado lógico)
- Usar `PreparedStatement` en todas las queries
- Comentar cada método

#### 3.3 Crear DTOs y el servicio de hábitos
- Crear `HabitCreateDTO.java` (campos para crear un nuevo hábito)
- Crear `HabitUpdateDTO.java` (mismos campos, para editar)
- Crear `HabitResponseDTO.java` (todos los campos para mostrar en frontend)
- Crear `HabitService.java` con los métodos: `createHabit`, `getHabitsByUser`, `updateHabit`, `deleteHabit`
- El servicio usa el DAO para acceder a la BD
- Comentar cada método

#### 3.4 Crear el Servlet de hábitos
- Crear `HabitServlet.java` mapeado a `/api/habits`
- Implementar:
  - `doGet` → llama a `getHabitsByUser` y devuelve el JSON de la lista
  - `doPost` → llama a `createHabit` con los datos del cuerpo de la petición
  - `doPut` → llama a `updateHabit` (leer el id de la URL)
  - `doDelete` → llama a `deleteHabit` (leer el id de la URL)
- Leer y escribir JSON con Gson
- Comentar cada método

#### 3.5 Crear el modelo y servicio de hábitos en Angular
- Crear la interfaz `Habit` en `models/habit.model.ts`
- Crear `HabitService` en `services/habit.service.ts` con los métodos que llaman a cada endpoint
- Comentar cada método

#### 3.6 Crear el componente de lista de hábitos
- Crear el componente `components/habits/habit-list`
- Mostrar las tarjetas de hábitos con el estilo cozy: emoji grande, nombre, color de fondo
- Botones para editar y eliminar cada hábito
- Si no hay hábitos, mostrar un mensaje motivador con ilustración: `"¡Aún no tienes hábitos! Añade el primero 🌱"`

#### 3.7 Crear el formulario de crear/editar hábito
- Crear el componente `components/habits/habit-form`
- Campos: nombre, descripción, emoji (selector visual de emojis), frecuencia, color
- El mismo formulario sirve para crear y para editar (si recibe un hábito como entrada, rellena los campos)
- Validaciones básicas: nombre obligatorio, emoji obligatorio


---

## TAREA 4 — Registro diario de hábitos (marcar como hecho)

**Historia**: Como usuaria, quiero poder marcar cada día si he completado un hábito para llevar un registro de mi progreso.

### Subtareas

#### 4.1 Crear la tabla de registros diarios en PostgreSQL
- Crear el script SQL para la tabla `habit_logs` con los campos: `id`, `habit_id`, `user_id`, `completed_date` (DATE), `completed` (BOOLEAN), `notes` (texto opcional), `created_at`
- Añadir restricción `UNIQUE` para que no haya duplicados de `habit_id + completed_date`
- Comentar el script

#### 4.2 Crear la clase HabitLog y su DAO
- Crear `HabitLog.java` en el paquete `model`
- Crear `HabitLogDAO.java` en el paquete `dao` con los métodos:
  - `findByHabitIdAndDate(int habitId, LocalDate date)`
  - `findByUserIdAndDateRange(int userId, LocalDate from, LocalDate to)`
  - `saveOrUpdate(HabitLog log)` — si ya existe el registro del día lo actualiza, si no lo crea
- Usar `PreparedStatement` en todas las queries
- Comentar cada método

#### 4.3 Crear DTOs y el servicio de registros
- Crear `HabitLogDTO.java` (habitId, date, completed, notes)
- Crear `HabitLogService.java` con los métodos:
  - `markHabit(int habitId, LocalDate date, boolean completed)` — crea o actualiza el registro del día
  - `getLogsForDay(int userId, LocalDate date)` — devuelve todos los registros de un día concreto
- Comentar cada método

#### 4.4 Crear el Servlet de registros
- Crear `HabitLogServlet.java` mapeado a `/api/habit-logs`
- `doPost` → marcar o desmarcar un hábito (llama a `markHabit`)
- `doGet` → obtener los registros de un día (lee el parámetro `?date=YYYY-MM-DD` de la URL)
- Comentar cada parte

#### 4.5 Actualizar la tarjeta de hábito en Angular para marcar/desmarcar
- Añadir un botón/checkbox en la tarjeta de hábito
- Al hacer clic, llamar al endpoint de marcar
- Cambiar visualmente la tarjeta: fondo verde suave, check visible, emoji animado
- Mostrar el mensaje `"¡Genial! Un paso más 🌟"` durante 2 segundos

#### 4.6 Crear el componente de vista diaria (dashboard)
- Crear el componente `pages/dashboard`
- Mostrar la fecha de hoy y el saludo personalizado según la hora del día
- Mostrar la lista de hábitos del día con su estado (completo/pendiente)
- Mostrar una barra de progreso circular con los hábitos completados / total del día


---

## TAREA 5 — Estadísticas y gráficos

**Historia**: Como usuaria, quiero ver gráficos visuales de mi progreso para entender cómo estoy evolucionando con mis hábitos.

### Subtareas

#### 5.1 Crear el DAO y servicio de estadísticas en Java
- Crear `StatsDAO.java` en el paquete `dao` con queries SQL que calculan:
  - `getWeeklyStats(int userId)` — cuántos hábitos se completaron cada día de la semana actual
  - `getMonthlyStats(int userId, int year, int month)` — porcentaje de cumplimiento del mes
  - `getStreak(int habitId)` — cuántos días consecutivos se ha completado el hábito
- Usar `PreparedStatement` con las fechas calculadas en Java antes de pasarlas a la query
- Crear `StatsService.java` que usa `StatsDAO`
- Comentar cada método

#### 5.2 Crear el Servlet de estadísticas
- Crear `StatsServlet.java` mapeado a `/api/stats`
- `doGet` con diferentes sub-rutas:
  - `/api/stats/weekly` — estadísticas semanales
  - `/api/stats/monthly?year=YYYY&month=MM` — estadísticas mensuales
  - `/api/stats/streak?habitId=X` — racha de un hábito
- Devolver el resultado como JSON con Gson
- Comentar el Servlet

#### 5.2 Crear el modelo de estadísticas en Angular
- Crear la interfaz `WeeklyStats` y `MonthlyStats` en `models/stats.model.ts`
- Crear `StatsService` en `services/stats.service.ts` con los métodos que llaman a cada endpoint

#### 5.3 Crear el gráfico de barras semanal
- Crear el componente `components/stats/weekly-chart`
- Usar Chart.js (ng2-charts) para mostrar un gráfico de barras
- El eje X son los días de la semana, el eje Y el número de hábitos completados
- Usar los colores de la paleta cozy definida en la guía de estilo

#### 5.4 Crear el gráfico circular mensual
- Crear el componente `components/stats/monthly-chart`
- Mostrar un gráfico de tipo doughnut con el porcentaje de días del mes con todos los hábitos completados
- Añadir el número grande en el centro del gráfico (porcentaje)

#### 5.5 Crear el calendario de hábitos
- Crear el componente `components/stats/habit-calendar`
- Mostrar una cuadrícula con los días del mes
- Colorear cada día según el nivel de cumplimiento: gris (sin datos), rojo suave (0%), amarillo (50%), verde (100%)
- Al pasar el ratón por encima de un día, mostrar un tooltip con los detalles

#### 5.6 Crear la pantalla de estadísticas
- Crear el componente `pages/stats`
- Componer los tres componentes anteriores en una sola pantalla
- Añadir un selector para cambiar entre semana/mes
- Mostrar la racha actual del hábito más largo


---

## TAREA 6 — Sistema de alarmas y recordatorios

**Historia**: Como usuaria, quiero configurar una hora de recordatorio para cada hábito para que la app me avise cuando sea momento de hacerlo.

### Subtareas

#### 6.1 Crear la tabla de alarmas en PostgreSQL
- Crear el script SQL para la tabla `reminders` con los campos: `id`, `habit_id`, `user_id`, `reminder_time` (TIME), `days_of_week` (array: lunes, martes...), `is_active`, `created_at`
- Comentar el script

#### 6.2 Crear la clase Reminder y su DAO en Java
- Crear `Reminder.java` en el paquete `model`
- Crear `ReminderDAO.java` en el paquete `dao` con los métodos:
  - `findByHabitId(int habitId)`
  - `save(Reminder reminder)`
  - `update(Reminder reminder)`
  - `delete(int reminderId)`
- Comentar todo

#### 6.3 Crear el servicio y el Servlet de recordatorios
- Crear `ReminderService.java` con los métodos: `createReminder`, `getRemindersByHabit`, `updateReminder`, `deleteReminder`
- Crear `ReminderServlet.java` mapeado a `/api/reminders`
- Implementar `doGet`, `doPost`, `doPut`, `doDelete`
- Comentar cada parte

#### 6.4 Crear el servicio de notificaciones en Angular
- Crear `NotificationService` en `services/notification.service.ts`
- Solicitar permiso de notificaciones del navegador (`Notification.requestPermission()`)
- Método `scheduleReminder(habitName, time)` que programa una notificación con la Web Notifications API
- Comentar cada parte

#### 6.5 Crear el componente de gestión de alarmas
- Crear el componente `components/habits/habit-reminders`
- Mostrar la lista de recordatorios del hábito seleccionado
- Formulario para añadir una nueva alarma: hora + días de la semana
- Toggle para activar/desactivar cada alarma
- Al activar, llamar al `NotificationService` para programarla en el navegador

#### 6.6 Integrar los recordatorios en la pantalla de perfil
- Crear el componente `pages/profile`
- Mostrar el nombre y avatar del usuario
- Listar todos los hábitos con su recordatorio configurado y el toggle on/off
- Botón para editar el recordatorio de cada hábito


---

## TAREA 7 — Sistema de celebraciones y logros

**Historia**: Como usuaria, quiero recibir felicitaciones y ver mis logros desbloqueados cuando cumplo mis objetivos para sentirme motivada a continuar.

### Subtareas

#### 7.1 Crear la tabla de logros en PostgreSQL
- Crear la tabla `achievements` con los campos: `id`, `code` (ej: "streak_7"), `name`, `description`, `emoji`, `condition_type`, `condition_value`
- Crear la tabla `user_achievements` con los campos: `id`, `user_id`, `achievement_id`, `unlocked_at`
- Insertar los logros predefinidos: primera semana, racha de 7 días, racha de 30 días, mes perfecto

#### 7.2 Crear las clases de logros y sus DAOs
- Crear `Achievement.java` y `UserAchievement.java` en el paquete `model`
- Crear `AchievementDAO.java` con los métodos:
  - `findAll()` — todos los logros disponibles
  - `findUnlockedByUserId(int userId)` — logros ya desbloqueados por el usuario
  - `saveUserAchievement(int userId, int achievementId)` — guardar un logro desbloqueado
- Comentar todo

#### 7.3 Crear el servicio de evaluación de logros
- Crear `AchievementService.java`
- Método `checkAndUnlockAchievements(int userId)` que:
  1. Obtiene las estadísticas del usuario (rachas, total de hábitos, etc.) usando `StatsDAO`
  2. Compara con las condiciones de cada logro
  3. Guarda los logros nuevos con `AchievementDAO`
  4. Devuelve una lista con los logros recién desbloqueados
- Llamar a este método desde `HabitLogService` cada vez que se marca un hábito
- Comentar cada paso

#### 7.4 Crear el Servlet de logros
- Crear `AchievementServlet.java` mapeado a `/api/achievements`
- `doGet /api/achievements` — devuelve todos los logros con si están desbloqueados o no
- `doGet /api/achievements/new` — devuelve solo los recién desbloqueados en esta sesión
- Comentar el Servlet

#### 7.4 Crear el componente de celebración en Angular
- Crear el componente `components/shared/celebration`
- Recibe un mensaje y un emoji como parámetros
- Muestra un modal animado con confeti (usar la librería `canvas-confetti`)
- Se cierra automáticamente a los 3 segundos o al hacer clic
- Comentar el componente

#### 7.5 Integrar la celebración al marcar un hábito
- Después de marcar un hábito, llamar al endpoint `GET /api/achievements/new` para ver si se desbloquearon logros
- Si hay logros nuevos, mostrar el componente de celebración
- Si es el último hábito del día, mostrar también el mensaje `"¡Meta del día cumplida! 🏆"`

#### 7.6 Crear la pantalla de logros
- Añadir una sección de logros en la pantalla de estadísticas
- Mostrar todos los logros disponibles: los desbloqueados en color, los bloqueados en gris con candado
- Mostrar la fecha en que se desbloqueó cada logro


---

## TAREA 8 — Chat / Comunidad para compartir resultados

**Historia**: Como usuaria, quiero poder compartir mis progresos con otras personas de la app para sentirme parte de una comunidad y celebrar juntas los logros.

### Subtareas

#### 8.1 Crear la tabla de posts en PostgreSQL
- Crear el script SQL para la tabla `posts` con los campos: `id`, `user_id`, `content` (texto del post), `habit_id` (opcional, el hábito que comparte), `streak_count` (racha en ese momento), `created_at`
- Crear la tabla `post_reactions` con los campos: `id`, `post_id`, `user_id`, `emoji` (la reacción: 🌟, 💪, 🎉, etc.)
- Comentar el script

#### 8.2 Crear clases y DAOs de posts
- Crear `Post.java` y `PostReaction.java` en el paquete `model`
- Crear `PostDAO.java` con los métodos:
  - `findAll(int limit, int offset)` — todos los posts paginados, ordenados por fecha descendente
  - `save(Post post)` — insertar un nuevo post
- Crear `PostReactionDAO.java` con los métodos:
  - `findByPostId(int postId)` — todas las reacciones de un post
  - `save(PostReaction reaction)` — añadir una reacción
  - `delete(int postId, int userId, String emoji)` — quitar una reacción
- Comentar todo

#### 8.3 Crear DTOs y el servicio de posts
- Crear `PostCreateDTO.java` (content, habitId opcional)
- Crear `PostResponseDTO.java` (incluye nombre del usuario, emoji del hábito, racha, reacciones agrupadas por emoji con su contador)
- Crear `PostService.java` con los métodos: `createPost`, `getAllPosts`, `addReaction`, `removeReaction`
- Comentar cada método

#### 8.4 Crear el Servlet de posts
- Crear `PostServlet.java` mapeado a `/api/posts`
- `doGet` → obtener todos los posts con paginación (`?page=1`)
- `doPost` → publicar un nuevo post
- Para las reacciones, crear `PostReactionServlet.java` mapeado a `/api/posts/reactions`
  - `doPost` → añadir una reacción
  - `doDelete` → quitar una reacción
- Comentar cada parte

#### 8.5 Crear el servicio y modelo de posts en Angular
- Crear la interfaz `Post` y `PostReaction` en `models/post.model.ts`
- Crear `PostService` en `services/post.service.ts` con los métodos que llaman a cada endpoint
- Comentar el servicio

#### 8.6 Crear el componente de tarjeta de post
- Crear el componente `components/chat/post-card`
- Mostrar: avatar/emoji del usuario, nombre, contenido del post, hábito compartido si lo hay, racha
- Mostrar las reacciones agrupadas con su contador
- Al hacer clic en una reacción se añade o se quita la propia
- Estilo cozy: tarjeta con borde redondeado, fondo crema

#### 8.7 Crear el formulario para publicar un resultado
- Crear el componente `components/chat/post-form`
- Campo de texto para escribir el mensaje
- Selector opcional de hábito (desplegable con los hábitos del usuario)
- Muestra automáticamente la racha actual del hábito elegido
- Botón "Compartir 🌟"

#### 8.8 Crear la pantalla de comunidad/chat
- Crear el componente `pages/community`
- Mostrar el formulario de publicar en la parte superior
- Debajo, el feed de posts con las tarjetas
- Paginación simple: botón "Ver más" al final que carga los siguientes 10 posts


---

## TAREA 9 — Sugerencias de hábitos

**Historia**: Como usuaria, quiero que la app me sugiera hábitos populares o que yo misma haya guardado como "quiero empezar esto" para que sea más fácil empezar.

### Subtareas

#### 9.1 Crear la tabla de hábitos sugeridos en PostgreSQL
- Crear el script SQL para la tabla `habit_suggestions` con los campos: `id`, `name`, `description`, `emoji`, `category` (salud, productividad, bienestar...), `is_default` (si viene de la app o es del usuario)
- Insertar al menos 15 sugerencias predefinidas de diferentes categorías
- Comentar el script

#### 9.2 Crear la clase y el DAO de sugerencias
- Crear `HabitSuggestion.java` en el paquete `model`
- Crear `HabitSuggestionDAO.java` con los métodos:
  - `findAll()` — todas las sugerencias
  - `findByCategory(String category)` — filtradas por categoría
- Comentar todo

#### 9.3 Crear el servicio y el Servlet de sugerencias
- Crear `HabitSuggestionService.java` con los métodos `getAllSuggestions` y `getSuggestionsByCategory`
- Crear `HabitSuggestionServlet.java` mapeado a `/api/suggestions`
- `doGet` lee el parámetro opcional `?category=salud` de la URL para filtrar
- Si no hay parámetro, devuelve todas las sugerencias
- Comentar cada parte

#### 9.4 Crear el componente de sugerencias en Angular
- Crear el componente `components/habits/habit-suggestions`
- Mostrar las sugerencias agrupadas por categoría con tabs o chips filtrables
- Cada sugerencia tiene un emoji, nombre y botón `"Añadir a mis hábitos 🌱"`
- Al hacer clic en el botón, pre-rellenar el formulario de crear hábito con los datos de la sugerencia

#### 9.5 Integrar las sugerencias en el flujo de crear hábito
- En la pantalla de crear hábito, añadir un enlace o botón: `"¿No sabes por dónde empezar? Ver sugerencias"`
- Al hacer clic, mostrar el componente de sugerencias como panel lateral o modal
- Al elegir una sugerencia, rellenar el formulario y cerrar el panel

---

## TAREA 10 — Navegación y diseño general de la app

**Historia**: Como usuaria, quiero una app con navegación clara y un diseño bonito y coherente para que usarla sea una experiencia agradable.

### Subtareas

#### 10.1 Configurar el enrutador de Angular
- Configurar las rutas en `app.routes.ts`:
  - `/login` → LoginPage
  - `/register` → RegisterPage
  - `/dashboard` → DashboardPage (protegida con guard)
  - `/stats` → StatsPage (protegida)
  - `/community` → CommunityPage (protegida)
  - `/profile` → ProfilePage (protegida)
- Redirección por defecto a `/dashboard` si está logueado, a `/login` si no

#### 10.2 Crear el componente de navegación lateral
- Crear el componente `components/shared/sidebar`
- Iconos y etiquetas para: Inicio, Estadísticas, Comunidad, Perfil
- Resaltar el ítem activo según la ruta actual
- Mostrar el nombre del usuario y su avatar emoji en la parte superior

#### 10.3 Crear el layout principal de la app
- Crear el componente `components/shared/main-layout`
- Incluye el sidebar en escritorio y barra inferior en móvil
- Aplica el fondo crema y la fuente Nunito a toda la app

#### 10.4 Crear los estilos globales
- En `styles.scss` definir las variables de colores, fuentes, espaciados y bordes de la guía de estilo
- Definir estilos base para botones, inputs, tarjetas y tipografía
- Importar las fuentes de Google Fonts

#### 10.5 Hacer la app responsiva
- Usar media queries para adaptar el layout a móvil (< 768px)
- En móvil: ocultar el sidebar, mostrar barra de navegación inferior
- Las tarjetas de hábitos ocupan el ancho completo en móvil
- Los gráficos se apilan verticalmente en pantallas pequeñas

#### 10.6 Añadir los estados vacíos y de carga
- Crear el componente `components/shared/loading-spinner` con animación cozy (un emoji girando)
- Crear el componente `components/shared/empty-state` que recibe un mensaje y un emoji
- Usarlos en todas las pantallas donde haya listas o gráficos

