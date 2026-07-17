# TL9 - Room Database - Gestión de Tareas (To-Do List)

Aplicación Android de lista de tareas (To-Do List) desarrollada en Kotlin, utilizando **Jetpack Compose** para la interfaz de usuario y **Room** como capa de persistencia local.

## Tabla de contenidos

- [Arquitectura utilizada](#arquitectura-utilizada)
- [Implementación de Room](#implementación-de-room)
- [Implementación de Jetpack Compose](#implementación-de-jetpack-compose)

---

## Arquitectura utilizada

El proyecto sigue el patrón **MVVM (Model-View-ViewModel)**, organizado en un único módulo Gradle (`:app`) con **separación por paquetes** en lugar de módulos independientes, dado el tamaño y alcance del proyecto.

### Estructura de paquetes

```
com.example.tl9_room_database_fu_huertas/
├── data/
│   ├── local/
│   │   ├── Task.kt              → Entity de Room
│   │   ├── TaskDao.kt           → DAO con las operaciones CRUD
│   │   └── AppDatabase.kt       → Clase Database (singleton)
│   └── repository/
│       └── TaskRepository.kt    → Repositorio, fuente única de datos
├── ui/
│   ├── tasklist/
│   │   ├── TaskViewModel.kt     → ViewModel de la pantalla de tareas
│   │   ├── TaskListScreen.kt    → Pantalla principal (Composable)
│   │   ├── TaskItem.kt          → Ítem individual de la lista (Composable)
│   │   └── TaskFormDialog.kt    → Diálogo para crear/editar tareas (Composable)
│   ├── navigation/
│   │   └── AppDestinations.kt   → Destinos de navegación de la app
│   └── theme/                   → Tema visual de Compose (colores, tipografía)
├── App.kt                       → Composable raíz de la aplicación
└── MainActivity.kt              → Punto de entrada (Activity)
```

### Responsabilidades por capa

| Capa | Paquete | Responsabilidad |
|---|---|---|
| **Data (local)** | `data.local` | Define el esquema de la base de datos y el acceso crudo a SQLite vía Room. No conoce nada de UI ni de ViewModels. |
| **Data (repository)** | `data.repository` | Actúa como fuente única de verdad para los datos. Desacopla al ViewModel de la implementación concreta de persistencia (Room). |
| **UI (ViewModel)** | `ui.tasklist` | Expone el estado de la pantalla como `StateFlow` y traduce eventos del usuario en llamadas al repositorio. |
| **UI (Composables)** | `ui.tasklist` | Renderiza la interfaz y observa el estado expuesto por el ViewModel, sin acceder directamente a la base de datos. |
| **Navegación** | `ui.navigation` | Define los destinos disponibles de la aplicación. |

### Flujo de datos (unidireccional)

```
UI (Composable)  →  ViewModel  →  Repository  →  DAO  →  Room (SQLite)
UI (Composable)  ←  StateFlow  ←  Flow        ←  Flow  ←  cambios en la tabla
```

Cuando el usuario agrega, edita o elimina una tarea, la operación viaja hacia abajo (UI → ViewModel → Repository → DAO). Room detecta el cambio en la tabla `tasks` y notifica automáticamente a través del `Flow`, propagándose de vuelta hacia arriba hasta la UI, que se recompone sola sin necesidad de refrescos manuales.

---

## Implementación de Room

Room se utiliza como capa de persistencia local, sustituyendo el manejo manual de SQLite. Está compuesto por tres piezas, ubicadas en `data/local/`:

### 1. Entity — `Task.kt`

- `@Entity(tableName = "tasks")` define la tabla `tasks` en la base de datos SQLite.
- `@PrimaryKey(autoGenerate = true)` genera automáticamente el `id` de cada tarea al insertarla.
- `title` almacena el texto de la tarea; `completed` indica si fue marcada como realizada (mapeado internamente a `INTEGER` 0/1 en SQLite).

### 2. DAO — `TaskDao.kt`

- `getAllTasks()` retorna un **`Flow` observable**: cada cambio en la tabla `tasks` (inserción, actualización o eliminación) emite automáticamente una nueva lista, sin necesidad de volver a consultar manualmente.
- Las operaciones de escritura (`insertTask`, `updateTask`, `deleteTask`) son funciones `suspend`, ejecutadas fuera del hilo principal mediante corrutinas.
- `onConflict = OnConflictStrategy.REPLACE` sobrescribe una fila existente si se inserta una tarea con un `id` ya presente.

### 3. Database — `AppDatabase.kt`

- Une la `Entity` (`Task`) con el `DAO` (`TaskDao`) en un único punto de acceso a la base de datos.
- Implementa el patrón **Singleton** (con *double-checked locking* y `@Volatile`) para garantizar que exista una sola instancia de la base de datos en toda la aplicación, evitando múltiples conexiones simultáneas a SQLite.
- `version = 1` corresponde a la versión del esquema; deberá incrementarse junto con una estrategia de migración si se modifica la estructura de la tabla en el futuro.

### Repositorio — `TaskRepository.kt`

Actúa como intermediario entre `TaskDao` y `TaskViewModel`, desacoplando al ViewModel de la implementación concreta de Room. Esto facilita mantenimiento y pruebas, y deja abierta la posibilidad de incorporar otras fuentes de datos (por ejemplo, una API remota) sin modificar la capa de UI.

---

## Implementación de Jetpack Compose

La interfaz de usuario está construida completamente con **Jetpack Compose**, sin uso de XML de layouts, siguiendo un enfoque declarativo y reactivo ubicado en `ui/tasklist/` y `ui/navigation/`.

### `TaskViewModel.kt` — puente entre datos y UI

- Extiende `AndroidViewModel` para tener acceso al contexto de la aplicación y poder construir `AppDatabase`.
- Convierte el `Flow<List<Task>>` del repositorio en un `StateFlow<List<Task>>` mediante `stateIn(...)`, con `SharingStarted.WhileSubscribed(5000)` para mantener la suscripción activa unos segundos tras dejar de observarse (útil ante cambios de configuración, como rotación de pantalla).
- Expone funciones (`addTask`, `updateTask`, `deleteTask`, `toggleCompleted`) que ejecutan operaciones dentro de `viewModelScope.launch { }`, respetando que las funciones del repositorio son `suspend`.

### `TaskListScreen.kt` — pantalla principal

- Observa `viewModel.allTasks` con `collectAsState()`, provocando que la UI se **recomponga automáticamente** cada vez que la lista de tareas cambia.
- Usa `Scaffold` con `TopAppBar` y `FloatingActionButton` (Material 3) como estructura base de la pantalla.
- Renderiza la lista con `LazyColumn`, optimizada para listas potencialmente largas (solo compone los elementos visibles en pantalla).
- Controla el estado local de un diálogo (`showDialog`, `taskToEdit`) para alternar entre creación y edición de tareas, usando `remember { mutableStateOf(...) }`.

### `TaskItem.kt` — ítem individual

- Representa una tarjeta (`ElevatedCard`) con un `Checkbox` para marcar la tarea como completada, el título (con tachado condicional vía `TextDecoration.LineThrough`), y botones de editar/eliminar.
- Es un componente **sin estado propio** (*stateless*): recibe el `Task` y funciones lambda como parámetros, delegando toda la lógica al componente padre (`TaskListScreen`) y, en última instancia, al `TaskViewModel`.

### `TaskFormDialog.kt` — formulario de creación/edición

- Reutiliza el mismo diálogo tanto para **crear** como para **editar** una tarea: si `task` es `null`, se trata de una tarea nueva; si no, se pre-carga el título existente.
- Usa `AlertDialog` de Material 3 con un `OutlinedTextField` controlado por estado (`remember { mutableStateOf(...) }`).
- El botón de confirmar solo se habilita si el título no está vacío (`enabled = title.isNotBlank()`).

### `AppDestinations.kt` y navegación

- Define los destinos disponibles de la app mediante un `enum class`, preparado para escalar si se agregan más pantallas.
- Se consume desde `App.kt` mediante `NavigationSuiteScaffold`, un componente adaptable de Material 3 que ajusta la navegación (barra inferior, rail lateral, drawer) según el tamaño de pantalla del dispositivo.

### Principios de Compose aplicados

| Principio | Cómo se aplica en el proyecto |
|---|---|
| **UI declarativa** | Cada Composable describe *cómo se ve* la UI en función del estado actual, no *cómo mutarla* paso a paso. |
| **Estado elevado (state hoisting)** | `TaskItem` y `TaskFormDialog` no manejan su propio estado de negocio; lo reciben como parámetros y notifican cambios mediante lambdas. |
| **Recomposición reactiva** | El uso de `StateFlow` + `collectAsState()` conecta los cambios en Room con la UI sin lógica manual de refresco. |
| **Componentes reutilizables** | `TaskItem` y `TaskFormDialog` están separados de `TaskListScreen`, permitiendo reutilizarlos o probarlos de forma aislada. |

---

## Resumen general del flujo de la aplicación

1. `MainActivity` inicia la app y establece el tema (`TL9_RoomDatabase_FuHuertasTheme`).
2. `App.kt` monta la navegación (`NavigationSuiteScaffold`) y muestra `TaskListScreen`.
3. `TaskListScreen` observa `TaskViewModel.allTasks`, que a su vez obtiene los datos de `TaskRepository`, que delega en `TaskDao`.
4. Las acciones del usuario (agregar, editar, completar, eliminar) se envían al `TaskViewModel`, que las ejecuta en `viewModelScope` contra el repositorio.
5. Room detecta los cambios en la tabla `tasks` y emite una nueva lista a través del `Flow`, propagándose automáticamente hasta la UI.