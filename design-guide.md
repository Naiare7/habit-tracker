# 🎨 Guía de Estilo — Habit Tracker (Estilo Cozy & Alegre)

## Concepto Visual

La aplicación tiene una personalidad **cálida, acogedora y motivadora**. Como un cuaderno de papel artesanal con stickers, colores pastel y pequeñas ilustraciones que hacen que apuntarse hábitos se sienta como algo bonito y no una obligación. El estilo es **cozy** (acogedor, hogareño) combinado con toques de **alegría y celebración**.

---

## Paleta de Colores

### Colores Principales
| Nombre | Hex | Uso |
|---|---|---|
| Melocotón suave | `#FFB347` | Botones primarios, acentos |
| Verde salvia | `#A8C5A0` | Hábitos completados, éxito |
| Lila pastel | `#C3B1E1` | Fondos de tarjetas, headers |
| Rosa empolvado | `#F2C4CE` | Notificaciones, badges |
| Crema cálida | `#FFF8F0` | Fondo principal de la app |

### Colores de Apoyo
| Nombre | Hex | Uso |
|---|---|---|
| Marrón canela | `#8B6F47` | Texto principal, bordes sutiles |
| Amarillo mantequilla | `#FFF3B0` | Destacados, tooltips |
| Coral suave | `#FF6B6B` | Alertas, recordatorios |
| Azul cielo | `#B8D4E8` | Gráficos, estadísticas |
| Blanco hueso | `#FAFAF5` | Fondos secundarios |

### Colores Semánticos
```scss
$color-success:  #A8C5A0;  // Verde salvia — hábito completado
$color-warning:  #FFB347;  // Melocotón — recordatorio pendiente
$color-danger:   #FF6B6B;  // Coral — hábito fallado
$color-info:     #B8D4E8;  // Azul cielo — información
$color-bg:       #FFF8F0;  // Crema — fondo app
$color-text:     #8B6F47;  // Marrón canela — texto
```

---

## Tipografía

### Fuentes
- **Títulos y encabezados**: `Nunito` (Google Fonts) — redondeada, amigable, cálida.
- **Texto de cuerpo**: `Nunito Sans` — legible y suave, misma familia.
- **Números y estadísticas**: `Nunito` en bold — para que los logros destaquen.

### Escala de Tamaños
```scss
$font-size-xs:   12px;  // Metadatos, fechas pequeñas
$font-size-sm:   14px;  // Texto secundario
$font-size-base: 16px;  // Texto de cuerpo
$font-size-md:   20px;  // Subtítulos
$font-size-lg:   26px;  // Títulos de sección
$font-size-xl:   36px;  // Títulos de página, números grandes
```

### Pesos
- Normal: `400`
- Semi-bold: `600`
- Bold: `800` — para celebraciones y logros

---

## Espaciado y Bordes

### Espaciado (base 8px)
```scss
$space-xs:  4px;
$space-sm:  8px;
$space-md:  16px;
$space-lg:  24px;
$space-xl:  40px;
$space-xxl: 64px;
```

### Bordes Redondeados
- Tarjetas de hábitos: `border-radius: 20px` — muy redondeadas, sensación suave.
- Botones: `border-radius: 50px` — tipo pill, dinámicos.
- Inputs: `border-radius: 12px` — redondeados pero no excesivos.
- Modales: `border-radius: 24px`.
- Badges / etiquetas: `border-radius: 999px` — píldoras.

### Sombras
```scss
// Sombra suave tipo papel
$shadow-card: 0 4px 16px rgba(139, 111, 71, 0.12);

// Sombra para elementos flotantes
$shadow-float: 0 8px 32px rgba(139, 111, 71, 0.18);

// Sin sombra (para fondos de pantalla)
$shadow-none: none;
```

---

## Componentes UI

### Tarjeta de Hábito
- Fondo: blanco hueso `#FAFAF5` con borde fino `1px solid #E8DDD0`
- Bordes redondeados: `20px`
- Sombra: `$shadow-card`
- Al pasar el cursor (hover): sombra un poco más pronunciada + leve escala `transform: scale(1.02)`
- Cuando está completado: fondo verde salvia suave `#E8F5E4`, check verde visible
- Emoji representativo del hábito visible y grande (al menos 28px)

### Botones
- **Primario**: fondo melocotón `#FFB347`, texto blanco, sin borde, sombra suave.
- **Secundario**: fondo transparente, borde `2px solid #FFB347`, texto melocotón.
- **Éxito / Completar**: fondo verde salvia, texto blanco.
- **Peligro**: fondo coral `#FF6B6B`, texto blanco.
- Todos tienen `border-radius: 50px` y padding generoso (`12px 24px`).
- Al hacer hover: ligero oscurecimiento del fondo (`filter: brightness(0.95)`) y pequeña elevación.

