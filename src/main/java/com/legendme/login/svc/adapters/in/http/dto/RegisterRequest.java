package com.legendme.login.svc.adapters.in.http.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Date;

public record RegisterRequest (
        String name,
        String lastName,
        String username,
        Date birthDate,
        @Email @NotBlank String email,
        @NotBlank String password
){}

