package com.legendme.login.svc.adapters.in.http;

import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.adapters.in.http.dto.RegisterResponse;
import com.legendme.login.svc.domain.usecase.AuthRegisterControllerLocal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
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