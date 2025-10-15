package com.legendme.login.svc.adapters.out.mock;

import com.legendme.login.svc.application.port.out.UserDirectoryPort;
import com.legendme.login.svc.shared.dto.GoogleUserPayload;
import com.legendme.login.svc.shared.dto.UserData;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class UserDirectoryDummy implements UserDirectoryPort {

    @Override
    public UserData upsertGoogleUser(GoogleUserPayload p, String provider) {
        System.out.println("[DummyUserDirectory] Simulando registro/actualización de usuario:");
        System.out.println("- Google Sub: " + p.googleSub());
        System.out.println("- Email: " + p.email());
        System.out.println("- Name: " + p.name());

        return new UserData(
                1L,
                p.email(),
                p.name(),
                List.of("USER")
        );
    }

}
