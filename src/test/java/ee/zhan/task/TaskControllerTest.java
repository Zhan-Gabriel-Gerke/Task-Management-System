package ee.zhan.task;

import ee.zhan.task.dto.*;
import ee.zhan.task.exception.TaskNotFoundException;
import ee.zhan.task.mapper.TaskWebMapper;
import ee.zhan.common.security.SecurityConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    @WithMockUser
    void testGetTaskById_WithAuth_ShouldPass() throws Exception {
        //Arrange
        var mockResponse = new TaskSummaryResponse();
        mockResponse.setTitle("title");
        mockResponse.setDescription("description");
        mockResponse.setStatus(TaskStatus.CREATED);
        mockResponse.setAuthor(new AuthorDto());
        mockResponse.getAuthor().setId(1L);
        mockResponse.getAuthor().setEmail("email");

        Mockito.when(taskService.getById(1L)).thenReturn(mockResponse);

        //Act
        String content = mockMvc.perform(get("/api/tasks/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskSummaryResponse actual = objectMapper.readValue(content, TaskSummaryResponse.class);

        //Assert
        Assertions.assertEquals(mockResponse.getTitle(), actual.getTitle());
        Assertions.assertEquals(mockResponse.getDescription(), actual.getDescription());
        Assertions.assertEquals(mockResponse.getStatus(), actual.getStatus());
        Assertions.assertEquals(mockResponse.getAuthor().getId(), actual.getAuthor().getId());
        Assertions.assertEquals(mockResponse.getAuthor().getEmail(), actual.getAuthor().getEmail());
    }

    @Test
    @WithMockUser
    void shouldReturn404WhenTaskNotFound() throws Exception {
        Mockito.when(taskService.getById(999L)).thenThrow(new TaskNotFoundException());

        mockMvc.perform(get("/api/tasks/999"))
                .andExpect(status().isNotFound());
    }

}