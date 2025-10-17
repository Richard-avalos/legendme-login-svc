package com.legendme.login.svc.adapters.out.security;

import com.legendme.login.svc.adapters.out.jpa.CredentialEntity;
import com.legendme.login.svc.adapters.out.jpa.CredentialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de Spring Security para cargar detalles de usuario desde la base de datos.
 *
 * Implementa {UserDetailsService} y obtiene la información de login desde {CredentialRepository}.
 *
 * Funcionalidad principal:
 * 1. Busca las credenciales del usuario por email.
 * 2. Valida el estado de la cuenta (ACTIVE, LOCKED, DISABLED).
 * 3. Devuelve un objeto {UserDetails} con username, password, roles y estados de la cuenta.
 *
 * Uso principal:
 * Se utiliza por el {DaoAuthenticationProvider} para autenticar usuarios locales en el sistema.
 */
@Service
@RequiredArgsConstructor
public class CredentialUserDetailsService implements UserDetailsService {
    private final CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.toLowerCase();
        CredentialEntity cred = credentialRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email no registrado"));

        boolean enabled = cred.getStatus() != CredentialEntity.CredentialStatus.DISABLED;
        boolean accountNonLocked = cred.getStatus() != CredentialEntity.CredentialStatus.LOCKED;

        return User.builder()
                .username(cred.getEmail())
                .password(cred.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .disabled(!enabled)
                .accountLocked(!accountNonLocked)
                .build();
    }
}