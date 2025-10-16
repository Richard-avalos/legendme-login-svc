package com.legendme.login.svc.adapters.out.http;

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

/**
 * Cliente HTTP responsable de comunicarse con el microservicio de usuarios (legendme-users-svc).
 *
 * Implementa el puerto UserDirectoryPort, permitiendo realizar operaciones remotas como el registro
 * o actualización de usuarios autenticados con Google.
 *
 * Funcionalidad principal:
 * 1. Construye un objeto UserRequest a partir de los datos provenientes de Google (GoogleUserPayload).
 * 2. Envía una solicitud POST al servicio de usuarios usando RestClient.
 * 3. Valida el código de respuesta HTTP y el cuerpo recibido.
 * 4. Retorna un objeto UserResponse con la información actualizada o creada del usuario.
 *
 * En caso de error en la comunicación o respuesta inválida, lanza una ErrorException con
 * el código y estado HTTP correspondiente.
 *
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

    @Value("${legendme-users-svc.url}")
    private String url;


    /**
     * Realiza un upsert (crear o actualizar) de un usuario autenticado con Google en el servicio de usuarios.
     *
     * Pasos del proceso:
     * 1. Loguea el inicio del proceso y los datos del usuario de Google.
     * 2. Construye un UserRequest a partir del GoogleUserPayload recibido.
     * 3. Envía una solicitud POST al endpoint configurado (legendme-users-svc.url).
     * 4. Valida que la respuesta HTTP sea 200 OK y que el cuerpo no sea nulo.
     * 5. Retorna el cuerpo de la respuesta como un objeto UserResponse.
     *
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
     * Construye un objeto UserRequest a partir de los datos obtenidos de un usuario de Google.
     *
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
        String lastname = parts.length > 3 ? parts[2]: "";

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

}


