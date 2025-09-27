# TravelGo  

TravelGo es una aplicación Android desarrollada en **Kotlin** que permite **gestionar usuarios y visualizar lugares turísticos de interés**.  
Nació como un proyecto autodidacta para poner en práctica conocimientos en **desarrollo Android**.  

---

## 🚀 Funcionalidades actuales

### 🔑 Autenticación y gestión de usuarios
- Registro de nuevos usuarios (clientes) con contraseñas **hasheadas** para mayor seguridad.
- Inicio de sesión con verificación contra la base de datos.
- Gestión de usuarios por parte del administrador:
  - Crear clientes desde el panel de administración.
  - Editar nombre y correo de usuarios existentes.
  - Eliminar usuarios (con restricciones de seguridad: no se puede eliminar al admin ni al usuario logueado).
- Mantenimiento de la sesión activa mediante `SessionManager`.

### 🗺️ Lugares turísticos
- Lista de lugares almacenados en la base de datos local (**Room**).
- Visualización de imágenes, descripción y detalles de cada lugar.
- Opción para agregar lugares turísticos manualmente desde un formulario.
- Pantalla de detalle de lugar con información ampliada.
- Integración de mapas (con **OSMDroid** para OpenStreetMap, además de soporte para Google Maps en emuladores).

### 📱 UI y experiencia de usuario
- **RecyclerView** con adapter para mostrar listas de usuarios y lugares turísticos.
- **SwipeRefreshLayout** para recargar listas.
- Uso de **Material Design Components** (Toolbar, Buttons, Dialogs).
- Pantallas separadas para login, registro, inicio, gestión de usuarios, detalle de lugares, etc.

---

## 🛠️ Tecnologías utilizadas

- **Lenguaje**: Kotlin  
- **Base de datos local**: Room (DAO, entidades y migraciones)  
- **UI / UX**:  
  - RecyclerView  
  - Material Design Components  
  - SwipeRefreshLayout  
- **Mapas**:  
  - OSMDroid (OpenStreetMap)  
  - Google Maps (compatibilidad en emuladores)  
- **Android Jetpack**:  
  - Lifecycle  
  - ViewModel (parcial)  
  - Coroutines + LiveData  
- **Seguridad**:  
  - Hash de contraseñas (`HashUtil`)  
  - Control de sesión (`SessionManager`)  

---

## 📌 Estado del proyecto

- ✅ Gestión completa de usuarios (registro, login, administración de clientes).  
- ✅ Persistencia local con Room funcionando estable.  
- ✅ Integración de mapas (OSMDroid y Google Maps).  
- ✅ Lugares turísticos con detalle, imágenes y descripciones.  

🛠️ **En desarrollo / próximos pasos**:  
- Sección de favoritos para usuarios.  
- Filtros y categorías en los lugares turísticos.  
- Mejoras en UI (animaciones, dark mode).  
- Migración de ciertas funciones a ViewModel y uso de Flow.  

