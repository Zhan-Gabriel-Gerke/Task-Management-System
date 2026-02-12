package ee.zhan.service;

import ee.zhan.AbstractIntegrationTest;
import ee.zhan.dto.RegistrationRequest;
import ee.zhan.entity.AppUser;
import ee.zhan.exception.EmailAlreadyExists;
import ee.zhan.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.junit.Assert.*;

public class AuthServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private AppUserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldRegisterUserInRealDatabase() {
        var request = new RegistrationRequest();
        request.setEmail("some_cool_email@gmail.com");
        request.setPassword("someStrongPassword1!");

        authService.register(request);

        Optional<AppUser> savedUser = userRepository.findAppUserByEmail("some_cool_email@gmail.com");

        Assertions.assertTrue(savedUser.isPresent());
        Assertions.assertEquals("some_cool_email@gmail.com", savedUser.get().getEmail());
        Assertions.assertNotEquals("someStrongPassword1!", savedUser.get().getPassword());
        Assertions.assertTrue(savedUser.get().getPassword().startsWith("$2a$"));
    }

    @Test
    void shouldFailWhenDuplicateEmailExistsInDb() {
        var user = new AppUser();
        user.setEmail("duplicate@test.com");
        user.setPassword("Password123456!");
        userRepository.save(user);

        var request = new RegistrationRequest();
        request.setEmail(user.getEmail());
        request.setPassword(user.getPassword());

        Assertions.assertThrows(EmailAlreadyExists.class, () -> {
            authService.register(request);
        });
    }
}
