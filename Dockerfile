# Usa una imagen base de OpenJDK
FROM openjdk:17-jdk-slim

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de tu proyecto al contenedor
COPY . /app

# Exponer el puerto que tu aplicación usará (en el caso de Spring Boot es generalmente 8080)
EXPOSE 8080

# Construir el proyecto con Gradle (puedes usar el archivo gradlew)
RUN ./gradlew build -x test

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "build/libs/GestionTarea22-0.0.1-SNAPSHOT.jar"]
