package ee.zhan.service.Task;

import com.github.dockerjava.api.model.AuthResponse;
import ee.zhan.AbstractIntegrationTest;
import ee.zhan.dto.Task.CreateTaskCommand;
import ee.zhan.dto.Task.TaskSummaryResponse;
import ee.zhan.entity.AppUserEntity;
import ee.zhan.entity.TaskEntity;
import ee.zhan.repository.AppUserRepository;
import ee.zhan.repository.TaskRepository;
import ee.zhan.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.support.TransactionTemplate;

public class TaskServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired private TaskService taskService;
    @Autowired private TaskRepository taskRepository;
    @Autowired private AppUserRepository appUserRepository;
    @Autowired private TransactionTemplate transactionTemplate;

    private AppUserEntity createAppUserEntity() {
        AppUserEntity appUserEntity = new AppUserEntity();
        appUserEntity.setEmail(generateUniqueEmail());
        appUserEntity.setPassword("SomeCoolPassword123!");
        return appUserRepository.save(appUserEntity);
    }

    @Test
    void shouldCreateTask() {
        // Arrange
        AppUserEntity user = createAppUserEntity();
        CreateTaskCommand createTaskCommand = new CreateTaskCommand();
        createTaskCommand.setTitle("title");
        createTaskCommand.setDescription("description");
        createTaskCommand.setAuthorId(user.getId());

        // Act & Assert
        TaskSummaryResponse response = taskService.create(createTaskCommand);
        transactionTemplate.execute(status -> {
            TaskEntity savedTask = taskRepository.findById(response.getId()).orElseThrow();
            Assertions.assertNotNull(savedTask);
            Assertions.assertEquals(createTaskCommand.getTitle(), savedTask.getTitle());
            Assertions.assertEquals(createTaskCommand.getDescription(), savedTask.getDescription());
            Assertions.assertEquals(user.getId(), savedTask.getAuthor().getId());
            Assertions.assertEquals(user.getEmail(), savedTask.getAuthor().getEmail());
            return null;
        });
    }
}
