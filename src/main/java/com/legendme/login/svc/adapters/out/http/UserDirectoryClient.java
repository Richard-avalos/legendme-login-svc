package com.legendme.login.svc.adapters.out.http;

import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.adapters.out.http.dto.UserCreateRequest;
import com.legendme.login.svc.adapters.out.http.dto.UserRequest;
import com.legendme.login.svc.adapters.out.http.dto.UserResponse;
import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Map;

/**
 * Cliente HTTP responsable de comunicarse con el microservicio de usuarios (legendme-users-svc).
 * <p>
 * Implementa el puerto UserDirectoryPort, permitiendo realizar operaciones remotas como el registro
 * o actualización de usuarios autenticados con Google.
 * <p>
 * Funcionalidad principal:
 * 1. Construye un objeto UserRequest a partir de los datos provenientes de Google (GoogleUserPayload).
 * 2. Envía una solicitud POST al servicio de usuarios usando RestClient.
 * 3. Valida el código de respuesta HTTP y el cuerpo recibido.
 * 4. Retorna un objeto UserResponse con la información actualizada o creada del usuario.
 * <p>
 * En caso de error en la comunicación o respuesta inválida, lanza una ErrorException con
 * el código y estado HTTP correspondiente.
 * <p>
 * Variables:
 * - restClient: cliente HTTP de Spring usado para las solicitudes REST.
 * - url: URL base del servicio de usuarios, configurada en legendme-users-svc.url.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDirectoryClient implements UserDirectoryPort {
    private final RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(UserDirectoryClient.class);

    /**
     * Respuesta simplificada del Users Service para endpoints de verificación de existencia.
     * Contiene un único campo:
     * - exists: indica si el recurso consultado existe.
     */
    record ExistResponse(boolean exists) {
    }

    @Value("${legendme-users-svc.url}")
    private String url;

    @Value("${legendme-users-svc.existByEmailUrl}")
    private String existByEmailUrl;

    @Value("${legendme-users-svc.existByUsernameUrl}")
    private String existByUsernameUrl;

    @Value("${legendme-users-svc.createLocalUrl}")
    private String createLocalUrl;

    @Value("${app.X-Internal-Token}")
    private String internalToken;

    /**
     * Realiza un upsert (crear o actualizar) de un usuario autenticado con Google en el servicio de usuarios.
     * <p>
     * Pasos del proceso:
     * 1. Loguea el inicio del proceso y los datos del usuario de Google.
     * 2. Construye un UserRequest a partir del GoogleUserPayload recibido.
     * 3. Envía una solicitud POST al endpoint configurado (legendme-users-svc.url).
     * 4. Valida que la respuesta HTTP sea 200 OK y que el cuerpo no sea nulo.
     * 5. Retorna el cuerpo de la respuesta como un objeto UserResponse.
     * <p>
     * Si la respuesta no es exitosa o el cuerpo es nulo, se lanza una ErrorException.
     *
     * @param p objeto GoogleUserPayload con la información proveniente del token de Google.
     * @return UserResponse con los datos del usuario creados o actualizados.
     * @throws ErrorException si ocurre un error en la comunicación o el servicio retorna una respuesta inválida.
     */
    @Override
    public UserResponse upsertGoogleUser(GoogleUserPayload p) {
        log.info("Iniciando Google Upserting, User: {}", p.toString());

        UserRequest request = buildRequest(p);

        logger.info("Iniciando llamada al servicio: {}, request: {}", url, request.toString());

        var response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(UserResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Error al llamar al servicio de usuarios, Status: {}, Body: {}",
                    response.getStatusCode(), response.hasBody() ? response.getBody() : "No Body");
            throw new ErrorException("Error al llamar al servicio de usuarios", "G-LOG-07", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response.getBody() == null) {
            log.error("El servicio de usuarios devolvió un body null");
            throw new ErrorException("Error al llamar al servicio de usuarios", "G-LOG-08", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response.getBody();
    }

    /**
     * Consulta el Users Service por email.
     * - Retorna UserResponse si la respuesta es 2xx.
     * - Retorna null si el servicio responde 404 o 500 con el mensaje "Usuario no encontrado".
     * - Lanza ErrorException para cualquier otro 4xx/5xx.
     *
     * @param email email del usuario a consultar.
     * @return UserResponse si existe, o null si no existe.
     */
    @Override
    public Boolean existByEmail(String email) {

        logger.info("Llamando a ExistUserByEmail: {}, email: {}", existByEmailUrl, email);
        var response = restClient.post()
                .uri(existByEmailUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Internal-Token", internalToken)
                .body(Map.of("email", email))
                .retrieve()
                .toEntity(ExistResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Error al llamar al servicio de usuarios, Status: {}, Body: {}",
                    response.getStatusCode(), response.hasBody() ? response.getBody() : "No Body");
            throw new ErrorException("Error al llamar al servicio de usuarios", "L-REG-07", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response.getBody() == null) {
            log.error("El servicio de usuarios devolvió un body null");
            throw new ErrorException("Error al llamar al servicio de usuarios", "L-REG-08", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response.getBody().exists();
    }

    /**
     * Consulta el Users Service por username.
     * - Retorna \`true\` si el servicio responde \`200 OK\` con un body válido.
     * - Lanza \`ErrorException\` si el estado es distinto de 200 o el body es nulo.
     *
     * @param username nombre de usuario a consultar.
     * @return \`true\` si existe; \`false\` si no existe.
     */
    @Override
    public Boolean existByUsername(String username) {

        logger.info("Llamando a ExistByUsername: {}, email: {}", existByUsernameUrl, username);
        var response = restClient.post()
                .uri(existByUsernameUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Internal-Token", internalToken)
                .body(Map.of("username", username))
                .retrieve()
                .toEntity(ExistResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Error al llamar al servicio de usuarios, Status: {}, Body: {}",
                    response.getStatusCode(), response.hasBody() ? response.getBody() : "No Body");
            throw new ErrorException("Error al llamar al servicio de usuarios", "L-REG-10", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response.getBody() == null) {
            log.error("El servicio de usuarios devolvió un body null");
            throw new ErrorException("Error al llamar al servicio de usuarios", "L-REG-11", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response.getBody().exists();
    }
    /**
     * Crea un usuario local en el Users Service.
     * Proceso:
     * 1. Construye \`UserCreateRequest\` a partir del \`RegisterRequest\`.
     * 2. Envía una solicitud \`POST\` al endpoint configurado con el header \`X-Internal-Token\`.
     * 3. Valida que la respuesta HTTP sea \`200 OK\` y que el body no sea nulo.
     * 4. Retorna el body como \`RegisterResponse\`.
     *
     * Lanza \`ErrorException\` si el estado HTTP no es \`200 OK\` o el body es nulo.
     *
     * @param registerRequest datos de registro del usuario.
     * @return \`RegisterResponse\` con la información del usuario creado.
     * @throws ErrorException si ocurre un error en la comunicación o la respuesta es inválida.
     */
    @Override
    public RegisterResponse createLocalUser(RegisterRequest registerRequest) {

        logger.info("Llamando a createLocalUser URL: {}", createLocalUrl );

        var request = buildCreateRequest(registerRequest);

        var response = restClient.post()
                .uri(createLocalUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Internal-Token", internalToken)
                .body(request)
                .retrieve()
                .toEntity(RegisterResponse.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Error al crear usuario en el servicio de users, Status: {}, Body: {}",
                    response.getStatusCode(), response.hasBody() ? response.getBody() : "No Body");
            throw new ErrorException("Error al llamar al servicio de usuarios", "L-REG-13", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (response.getBody() == null) {
            log.error("El servicio de usuarios devolvió un body null");
            throw new ErrorException("Error al llamar al servicio de usuarios", "L-REG-14", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return response.getBody();
    }
    /**
     * Construye un objeto UserRequest a partir de los datos obtenidos de un usuario de Google.
     * <p>
     * Reglas:
     * - Toma el primer fragmento del nombre completo como nombre.
     * - Toma el tercer fragmento como apellido (si existe).
     * - Usa la parte anterior al símbolo '@' como nombre de usuario.
     * - Establece el proveedor como "GOOGLE".
     *
     * @param p objeto GoogleUserPayload con los datos básicos del usuario autenticado.
     * @return UserRequest listo para ser enviado al servicio de usuarios.
     */
    public UserRequest buildRequest(GoogleUserPayload p) {
        String fullName = p.name() != null ? p.name().trim() : "";
        String[] parts = fullName.split(" ", 4);

        String name = parts.length > 0 ? parts[0] : "";
        String lastname = parts.length > 3 ? parts[2] : "";

        return new UserRequest(
                name,
                lastname,
                null,
                p.email().split("@")[0],
                p.email(),
                null,
                "GOOGLE"
        );
    }

    /**
     * Construye un \`UserCreateRequest\` a partir de los datos de registro locales.
     * Reglas:
     * - Copia \`name\`, \`lastName\`, \`username\`, \`birthDate\`, \`email\` y \`password\`.
     * - Fija el proveedor en \`LOCAL\`.
     * - Marca \`enabled\` como \`TRUE\`.
     *
     * @param p datos de registro del usuario.
     * @return \`UserCreateRequest\` listo para enviar al servicio de usuarios.
     */
    public UserCreateRequest buildCreateRequest(RegisterRequest p) {
        return new UserCreateRequest(
                p.name(),
                p.lastName(),
                p.username(),
                p.birthDate(),
                p.email(),
                "LOCAL",
                Boolean.TRUE,
                p.password()
        );
    }
}