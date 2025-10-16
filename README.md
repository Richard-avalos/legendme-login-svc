# legendme-login-svc

**Servicio de autenticación para LegendMe**, desarrollado con **Spring Boot**, que permite la autenticación de usuarios mediante **Google Sign-In** y ofrece endpoints **seguros** con protección JWT.

---

## 🚀 Características principales

* 🔐 Autenticación mediante **Google Sign-In**.
* 🛡️ Endpoints REST protegidos con **JWT**.
* ⚙️ Manejo **global de excepciones** y errores personalizados.
* 📦 Respuestas estructuradas y consistentes para autenticación y errores.
* 🧱 Integración modular y lista para despliegue en entornos de microservicios.

---

## 🧰 Requisitos previos

* **Java 21** o superior
* **Maven 3.9.9**
* **Spring Boot 3.5.6**

---

## 🗳 Endpoints principales

### 1️⃣ Autenticación con Google

**Método:** `POST`

**Ruta:** `/legendme/login/google`

**Descripción:** Autentica al usuario utilizando un **Google ID Token** válido y genera tokens JWT de sesión.

#### 📩 Request

**Body (JSON):**

```json
{
  "idToken": "TOKEN_DE_GOOGLE"
}
```

| Campo     | Tipo   | Requerido | Descripción                       |
| --------- | ------ |-----------| --------------------------------- |
| `idToken` | string | Si        | Token de identificación de Google |

#### ✅ Respuesta exitosa

**Código:** `200 OK`
**Body:**

```json
{
  "accessToken": "jwt-access-token",
  "refreshToken": "jwt-refresh-token",
  "userId": "12345",
  "email": "usuario@ejemplo.com",
  "name": "Usuario Ejemplo"
}
```

#### ❌ Posibles errores

| Código | Descripción               | Ejemplo de cuerpo de error                                        |
|--------|---------------------------|-------------------------------------------------------------------|
| 400    | Token inválido o faltante | `{ "status": 400, "message": "El idToken no puede estar vacío" }` |
| 401    | Token de Google no válido | `{ "status": 401, "message": "Token de Google no válido" }`       |
| 500    | Error interno              | `{ "status": 401, "message": "No se ha podido firmar JWT" }`      |

#### 🧪 Ejemplo con `curl`

```bash
curl -X POST http://localhost:8080/legendme/login/google \
  -H "Content-Type: application/json" \
  -d '{"idToken": "TOKEN_DE_GOOGLE"}'
```

---

## ⚠️ Manejo global de errores

Todas las respuestas de error siguen un formato estándar:

```json
{
  "status": <codigo_http>,
  "message": "<descripcion_del_error>"
}
```

---

## 🧩 Arquitectura

El servicio sigue una **arquitectura hexagonal (ports & adapters)**, separando las capas de dominio, infraestructura y presentación.
Incluye configuraciones de seguridad (`SecurityConfig`), validadores de tokens (`GoogleTokenVerifierNimbus`), y controladores REST protegidos.
