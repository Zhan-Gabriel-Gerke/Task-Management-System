package ee.zhan.dto.Task;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertFalse;

class CreateTaskRequestTest {

    private Validator validator;
    private CreateTaskRequest createTaskRequest;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("title");
        createTaskRequest.setDescription("description");
    }

    @Test
    void shouldFail_WhenTitleIsBlank() {
        createTaskRequest.setTitle("");
        var violations = validator.validate(createTaskRequest);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void shouldFail_WhenDescriptionIsBlank() {
        createTaskRequest.setDescription("");
        var violations = validator.validate(createTaskRequest);
        Assertions.assertFalse(violations.isEmpty());
    }

    @Test
    void shouldPassValidation() {
        var violations = validator.validate(createTaskRequest);
        Assertions.assertTrue(violations.isEmpty());
    }

}