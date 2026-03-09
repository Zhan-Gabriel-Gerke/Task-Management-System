package ee.zhan.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.zhan.auth.jwt.JwtAuthenticationFilter;
import ee.zhan.common.security.SecurityConfig;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@Import(SecurityConfig.class)
public abstract class BaseControllerTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected ObjectMapper objectMapper;
    @MockitoBean protected JwtAuthenticationFilter jwtAuthenticationFilter;
    @MockitoBean protected AuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUpSecurity() throws ServletException, IOException {
        // Mock the JWT filter to bypass authentication for tests
        doAnswer(invocation -> {
            HttpServletRequest request = invocation.getArgument(0);
            HttpServletResponse response = invocation.getArgument(1);
            FilterChain filterChain = invocation.getArgument(2);
            // Just continue the filter chain without checking JWTs
            filterChain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

}
