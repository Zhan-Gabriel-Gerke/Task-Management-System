package ee.zhan.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;
    private UserDetails userDetails;

    private final String TEST_SECRET_KEY = "Zm9vYmFyYmF6MTIzNDU2Nzg5MGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6";
    private final long TEST_JWT_EXPIRATION = 3600000;

    @BeforeEach
    void setUp() {

        userDetails = Mockito.mock(UserDetails.class);
        Mockito.when(userDetails.getUsername()).thenReturn("test@email.com");
    }

    @Test
    void shouldGenerateValidToken() {
        //Act
        jwtService = new JwtService(TEST_SECRET_KEY, TEST_JWT_EXPIRATION);
        String token = jwtService.generateToken(userDetails);

        //Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(3, token.split("\\.").length);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(extractedUsername, userDetails.getUsername());
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldReturnExpiredToken() {
        //Arrange
        jwtService = new JwtService(TEST_SECRET_KEY, 1);
        String token = jwtService.generateToken(userDetails);

        //Act & Assert
        Assertions.assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, userDetails);
        });
    }

    @Test
    void shouldThrownException_WhenTokenSignatureIsInvalid() {
        //Arrange
        String TEST_FOREIGN_SECRET_KEY = "c2VjdXJlc2VjcmV0a2V5Zm9yZXRlc3Rpbmd1c2VyMTIzNDU2Nzg5MGFiY2RlZmdoaWprbG1ub3BxcnN0dXZ3eHl6";
        jwtService = new JwtService(TEST_SECRET_KEY, 3600000);
        JwtService foreignJwtService = new JwtService(TEST_FOREIGN_SECRET_KEY, 3600000);
        String foreignToken = foreignJwtService.generateToken(userDetails);

        //Act & Assert
        Assertions.assertThrows(SignatureException.class, () -> {
            jwtService.isTokenValid(foreignToken, userDetails);
        });
    }
}