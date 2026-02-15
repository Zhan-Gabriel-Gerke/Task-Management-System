package ee.zhan.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.zhan.auth.dto.RegistrationRequest;
import ee.zhan.common.security.SecurityConfig;
import ee.zhan.user.AppUserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private AuthService service;
    @MockitoBean private AppUserDetailsServiceImpl userDetailsService;

    @Test
    void register_ShouldBePublic() throws Exception {
        RegistrationRequest request = new RegistrationRequest();
        request.setEmail("valid@email.com");
        request.setPassword("ValidPassword1!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                // .with(csrf())
        ).andExpect(status().isCreated());
    }

    @Test
    void me_ShouldBeProtected_401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void me_ShouldAllowAuthorized_200() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk());
    }


}