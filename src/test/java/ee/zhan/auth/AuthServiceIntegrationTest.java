package ee.zhan.auth;

import ee.zhan.common.AbstractIntegrationTest;
import ee.zhan.auth.dto.RegistrationRequest;
import ee.zhan.user.AppUserEntity;
import ee.zhan.auth.exceptions.EmailAlreadyExists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

public class AuthServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired private AuthService authService;

    @Test
    void shouldRegisterUserInRealDatabase() {
        var request = new RegistrationRequest();
        request.setEmail("some_cool_email@gmail.com");
        request.setPassword("someStrongPassword1!");

        authService.register(request);

        Optional<AppUserEntity> savedUser = appUserRepository.findByEmail("some_cool_email@gmail.com");

        Assertions.assertTrue(savedUser.isPresent());
        Assertions.assertEquals("some_cool_email@gmail.com", savedUser.get().getEmail());
        Assertions.assertNotEquals("someStrongPassword1!", savedUser.get().getPassword());
        Assertions.assertTrue(savedUser.get().getPassword().startsWith("$2a$"));
    }

    @Test
    void shouldFailWhenDuplicateEmailExistsInDb() {
        var user = new AppUserEntity();
        user.setEmail("duplicate@test.com");
        user.setPassword("Password123456!");
        appUserRepository.save(user);

        var request = new RegistrationRequest();
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());

        Assertions.assertThrows(EmailAlreadyExists.class, () -> {
            authService.register(request);
        });
    }
}
