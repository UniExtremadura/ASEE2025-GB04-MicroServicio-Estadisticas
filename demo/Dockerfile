# FASE 1: Compilación
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app

# Copiamos todo el contenido de la carpeta demo
COPY . .

# Verificamos si los archivos realmente llegaron (esto saldrá en los logs)
RUN ls -la

# Compilamos usando el Maven del sistema (no el wrapper) para evitar errores de permisos
RUN mvn clean package -DskipTests

# FASE 2: Ejecución
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]