### Inputs y Formularios
- Borde: `2px solid #E8DDD0`
- Fondo: `#FAFAF5`
- Al enfocar (focus): borde lila `#C3B1E1` + sombra suave lila
- Placeholder: color gris muy suave, nunca negro
- Labels encima del campo, en marrón canela, tamaño `14px`

### Navegación
- Barra lateral o inferior con iconos grandes y etiquetas
- Fondo: lila pastel muy suave `#EDE8F5`
- Ítem activo: fondo blanco, borde izquierdo `4px solid #FFB347`, texto en bold
- Ítem inactivo: texto marrón canela claro

### Gráficos (estadísticas)
- Usa Chart.js con colores de la paleta (no los colores por defecto)
- Fondo del área de gráfico: crema `#FFF8F0`
- Líneas del grid: muy sutiles, `rgba(139, 111, 71, 0.1)`
- Los gráficos de barras usan degradados suaves de la paleta

---

## Animaciones y Micro-interacciones

### Principios
- Las animaciones son **suaves y rápidas** (duración entre 200ms y 400ms).
- Efecto **bounce suave** cuando se completa un hábito.
- **Confeti** o partículas cuando se logra una racha o meta importante.
- Las tarjetas **aparecen con fade-in + slide-up** al cargar la pantalla.

### Valores CSS
```scss
$transition-fast:   200ms ease;
$transition-normal: 300ms ease;
$transition-slow:   400ms ease-in-out;
$transition-bounce: 300ms cubic-bezier(0.34, 1.56, 0.64, 1);
```

### Celebraciones
- Al completar un hábito: emoji animado que aparece y desaparece (`🌟`, `✨`, `🎉`)
- Al lograr una racha de 7 días: modal de celebración con confeti
- Al alcanzar la meta semanal: mensaje personalizado y badge especial

---

## Iconografía

- Usa **emojis** como iconos principales de cada hábito (el usuario los elige al crear el hábito).
- Para la UI general usa iconos de [Lucide Icons](https://lucide.dev/) — línea fina, estilo suave.
- Tamaño de iconos de navegación: `24px`
- Tamaño de emojis de hábitos en tarjeta: `32px`
- Tamaño de emojis en celebraciones: `48px` o más

---

## Pantallas y Layout

### Estructura General
```
┌─────────────────────────────────┐
│  Header (nombre + saludo cozy)  │
├──────────┬──────────────────────┤
│          │                      │
│  Nav     │   Contenido          │
│ lateral  │   principal          │
│          │                      │
└──────────┴──────────────────────┘
```
En móvil, la navegación se mueve a una **barra inferior**.

### Pantalla de Inicio (Dashboard)
- Saludo personalizado con el nombre del usuario y hora del día
  - Mañana: `"¡Buenos días, [nombre]! ☀️ ¿Lista para empezar?"` 
  - Tarde: `"¡Buenas tardes, [nombre]! 🌤️ ¿Cómo vas hoy?"`
  - Noche: `"¡Buenas noches, [nombre]! 🌙 Repasa tu día"`
- Resumen del día: hábitos completados / total con barra de progreso circular
- Lista de hábitos del día con sus tarjetas
- Botón flotante (FAB) `+` para añadir nuevo hábito

### Pantalla de Estadísticas
- Gráfico de racha semanal (barras)
- Gráfico circular de cumplimiento del mes
- Calendario de hábitos con días marcados en colores
- Medallas y logros desbloqueados

### Pantalla de Chat / Comunidad
- Lista de posts de usuarios con avatar, nombre y resultado compartido
- Botón para publicar mi progreso
- Reacciones con emojis (sin comentarios de texto complejos en v1)

### Pantalla de Perfil y Alarmas
- Foto o avatar del usuario
- Lista de alarmas configuradas por hábito
- Botón para editar o añadir alarma a cada hábito

---

## Tono de los Mensajes

- **Motivador sin ser agresivo**: `"¡Vas genial! 🌱"` en vez de `"¡SIGUE ASÍ!"`
- **Cercano y cálido**: usa el nombre de la persona siempre que puedas.
- **Celebra los pequeños logros**: incluso completar 1 hábito merece un mensaje bonito.
- **Sin culpa si no cumples**: si llevas días sin registrar, el mensaje es `"¡Hola! Te echábamos de menos 🌸"`, no un recordatorio de fracaso.

### Ejemplos de Mensajes
| Situación | Mensaje |
|---|---|
| Completar un hábito | `"¡Genial! Un paso más 🌟"` |
| Racha de 7 días | `"¡7 días seguidos! Eres increíble 🎉"` |
| Meta semanal cumplida | `"¡Semana perfecta! Te lo mereces 🏆"` |
| Primer hábito del día | `"¡Empezamos! Hoy va a ser un gran día ☀️"` |
| Recordatorio de alarma | `"Hey [nombre], es hora de [hábito] 🌿"` |
| Tras varios días sin abrir | `"¡Hola! Te echábamos de menos 🌸 ¿Seguimos?"` |
