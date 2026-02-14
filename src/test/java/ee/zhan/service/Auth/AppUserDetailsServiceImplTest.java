package ee.zhan.service.Auth;

import ee.zhan.entity.AppUserEntity;
import ee.zhan.repository.AppUserRepository;
import ee.zhan.service.AppUserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceImplTest {

    @Mock
    private AppUserRepository repository;

    @InjectMocks
    private AppUserDetailsServiceImpl service;

    @Test
    void shouldLoadUserAndNormalizeEmail() {
        String rawInputEmail = "  ZhAn@GmAiL.cOm  ";
        String expectedNormalizedEmail = "zhan@gmail.com";

        var user = new AppUserEntity();
        user.setEmail(expectedNormalizedEmail);
        user.setPassword("passsworD12345!!!!");

        Mockito.when(repository.findByEmail(expectedNormalizedEmail))
                .thenReturn(Optional.of(user));

        UserDetails actualResult = service.loadUserByUsername(rawInputEmail);


        assertNotNull(actualResult);
        assertEquals(expectedNormalizedEmail, actualResult.getUsername());
        Mockito.verify(repository).findByEmail(expectedNormalizedEmail);
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        String email = "email@gmail.com";
        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(email);
        });
    }
}