package com.legendme.login.svc.adapters.out.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;
import java.util.UUID;

/**
 * Entidad JPA que representa las credenciales de usuario en la base de datos.
 *
 * Almacena información de autenticación local, incluyendo email y hash de contraseña,
 * y mantiene estado, timestamps de creación/actualización y relaciones con el usuario.
 *
 * Campos principales:
 * - id: identificador único de la credencial (UUID).
 * - userId: identificador del usuario al que pertenece la credencial.
 * - email: correo electrónico único, usado para login.
 * - passwordHash: contraseña encriptada.
 * - status: estado de la credencial (ACTIVE, LOCKED, DISABLED).
 * - createdAt / updatedAt: timestamps automáticos de auditoría.
 *
 * Funcionalidad adicional:
 * - prePersist(): asigna UUID automático y convierte email a minúsculas antes de persistir.
 */
@Entity
@Table(name = "credentials", uniqueConstraints = {
        @UniqueConstraint(name = "uk_credentials_email", columnNames = "email")
})
@Getter
@Setter
@ToString(exclude = "passwordHash")
public class CredentialEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
    private UUID userId;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CredentialStatus status = CredentialStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (email != null) email = email.toLowerCase();
    }

    public enum CredentialStatus {
        ACTIVE, LOCKED, DISABLED
    }
}