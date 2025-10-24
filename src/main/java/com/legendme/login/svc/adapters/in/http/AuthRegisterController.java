package com.legendme.login.svc.adapters.in.http;

import com.legendme.login.svc.adapters.in.http.dto.LoginRequest;
import com.legendme.login.svc.adapters.in.http.dto.LoginResponse;
import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.adapters.in.http.dto.RegisterResponse;
import com.legendme.login.svc.adapters.out.http.UserDirectoryClient;
import com.legendme.login.svc.application.port.out.JwtIssuerPort;
import com.legendme.login.svc.domain.usecase.AuthRegisterControllerLocal;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/legendme/register")
@RequiredArgsConstructor
public class AuthRegisterController {
    private final AuthRegisterControllerLocal useCase;

    @PostMapping("/local")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Validated RegisterRequest req) {
        try {
            log.info("Iniciando proceso de registro LOCAL");
            var r = useCase.authenticate(req);
            return ResponseEntity.ok(new RegisterResponse(r.userId(), r.username(), r.email()));
        } catch (Exception e){
            log.error("Error en proceso de registro LOCAL: {} ", e.getMessage());
            throw e;
        }
    }
}