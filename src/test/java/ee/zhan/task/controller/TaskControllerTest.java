package ee.zhan.task.controller;

import ee.zhan.common.BaseControllerTest;
import ee.zhan.common.security.AppUserAdapter;
import ee.zhan.task.dto.*;
import ee.zhan.task.entity.TaskStatus;
import ee.zhan.task.exceptions.TaskNotFoundException;
import ee.zhan.task.mapper.TaskWebMapper;
import ee.zhan.common.security.SecurityConfig;
import ee.zhan.task.service.TaskService;
import ee.zhan.user.entity.AppUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@Import(SecurityConfig.class)
class TaskControllerTest extends BaseControllerTest {

    @MockitoBean private TaskWebMapper taskWebMapper;
    @MockitoBean private TaskService taskService;

    private CreateTaskRequest createTaskRequest;
    private AppUserAdapter userAdapter;

    @BeforeEach
    void setUp() {
        createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("title");
        createTaskRequest.setDescription("description");

        AppUserEntity fakeUser = new AppUserEntity();
        fakeUser.setId(1L);
        fakeUser.setEmail("test@test.com");

        userAdapter = new AppUserAdapter(fakeUser);
    }

    @Test
    void testCreateTask_WithoutAuth_ShouldNotPass() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    void testCreateTask_WithAuth_ShouldPass() throws Exception {
        mockMvc.perform(post("/api/tasks")
                .with(user(userAdapter))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createTaskRequest))
                // .with(csrf())
        ).andExpect(status().isCreated());
    }

    @Test
    void testGetTaskById_WithAuth_ShouldPass() throws Exception {
        //Arrange
        var mockResponse = new TaskSummaryResponse();
        mockResponse.setTitle("title");
        mockResponse.setDescription("description");
        mockResponse.setStatus(TaskStatus.CREATED);
        mockResponse.setAuthor(new UserDto());
        mockResponse.getAuthor().setId(1L);
        mockResponse.getAuthor().setEmail("email");

        Mockito.when(taskService.getTaskById(1L)).thenReturn(mockResponse);

        //Act
        String content = mockMvc.perform(get("/api/tasks/1")
                .with(user(userAdapter))
                .contentType(MediaType.APPLICATION_JSON))
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
    void shouldReturn404WhenTaskNotFound() throws Exception {
        Mockito.when(taskService.getTaskById(999L)).thenThrow(new TaskNotFoundException());

        mockMvc.perform(get("/api/tasks/999")
                .with(user(userAdapter)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTask_WithoutFilters_ShouldReturnDefaultPage() throws Exception {
        //Arrange
        TaskSummaryResponse task = new TaskSummaryResponse();
        task.setTitle("title");

        Page<TaskSummaryResponse> mackPage = new PageImpl<>(List.of(task), PageRequest.of(0, 10), 1);

        //Expect that service will be called with null-parametrs for filters
        Mockito.when(taskService.getTasks(isNull(), isNull(), any(Pageable.class))).thenReturn(mackPage);

        //Act & Assert
        mockMvc.perform(get("/api/tasks")
                .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.pageable.pageNumber").value(0));
    }

    @Test
    void testGetTask_WithFiltersAndPagination_ShouldPassParamsToService() throws Exception {
        //Arrange
        String authorEmail = "author@test.com";
        String assigneeEmail = "assignee@test.com";

        TaskSummaryResponse task = new TaskSummaryResponse();
        task.setTitle("Filtered Task");
        Page<TaskSummaryResponse> mockPage = new PageImpl<>(List.of(task), PageRequest.of(0, 10), 1);

        Mockito.when(taskService.getTasks(eq(authorEmail), eq(assigneeEmail), any()))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .with(user(userAdapter))
                        .param("author", authorEmail)
                        .param("assignee", assigneeEmail)
                        .param("page", "2")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Filtered Task"));
        Mockito.verify(taskService).getTasks(eq(authorEmail), eq(assigneeEmail), any(Pageable.class));
    }

    @Test
    void testGetTasks_WithOnlyOneFilter_ShouldHandleCorrectly() throws Exception {
        // Arrange
        String assigneeEmail = "assignee@test.com";
        Page<TaskSummaryResponse> mockPage = new PageImpl<>(List.of());

        // Author = null, Assignee = assignee@test.com
        Mockito.when(taskService.getTasks(isNull(), eq(assigneeEmail), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                .with(user(userAdapter))
                .param("assignee", assigneeEmail))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void updateTaskStatus_WithValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        Long taskId = 1L;
        UpdateTaskStatus request = new UpdateTaskStatus();
        request.setStatus(TaskStatus.IN_PROGRESS);

        TaskSummaryResponse mockResponse = new TaskSummaryResponse();
        mockResponse.setStatus(TaskStatus.IN_PROGRESS);

        Mockito.when(taskService.updateTaskStatus(eq(taskId), eq(TaskStatus.IN_PROGRESS), any()))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/status", taskId)
                        .with(user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void updateTaskStatus_WithInvalidRequest_ShouldReturnBadRequest() throws Exception {
        UpdateTaskStatus request = new UpdateTaskStatus();
        request.setStatus(null);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/status", 1L)
                        .with(user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(taskService, Mockito.never()).updateTaskStatus(any(), any(), any());
    }

    @Test
    void updateTaskAssignee_WithValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        Long taskId = 1L;
        String newAssigneeEmail = "new_dev@test.com";

        UpdateTaskAssignee request = new UpdateTaskAssignee();
        request.setAssigneeEmail(newAssigneeEmail);

        TaskSummaryResponse mockResponse = new TaskSummaryResponse();
        mockResponse.setTitle("Title");

        Mockito.when(taskService.updateTaskAssignee(eq(taskId), eq(newAssigneeEmail), any()))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(patch("/api/tasks/{id}/assignee", taskId)
                        .with(user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void testCreateComment_WithValidRequest_ShouldReturnCreated() throws Exception {
        // Arrange
        Long taskId = 1L;
        CreateTaskComment request = new CreateTaskComment("Nice task!");

        // Act & Assert
        mockMvc.perform(post("/api/tasks/{id}/comments", taskId)
                        .with(user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        Mockito.verify(taskService).createTaskComment(eq("Nice task!"), eq(taskId), eq(userAdapter.getId()));
    }

    @Test
    void testCreateComment_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        Long taskId = 1L;
        CreateTaskComment request = new CreateTaskComment("Nice task!");

        // Act & Assert
        mockMvc.perform(post("/api/tasks/{id}/comments", taskId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        Mockito.verify(taskService, Mockito.never()).createTaskComment(any(), any(), any());
    }

    @Test
    void testCreateComment_WithEmptyText_ShouldReturnBadRequest() throws Exception {
        // Arrange
        Long taskId = 1L;
        CreateTaskComment request = new CreateTaskComment(""); // @NotBlank fails

        // Act & Assert
        mockMvc.perform(post("/api/tasks/{id}/comments", taskId)
                        .with(user(userAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        Mockito.verify(taskService, Mockito.never()).createTaskComment(any(), any(), any());
    }

    @Test
    void testGetTaskComments_WithAuth_ShouldReturnPageOfComments() throws Exception {
        // Arrange
        Long taskId = 1L;
        TaskCommentRespond mockComment = new TaskCommentRespond(1L, taskId, "Test comment", "author@test.com");
        Page<TaskCommentRespond> mockPage = new PageImpl<>(List.of(mockComment), PageRequest.of(0, 10), 1);

        Mockito.when(taskService.getTaskComments(eq(taskId), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}/comments", taskId)
                        .with(user(userAdapter))
                        .param("page", "0")
                        .param("size", "10")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].text").value("Test comment"))
                .andExpect(jsonPath("$.content[0].authorEmail").value("author@test.com"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void testGetTaskComments_WithoutAuth_ShouldReturnUnauthorized() throws Exception {
        // Arrange
        Long taskId = 1L;

        // Act & Assert
        mockMvc.perform(get("/api/tasks/{id}/comments", taskId))
                .andExpect(status().isUnauthorized());

        Mockito.verify(taskService, Mockito.never()).getTaskComments(any(), any());
    }

}