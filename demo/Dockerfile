# Usamos una imagen base con Java 17
FROM eclipse-temurin:17-jdk-alpine

# Creamos el directorio de trabajo
WORKDIR /app

# Copiamos el archivo JAR generado por Maven a la imagen
COPY target/*.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]