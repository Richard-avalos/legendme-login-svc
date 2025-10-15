package com.legendme.login.svc.adapters.out.http;

import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import com.legendme.login.svc.shared.dto.UserData;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Cliente HTTP para interactuar con el directorio de usuarios.
 * Implementa el puerto {@link UserDirectoryPort}.
 */
@Component
@RequiredArgsConstructor
public class UserDirectoryClient implements UserDirectoryPort {
    private final WebClient userWebClient;

    // TODO: Definir logica que tomara el endpoint en el otro servicio
    /**
     * Inserta o actualiza la información de un usuario en el directorio de usuarios.
     *
     * @param p Los datos del usuario obtenidos de Google.
     * @return Un objeto {@link UserData} que contiene la información actualizada del usuario.
     */
    @Override
    public UserData upsertGoogleUser(GoogleUserPayload p, String provider) {
        request r = new request(p, provider);
        return userWebClient.post()
                // TODO: set correct path
                .uri("")
                .bodyValue(r)
                .retrieve()
                .bodyToMono(UserData.class)
                .block();
    }

    record request(GoogleUserPayload googleUserPayload, String provider){}
}
