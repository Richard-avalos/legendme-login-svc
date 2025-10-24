package com.legendme.login.svc.application.service;

import com.legendme.login.svc.adapters.in.http.dto.RegisterRequest;
import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.domain.usecase.AuthRegisterControllerLocal;
import com.legendme.login.svc.shared.exceptions.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticateRegisterLocalService implements AuthRegisterControllerLocal {
    private final UserDirectoryPort userDir;

    @Override
    public Result authenticate(RegisterRequest registerRequest) {
        final Boolean exist;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //log.info("Iniciando la autenticacion local, capa de servicio, request: name {}",registerRequest.name(), registerRequest.lastName(),registerRequest.username(), registerRequest.birthDate(), registerRequest.email());
        log.info("Iniciando la autenticacion local, capa de servicio, request:  {}",registerRequest);
//TODO completar esa con info authcontroller
        if (registerRequest.name() == null || registerRequest.name().isEmpty()) {
            throw new ErrorException("Nombre no proporcionado", "L-REG-01", HttpStatus.BAD_REQUEST);
        }
        if (registerRequest.lastName() == null || registerRequest.lastName().isEmpty()) {
            throw new ErrorException("Apellido no proporcionado", "L-REG-02", HttpStatus.BAD_REQUEST);
        }
        if (registerRequest.username() == null || registerRequest.username().isEmpty()) {
            throw new ErrorException("Usuario no proporcionado", "L-REG-03", HttpStatus.BAD_REQUEST);
        }
        if (registerRequest.birthDate() == null) {
            throw new ErrorException("Fecha de nacimiento no proporcionada", "L-REG-04", HttpStatus.BAD_REQUEST);
        }

       /* try{
            LocalDate date = LocalDate.parse((CharSequence) registerRequest.birthDate(), formatter);
        }
        catch (Exception e){
            log.error("Error al obtener el date: {}", e.getMessage());
            throw new ErrorException("Nombre no proporcionado", "L-REG-05", HttpStatus.BAD_REQUEST);
        }*/

        if (registerRequest.email() == null || registerRequest.email().isEmpty()) {
            throw new ErrorException("Correo no proporcionado", "L-REG-06", HttpStatus.BAD_REQUEST);
        }

        exist = userDir.ExistByEmail(registerRequest.email());
        if (exist) {
            throw new ErrorException("El email ya existe", "L-REG-09", HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}
