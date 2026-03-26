package ee.zhan.task.controller;

import ee.zhan.common.AbstractIntegrationTest;
import ee.zhan.task.dto.CreateTaskRequest;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.common.security.AppUserAdapter;
import ee.zhan.task.dto.UpdateTaskAssignee;
import ee.zhan.task.dto.UpdateTaskStatus;
import ee.zhan.task.entity.TaskEntity;
import ee.zhan.task.entity.TaskStatus;
import ee.zhan.user.entity.AppUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TaskControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private PasswordEncoder passwordEncoder;

    private AppUserEntity author;
    private AppUserAdapter authorAdapter;
    private AppUserEntity assignee;
    private AppUserAdapter assigneeAdapter;

    @BeforeEach
    void setUp() {
        author = new AppUserEntity();
        author.setEmail(generateUniqueEmail());
        author.setPassword(passwordEncoder.encode("password"));
        author = appUserRepository.save(author);
        authorAdapter = new AppUserAdapter(author);

        assignee = new AppUserEntity();
        assignee.setEmail(generateUniqueEmail());
        assignee.setPassword(passwordEncoder.encode("password"));
        assignee = appUserRepository.save(assignee);
        assigneeAdapter = new AppUserAdapter(assignee);
    }

    @Test
    void testCreateTask_WhenGivenValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        var request = new CreateTaskRequest();
        request.setTitle("title");
        request.setDescription("description");

        // Act
        String content = mockMvc.perform(post("/api/tasks")
                .with(user(authorAdapter))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskSummaryResponse actual = objectMapper.readValue(content, TaskSummaryResponse.class);

        //Assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(request.getTitle(), actual.getTitle());
        Assertions.assertEquals(request.getDescription(), actual.getDescription());
        Assertions.assertEquals(author.getId(), actual.getAuthor().getId());
        Assertions.assertEquals(author.getEmail(), actual.getAuthor().getEmail());
        Assertions.assertNotNull(actual.getId());
    }

    @Test
    void testGetTaskById_WhenGivenValidId_ShouldReturnTask() throws Exception {
        // Arrange

        var task = new TaskEntity();
        task.setTitle("title");
        task.setDescription("description");
        task.setAuthor(author);
        task = taskRepository.save(task);

        // Act
        String content = mockMvc.perform(get("/api/tasks/" + task.getId())
                .with(user(authorAdapter))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        TaskSummaryResponse actual = objectMapper.readValue(content, TaskSummaryResponse.class);

        //Assert
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(task.getTitle(), actual.getTitle());
        Assertions.assertEquals(task.getDescription(), actual.getDescription());
        Assertions.assertEquals(author.getId(), actual.getAuthor().getId());
        Assertions.assertEquals(author.getEmail(), actual.getAuthor().getEmail());
    }

    @Test
    void testGetTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        long nonExistentId = 700L;

        mockMvc.perform(get("/api/tasks/" + nonExistentId)
                .with(user(authorAdapter)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTasks_WhenFilteredByEmail_ShouldReturnOnlyUserTasks() throws Exception {
        // Arrange
        createTestTask("My Task 1", author);
        createTestTask("My Task 2", author);

        AppUserEntity otherUser = createTestUser("other@test.com");
        createTestTask("Other User's Task", otherUser);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("author", author.getEmail())
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testGetTasks_WhenPaginated_ShouldReturnCorrectPage() throws Exception {
        // Arrange
        for (int i = 0; i < 7; i++) {
            createTestTask("Task " + i, author);
        }

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("page", "1")
                        .param("size", "5")
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.content.length()").value(2)); // Second page must have 2 elements
    }

    @Test
    void testGetTasks_WhenNoFilter_ShouldReturnAllTasks() throws Exception {
        // Arrange
        createTestTask("My Task", author);
        AppUserEntity otherUser = createTestUser("other@test.com");
        createTestTask("Other User's Task", otherUser);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    private AppUserEntity createTestUser(String email) {
        AppUserEntity user = new AppUserEntity();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        return appUserRepository.save(user);
    }

    private TaskEntity createTestTask(String title, AppUserEntity author) {
        TaskEntity task = new TaskEntity();
        task.setTitle(title);
        task.setDescription("Test Description");
        task.setAuthor(author);
        task.setStatus(TaskStatus.CREATED);
        return taskRepository.save(task);
    }

    private TaskEntity createTestTask(String title, AppUserEntity author, AppUserEntity assignee) {
        TaskEntity task = new TaskEntity();
        task.setTitle(title);
        task.setDescription("Test description");
        task.setAuthor(author);
        task.setAssignee(assignee);

        return taskRepository.save(task);
    }

    @Test
    void getTasks_WhenNoFiltersProvided_ShouldReturnAllTasks() throws Exception {
        // Arrange
        AppUserEntity otherUser = createTestUser("other@test.com");
        createTestTask("Task 1", author);
        createTestTask("Task 2", otherUser);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void getTasks_WhenFilteredByAssignee_ShouldReturnOnlyAssignedTasks() throws Exception {
        // Arrange
        AppUserEntity otherUser = createTestUser("other@test.com");
        createTestTask("Task for me", otherUser, author);
        createTestTask("Task for other", author, otherUser);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("assignee", author.getEmail())
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Task for me"));
    }

    @Test
    void getTasks_WhenFilteredByBothAuthorAndAssignee_ShouldReturnExactMatch() throws Exception {
        // Arrange
        AppUserEntity otherUser = createTestUser("other@test.com");

        createTestTask("My task, my execution", author, author);
        createTestTask("My task, their execution", author, otherUser);
        createTestTask("Their task, my execution", otherUser, author);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("author", author.getEmail())
                        .param("assignee", author.getEmail())
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("My task, my execution"));
    }

    @Test
    void getTasks_WhenPaginationIsRequested_ShouldReturnCorrectPageSize() throws Exception {
        // Arrange
        for (int i = 0; i < 15; i++) {
            createTestTask("Task " + i, author);
        }

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("page", "0")
                        .param("size", "10")
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(15))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.content.length()").value(10));
    }

    @Test
    void shouldReturnTasksFilteredByAuthorAndAssignee() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setTitle("Integration Task");
        task.setAuthor(author);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.CREATED);
        taskRepository.save(task);

        mockMvc.perform(get("/api/tasks")
                        .param("author", author.getEmail())
                        .param("assignee", assignee.getEmail())
                        .with(user(authorAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Integration Task"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void shouldUpdateStatusWhenUserIsAssignee() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setTitle("Status Task");
        task.setAuthor(author);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.CREATED);
        task = taskRepository.save(task);

        UpdateTaskStatus request = new UpdateTaskStatus();
        request.setStatus(TaskStatus.IN_PROGRESS);

        mockMvc.perform(patch("/api/tasks/" + task.getId() + "/status")
                        .with(user(assigneeAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void shouldReturnForbiddenWhenNonParticipantUpdatesStatus() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setTitle("Private Task");
        task.setAuthor(author);
        taskRepository.save(task);

        AppUserEntity stranger = new AppUserEntity();
        stranger.setEmail("stranger@test.com");
        stranger = appUserRepository.save(stranger);
        AppUserAdapter strangerAdapter = new AppUserAdapter(stranger);

        UpdateTaskStatus request = new UpdateTaskStatus();
        request.setStatus(TaskStatus.COMPLETED);

        mockMvc.perform(patch("/api/tasks/" + task.getId() + "/status")
                        .with(user(strangerAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldUpdateAssigneeWhenUserIsAuthor() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setTitle("Assignee Task");
        task.setAuthor(author);
        task = taskRepository.save(task);

        UpdateTaskAssignee request = new UpdateTaskAssignee();
        request.setAssigneeEmail(assignee.getEmail());

        mockMvc.perform(patch("/api/tasks/" + task.getId() + "/assignee")
                        .with(user(authorAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnForbiddenWhenAssigneeTriesToChangeAssignee() throws Exception {
        TaskEntity task = new TaskEntity();
        task.setTitle("Only Author Can Change Me");
        task.setAuthor(author);
        task.setAssignee(assignee);
        task = taskRepository.save(task);

        UpdateTaskAssignee request = new UpdateTaskAssignee();
        request.setAssigneeEmail("someone@test.com");

        mockMvc.perform(patch("/api/tasks/" + task.getId() + "/assignee")
                        .with(user(assigneeAdapter))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
