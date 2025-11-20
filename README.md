# Ejercicio Restaurante 🍽️

Aplicación de Android para gestionar y visualizar una lista de restaurantes y bares. Permite a los usuarios ver los establecimientos en una lista, en un mapa interactivo y añadir, editar o eliminar sus propios registros.

## 🌟 Características Principales

*   **Listado de Restaurantes**: Visualiza todos los restaurantes en una lista con su nombre, categoría y puntuación.
*   **Mapa Interactivo**: Muestra todos los restaurantes en un mapa de Google Maps utilizando marcadores agrupados (clustering) para una mejor visualización.
*   **Estilo de Mapa Personalizado**: El mapa utiliza un estilo personalizado creado desde la Google Cloud Platform.
*   **Detalles del Restaurante**: Al hacer clic en un restaurante (en la lista o en el mapa), se muestra una tarjeta con información detallada.
*   **Navegación a Google Maps**: Desde la tarjeta de un restaurante en el mapa, un botón permite abrir la ubicación directamente en la aplicación de Google Maps.
*   **Gestión CRUD Completa**:
    *   **Añadir**: Un formulario permite crear nuevos restaurantes, obteniendo automáticamente las coordenadas a partir de la dirección introducida (Geocoding).
    *   **Editar**: Modifica la información de un restaurante existente.
    *   **Eliminar**: Borra un restaurante de la base de datos con una confirmación.
*   **Permisos de Localización**: La aplicación solicita permisos para acceder a la ubicación del usuario y centrar el mapa en su posición actual.
*   **Base de Datos Local**: Toda la información se almacena localmente usando SQLite.

## 🛠️ Tecnologías y Librerías Utilizadas

*   **Lenguaje**: Java
*   **Arquitectura**: Aplicación de una sola actividad (Single-Activity) con Fragmentos.
*   **UI (Interfaz de Usuario)**:
    *   `Material Design 3`: Para los componentes visuales como `CardView`, `Button`, `RecyclerView`, etc.
    *   `RecyclerView`: Para mostrar la lista de restaurantes de forma eficiente.
    *   `Navigation Component`: Para gestionar la navegación entre los diferentes fragmentos (Lista, Mapa, Formulario).
    *   `Lottie`: Para animaciones.
*   **Mapas y Localización**:
    *   `Google Maps SDK for Android`: Para integrar el mapa.
    *   `Google Maps Utils Library`: Para la funcionalidad de clustering de marcadores.
    *   `Play Services Location (FusedLocationProviderClient)`: Para obtener la ubicación actual del usuario.
    *   `Geocoder`: Para convertir direcciones postales en coordenadas (latitud/longitud).
*   **Base de Datos**:
    *   `SQLite`: Como motor de base deatos para el almacenamiento local.

## 📸 Capturas de Pantalla


| Pantalla Inicio || Agregar nuevo Lugar || Lista de Restaurantes | Mapa Interactivo | Formulario de Edición |
| ![inicio](https://github.com/user-attachments/assets/9100397b-541c-4101-b9b9-04c1ba53fa1b) | :---: | :---: |


_Desarrollado con ❤️ por Piera Merlo_
