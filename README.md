# ASEE2025-GB04-MicroServicio-Estadisticas

Este es un microservicio de ejemplo para la gestión de estadísticas, valoraciones y recomendaciones.

## Requisitos Previos

Antes de poder ejecutar este proyecto, necesitarás tener instalado lo siguiente:

*   **Java Development Kit (JDK) 17**: Asegúrate de que tienes el JDK 17 y de que la variable de entorno `JAVA_HOME` apunta a su directorio.

Instala JDK 17 en Windows: Usa el instalador .msi de Adoptium Temurin 17.

Activa "Set JAVA_HOME": Durante la instalación, asegúrate de activar la opción que actualiza la variable de entorno JAVA_HOME.

REINICIA VS CODE: Cierra por completo VS Code y ábrelo de nuevo.

Verifica en el Terminal: Abre un nuevo terminal (Ctrl + Ñ) y escribe java -version. Ahora debería mostrar "17.0.x".
*   **MongoDB**: Una instancia de MongoDB debe estar en ejecución. La configuración por defecto del proyecto intentará conectarse a `mongodb://localhost:27017` y usar la base de datos `test`. Si tu configuración es diferente, deberás modificar el archivo `src/main/resources/application.properties` para añadir las propiedades de conexión de Spring Data MongoDB (por ejemplo, `spring.data.mongodb.uri`).

## Cómo Iniciar el Microservicio

1.  **Clonar el repositorio**:
    ```sh
    git clone <URL-del-repositorio>
    ```

2.  **Navegar al directorio del proyecto**:
    ```sh
    cd ASEE2025-GB04-MicroServicio-Estadisticas/demo
    ```
    CD DEMO PARA EJECUTAR 
3.  **Ejecutar la aplicación**:
    Puedes usar el wrapper de Maven incluido en el proyecto para iniciar la aplicación.

    *   En Windows:
        ```sh
        .\mvnw.cmd spring-boot:run
        o 
        .\mvnw.cmd clean spring-boot:run
        ```

    *   En Linux o macOS:
        ```sh
        ./mvnw spring-boot:run
        ```

Una vez iniciado, el servicio estará disponible en `http://localhost:8081` (o el puerto que hayas configurado). La especificación de la API (OpenAPI) estará visible en `http://localhost:8081/swagger-ui.html`.
