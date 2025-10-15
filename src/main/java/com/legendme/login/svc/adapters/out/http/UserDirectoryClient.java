package com.legendme.login.svc.adapters.out.http;

import com.legendme.login.svc.adapters.out.http.dto.UserRequest;
import com.legendme.login.svc.adapters.out.http.dto.UserResponse;
import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import com.legendme.login.svc.shared.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


/**
 * Cliente HTTP para interactuar con el directorio de usuarios.
 * Implementa el puerto {@link UserDirectoryPort}.
 */
@Component
@RequiredArgsConstructor
public class UserDirectoryClient implements UserDirectoryPort {
    private final RestClient restClient;
    private final Logger logger = LoggerFactory.getLogger(UserDirectoryClient.class);

    @Value("${legendme-users-svc.url}")
    private String url;

    /**
     * Inserta o actualiza la información de un usuario en el directorio de usuarios.
     *
     * @param p Los datos del usuario obtenidos de Google.
     * @return Un objeto {@link UserData} que contiene la información actualizada del usuario.
     */
    @Override
    public UserResponse upsertGoogleUser(GoogleUserPayload p) {
        UserRequest request = buildRequest(p);

        logger.info("Init Upserting Google User, request: {}", request);

        var response = restClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toEntity(UserResponse.class);

        return response.hasBody() ? response.getBody() : null;
    }

    public UserRequest buildRequest(GoogleUserPayload p) {
        // Evita nulls
        String fullName = p.name() != null ? p.name().trim() : "";
        String[] parts = fullName.split(" ", 4);

        String name = parts.length > 0 ? parts[0] : "";
        String lastname = parts.length > 3 ? parts[2]: "";

        // Crear el request con valores seguros
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


