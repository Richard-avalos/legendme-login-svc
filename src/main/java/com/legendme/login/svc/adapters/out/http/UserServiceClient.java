package com.legendme.login.svc.adapters.out.http;

import lombok.Builder;

/**
 * Interfaz que define la comunicación con el microservicio de usuarios (legendme-users-svc).
 *
 * Métodos principales:
 * - findByEmail: busca un usuario por email.
 * - createLocalUser: crea un usuario local.
 *
 * Records anidados:
 * - CreateUserRequest / CreateUserResponse: datos para crear un usuario.
 * - FindByEmailRequest / FindByEmailResponse: datos para consultar un usuario por email.
 */
public interface UserServiceClient {
    FindByEmailResponse findByEmail(String email);
    CreateUserResponse createLocalUser(CreateUserRequest request);

    @Builder
    record CreateUserRequest(String name, String lastname, String username, String email, String provider, Boolean active) {}

    record CreateUserResponse(String id, String name, String lastname, String username, String email, String provider, Boolean active) {}

    record FindByEmailRequest(String email) {}

    record FindByEmailResponse(String id, String name, String lastname, String username, String email, String provider, Boolean active) {}
}