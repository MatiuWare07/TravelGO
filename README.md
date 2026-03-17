# TravelGo  

TravelGo is an Android application developed in Kotlin that allows you to manage users and view tourist attractions.
It began as a self-taught project to practice Android development skills.
---

## 🚀 Current Features

### 🔑 Authentication and User Management
- Registration of new users (clients) with hashed passwords for added security.
- Login with database-verified credentials.
- User management available to the admin:
  - Create clients from the admin panel.
  - Edit the name and email of existing users.
  - Delete users (with security restrictions: the admin and the currently logged-in user cannot be deleted).
- Session persistence using `SessionManager`.

### 🗺️ Tourist Places
- List of locations stored in the local database (Room).
- Display of images, descriptions, and detailed information for each place.
- Option to manually add new tourist spots through a form.
- Dedicated detail screen with extended information.
- Map integration (OSMDroid for OpenStreetMap, plus Google Maps support in emulators).

### 📱 UI and User Experience
- **RecyclerView** with an adapter to display user and tourist place lists.
- **SwipeRefreshLayout** to refresh lists.
- Uso de **Material Design Components** (Toolbar, Buttons, Dialogs).
- Separate screens for login, registration, home, user management, place details, etc.

---

## 🛠️ Technologies Used

- **Language**: Kotlin  
- **Base de datos local**: Room (DAO, entidades y migraciones)  
- **UI / UX**:  
  - RecyclerView  
  - Material Design Components  
  - SwipeRefreshLayout  
- **Maps**:  
  - OSMDroid (OpenStreetMap)  
  - Google Maps (compatibilidad en emuladores)  
- **Android Jetpack**:  
  - Lifecycle  
  - ViewModel (parcial)  
  - Coroutines + LiveData  
- **Security**:  
  - Password hashing (`HashUtil`)  
  - Session control (`SessionManager`)  

---

## 📌 Project Status

- ✅ Full user management (registration, login, client administration).
- ✅ Stable local persistence with Room.
- ✅ Map integration (OSMDroid and Google Maps).
- ✅ Tourist places with details, images, and descriptions.


