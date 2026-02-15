package ee.zhan.task;

import ee.zhan.common.AbstractIntegrationTest;
import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.user.AppUserEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TaskServiceIntegrationTest extends AbstractIntegrationTest {

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
