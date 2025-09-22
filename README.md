# Librería – Consumidor de API (Android/Java)

App Android sencilla que **lista libros** consumiendo `GET /api/libros` de un backend Spring Boot.

## Requisitos

- Android Studio (Flamingo+)
- JDK 17+
- Backend activo (p. ej., `http://localhost:8080`)

## Configuración rápida

1.  **Permisos / HTTP claro**  
    En `app/src/main/AndroidManifest.xml`:

         <uses-permission android:name="android.permission.INTERNET"/>
         <application android:usesCleartextTraffic="true" ...>

2.  **URL del backend (MainActivity.java)**

         // Emulador Android → localhost del PC
         private static final String API_BASE = "http://10.0.2.2:8080";
         private static final String LIBROS_URL = API_BASE + "/api/libros";

    - Dispositivo físico: usa la **IP local** del PC (ej. `http://192.168.1.50:8080`).

3.  **Layout (activity_main.xml)**  
    Botón “Cargar libros” + ProgressBar + TextView para mostrar la respuesta.

## Ejecución

1. Abre el proyecto en Android Studio.
2. Tools → Device Manager → crea/inicia un emulador (o conecta un dispositivo con _USB debugging_).
3. Selecciona el dispositivo y pulsa **Run**.
4. En la app, toca **“Cargar libros”** para llamar al API.

## Contrato esperado del API

Respuesta JSON (array) como:

        [
          {
            "id": 1,
            "titulo": "El coronel no tiene quien le escriba",
            "categoria": "Novela",
            "autorId": 1,
            "cantidadDisponible": 5
          }
        ]

## Nota:

- El backend debe estar ejecutandose para poder hacer peticiones
