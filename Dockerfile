# ---------- ETAPA 1: BUILD ----------
# Usa una imagen con Maven + JDK para compilar el proyecto
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# Crea un directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el descriptor de dependencias (pom.xml)
COPY pom.xml .

# Descarga dependencias sin copiar todo el código aún (mejora la caché)
RUN mvn dependency:go-offline -B

# Copia el código fuente al contenedor
COPY src ./src

# Compila el proyecto y genera el .jar (sin ejecutar tests)
RUN mvn clean package -DskipTests


# ---------- ETAPA 2: RUNTIME ----------
# Usa una imagen ligera solo con JRE (sin Maven)
FROM eclipse-temurin:21-jre

# Crea un directorio limpio para la app
WORKDIR /app

# Copia el .jar generado desde la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

# Expone el puerto interno del contenedor
EXPOSE 8080

# Permite pasar flags personalizados (por ej. -Xms128m)
ENV JAVA_OPTS=""

# Define el comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
