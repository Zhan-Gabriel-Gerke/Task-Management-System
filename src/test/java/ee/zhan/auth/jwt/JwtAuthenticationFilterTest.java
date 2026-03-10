package ee.zhan.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTest {

    @Mock private JwtService jwtService;
    @Mock private UserDetailsService userDetailsService;
    @Mock private HandlerExceptionResolver resolver;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthenticationFilter filter;

    private String testToken = "test.jwt.token";
    private String authHeader = "Bearer " + testToken;

    @BeforeEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUser_WhenTokenIsValid() throws ServletException, IOException {
        // Arrange
        String userEmail = "test@email.com";
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        Mockito.when(request.getHeader("Authorization")).thenReturn(authHeader);
        Mockito.when(jwtService.extractUsername(testToken)).thenReturn(userEmail);
        Mockito.when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
        Mockito.when(jwtService.isTokenValid(testToken, userDetails)).thenReturn(true);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());

        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotAuthenticate_WhenHeaderIsMissing() throws ServletException, IOException {
        // Arrange
        Mockito.when(request.getHeader("Authorization")).thenReturn(null);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Mockito.verify(jwtService, Mockito.never()).extractUsername(Mockito.anyString());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        Mockito.verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleExpiredToken() throws ServletException, IOException {
        // Arrange

        Mockito.when(request.getHeader("Authorization")).thenReturn(authHeader);
        Mockito.when(jwtService.extractUsername(testToken))
                .thenThrow(new ExpiredJwtException(null, null, "Token has expired"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        Mockito.verify(resolver).resolveException(
                Mockito.eq(request),
                Mockito.eq(response),
                Mockito.isNull(),
                Mockito.any(JwtAuthException.class)
        );
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        Mockito.verify(filterChain, Mockito.never()).doFilter(request, response);
    }
}