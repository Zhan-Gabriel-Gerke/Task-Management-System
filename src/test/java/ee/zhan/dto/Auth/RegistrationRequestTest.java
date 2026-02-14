package ee.zhan.dto.Auth;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldPassValidation() {
        var request = new RegistrationRequest();
        request.setEmail("test@email.com");
        request.setPassword("StrongPass123!");

        var violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void shouldFail_WhenPasswordTooShort() {
        var request = new RegistrationRequest();
        request.setEmail("test@test.com");
        request.setPassword("Short1!");
        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_WhenPasswordNoDigit() {
        var request = new RegistrationRequest();
        request.setEmail("test@email.com");
        request.setPassword("NoDigitPassword!");

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_WhenPasswordNoSpecialSymbol() {
        var request = new RegistrationRequest();
        request.setEmail("test@email.com");
        request.setPassword("NoSpecialSymbol111111");

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_WhenPasswordIncludesOnlyLowerCaseLetters() {
        var request = new RegistrationRequest();
        request.setEmail("test@email.com");
        request.setPassword("passsswordddd1!");

        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

}