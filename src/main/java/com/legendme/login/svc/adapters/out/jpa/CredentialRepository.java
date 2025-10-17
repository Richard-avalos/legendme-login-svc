package com.legendme.login.svc.adapters.out.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para gestionar las credenciales de usuarios.
 *
 * Extiende {JpaRepository} para proveer operaciones CRUD básicas sobre
 * {CredentialEntity} usando UUID como identificador.
 *
 * Métodos personalizados:
 * - findByEmail(String email): busca una credencial por email. Retorna Optional vacío si no existe.
 *
 * Uso principal:
 * Se utiliza en los servicios de autenticación para verificar credenciales y recuperar información de login.
 */
public interface CredentialRepository extends JpaRepository<CredentialEntity, UUID> {
    Optional<CredentialEntity> findByEmail(String email);
}