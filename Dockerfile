# Usar una imagen base con OpenJDK
FROM openjdk:17-jdk-slim

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el archivo de construcción del proyecto
COPY build/libs/tu-api.jar /app/GestionTarea22-0.0.1-SNAPSHOT.jar

# Exponer el puerto donde se ejecutará la aplicación
EXPOSE 8080

# Ejecutar la aplicación Spring Boot
ENTRYPOINT ["java", "-jar", "/app/GestionTarea22-0.0.1-SNAPSHOT.jar"]
