package ee.zhan.service;

import ee.zhan.dto.RegistrationRequest;
import ee.zhan.entity.AppUser;
import ee.zhan.exception.EmailAlreadyExists;
import ee.zhan.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private AppUserRepository repository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService service;

    @Captor
    private ArgumentCaptor<AppUser> userCaptor;

    private RegistrationRequest request;

    @BeforeEach
    void setUp() {
        request = new RegistrationRequest();
        request.setEmail("zhan@gmail.com");
        request.setPassword("12345Zxcvb!");
    }

    @Test
    void shouldRegister_whenGivenValidData() {
        //Given
        Mockito.when(repository.existsAppUsersByEmail("zhan@gmail.com")).thenReturn(Boolean.FALSE);
        Mockito.when(passwordEncoder.encode(Mockito.any())).thenReturn("encoded_password");

        //When
        service.register(request);

        //Then
        Mockito.verify(repository).save(userCaptor.capture());
        AppUser capturedUser = userCaptor.getValue();

        //Check
        Assertions.assertEquals("zhan@gmail.com", capturedUser.getEmail());
        Assertions.assertEquals("encoded_password", capturedUser.getPassword());
    }

    @Test
    void shouldNotRegister_whenGivenInValidData() {
        Mockito.when(repository.existsAppUsersByEmail("zhan@gmail.com")).thenReturn(Boolean.FALSE);
        Mockito.when(repository.save(Mockito.any(AppUser.class)))
                .thenThrow(DataIntegrityViolationException.class);

        Assertions.assertThrows(EmailAlreadyExists.class, () -> {
            service.register(request);
        });
    }

    @Test
    void shouldNotRegister_whenGivenInValidEmail() {
        Mockito.when(repository.existsAppUsersByEmail("zhan@gmail.com")).thenReturn(Boolean.TRUE);

        Assertions.assertThrows(EmailAlreadyExists.class, () -> {
            service.register(request);
        });
    }
}
