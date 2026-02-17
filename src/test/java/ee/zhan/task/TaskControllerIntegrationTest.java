package ee.zhan.task;

import ee.zhan.common.AbstractIntegrationTest;
import ee.zhan.task.dto.CreateTaskRequest;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.user.AppUserAdapter;
import ee.zhan.user.AppUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TaskControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private PasswordEncoder passwordEncoder;

    private AppUserEntity appUserEntity;
    private AppUserAdapter userAdapter;

    @BeforeEach
    void setUp() {
        appUserEntity = new AppUserEntity();
        appUserEntity.setEmail(generateUniqueEmail());
        appUserEntity.setPassword(passwordEncoder.encode("password"));
        appUserEntity = appUserRepository.save(appUserEntity);
        userAdapter = new AppUserAdapter(appUserEntity);
    }

    @Test
    void testCreateTask_WhenGivenValidData_ShouldReturnCreated() throws Exception {
        // Arrange
        var request = new CreateTaskRequest();
        request.setTitle("title");
        request.setDescription("description");

        // Act
        String content = mockMvc.perform(post("/api/tasks")
                .with(user(userAdapter))
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
        Assertions.assertEquals(appUserEntity.getId(), actual.getAuthor().getId());
        Assertions.assertEquals(appUserEntity.getEmail(), actual.getAuthor().getEmail());
        Assertions.assertNotNull(actual.getId());
    }

    @Test
    void testGetTaskById_WhenGivenValidId_ShouldReturnTask() throws Exception {
        // Arrange

        var task = new TaskEntity();
        task.setTitle("title");
        task.setDescription("description");
        task.setAuthor(appUserEntity);
        task = taskRepository.save(task);

        // Act
        String content = mockMvc.perform(get("/api/tasks/" + task.getId())
                .with(user(userAdapter))
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
        Assertions.assertEquals(appUserEntity.getId(), actual.getAuthor().getId());
        Assertions.assertEquals(appUserEntity.getEmail(), actual.getAuthor().getEmail());
    }

    @Test
    void testGetTaskById_WhenTaskDoesNotExist_ShouldReturnNotFound() throws Exception {
        // Act & Assert
        long nonExistentId = 700L;

        mockMvc.perform(get("/api/tasks/" + nonExistentId)
                .with(user(userAdapter)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetTasks_WhenFilteredByEmail_ShouldReturnOnlyUserTasks() throws Exception {
        // Arrange
        createTestTask("My Task 1", appUserEntity);
        createTestTask("My Task 2", appUserEntity);

        AppUserEntity otherUser = createTestUser("other@test.com");
        createTestTask("Other User's Task", otherUser);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("email", appUserEntity.getEmail())
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void testGetTasks_WhenPaginated_ShouldReturnCorrectPage() throws Exception {
        // Arrange
        for (int i = 0; i < 7; i++) {
            createTestTask("Task " + i, appUserEntity);
        }

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .param("page", "1")
                        .param("size", "5")
                        .with(user(userAdapter)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(7))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.content.length()").value(2)); // Second page must have 2 elements
    }

    @Test
    void testGetTasks_WhenNoFilter_ShouldReturnAllTasks() throws Exception {
        // Arrange
        createTestTask("My Task", appUserEntity);
        AppUserEntity otherUser = createTestUser("other@test.com");
        createTestTask("Other User's Task", otherUser);

        // Act & Assert
        mockMvc.perform(get("/api/tasks")
                        .with(user(userAdapter)))
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
}
