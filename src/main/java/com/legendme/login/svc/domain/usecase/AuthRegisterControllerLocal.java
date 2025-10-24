package com.legendme.login.svc.domain.usecase;

import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.domain.model.AuthTokens;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.util.Date;

public interface AuthRegisterControllerLocal {
    record Result(java.util.UUID userId, String username, String email) {}

    Result authenticate(RegisterRequest registerRequest);
}
