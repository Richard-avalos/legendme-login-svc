package com.legendme.login.svc.adapters.out.http.dto;

import java.util.Date;

/**
 * Solicitud para crear un nuevo usuario. Incluye los siguientes campos:
 * @param name nombre del usuario
 * @param lastname apellido del usuario
 * @param username nombre de usuario único
 * @param birthDate fecha de nacimiento
 * @param email correo electrónico
 * @param provider código o fuente del proveedor
 * @param active indicador de activo
 * @param password contraseña en texto plano
 */
public record UserCreateRequest(String name, String lastname, String username, Date birthDate, String email, String provider, Boolean active, String password) {}