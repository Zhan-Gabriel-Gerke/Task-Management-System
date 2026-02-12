package ee.zhan.service;

import ee.zhan.entity.AppUser;
import ee.zhan.repository.AppUserRepository;
import ee.zhan.security.AppUserAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

        var user = new AppUser();
        user.setEmail(expectedNormalizedEmail);
        user.setPassword("passsworD12345!!!!");

        Mockito.when(repository.findAppUserByEmail(expectedNormalizedEmail))
                .thenReturn(Optional.of(user));

        UserDetails actualResult = service.loadUserByUsername(rawInputEmail);


        assertNotNull(actualResult);
        assertEquals(expectedNormalizedEmail, actualResult.getUsername());
        Mockito.verify(repository).findAppUserByEmail(expectedNormalizedEmail);
    }

    @Test
    void shouldThrowException_WhenUserNotFound() {
        String email = "email@gmail.com";
        Mockito.when(repository.findAppUserByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(email);
        });
    }
}