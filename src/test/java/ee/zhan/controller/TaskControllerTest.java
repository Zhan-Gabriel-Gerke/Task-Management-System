package ee.zhan.controller;

import ee.zhan.dto.Task.CreateTaskRequest;
import ee.zhan.mapper.Task.TaskWebMapper;
import ee.zhan.security.SecurityConfig;
import ee.zhan.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskWebMapper taskWebMapper;

    @MockitoBean
    private TaskService taskService;

    private CreateTaskRequest createTaskRequest;

    @BeforeEach
    void setUp() {
        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("title");
        createTaskRequest.setDescription("description");
    }

    @Test
    void testCreateTask_WithoutAuth_ShouldNotPass() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void testCreateTask_WithAuth_ShouldPass() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest))
                // .with(csrf())
        ).andExpect(status().isCreated());
    }

}