package ee.zhan.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import ee.zhan.auth.dto.RegistrationRequest;
import ee.zhan.common.AbstractIntegrationTest;
import ee.zhan.user.AppUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void testRegisterUser_WhenGivenValidData_ShouldRegisterUser() throws Exception {
        // Arrange
        var registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail(generateUniqueEmail());
        registrationRequest.setPassword(generateUniquePassword());

        // Act
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated());

        AppUserEntity user = appUserRepository.findByEmail(registrationRequest.getEmail()).orElseThrow();

        // Assert
        Assertions.assertEquals(user.getEmail(), registrationRequest.getEmail());
    }

    @Test
    void testRegisterUser_WhenGivenInvalidData_ShouldThrowException() throws Exception {
        // Arrange
        var registrationRequest = new RegistrationRequest();
        registrationRequest.setEmail("invalid_email");
        registrationRequest.setPassword("invalid_password");

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isBadRequest());
    }
}
