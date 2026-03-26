package ee.zhan.task.service;

import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.entity.TaskEntity;
import ee.zhan.task.entity.TaskStatus;
import ee.zhan.task.exceptions.AccessDenied;
import ee.zhan.task.exceptions.TaskNotFoundException;
import ee.zhan.task.exceptions.UserWasNotFound;
import ee.zhan.task.repository.TaskRepository;
import ee.zhan.user.entity.AppUserEntity;
import ee.zhan.task.mapper.TaskEntityMapper;
import ee.zhan.task.mapper.TaskSummaryMapper;
import ee.zhan.user.repository.AppUserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private AppUserRepository userRepository;
    @InjectMocks private TaskService taskService;
    @Captor private ArgumentCaptor<TaskEntity> taskCaptor;
    @Spy private TaskSummaryMapper taskSummaryMapper = Mappers.getMapper(TaskSummaryMapper.class);
    @Mock private TaskEntityMapper taskEntityMapper;
    private CreateTaskCommand createTaskCommand;

    private final Long TASK_ID = 1L;
    private final Long AUTHOR_ID = 100L;
    private final Long ASSIGNEE_ID = 200L;
    private final Long HACKER_ID = 999L;

    private TaskEntity createMockTask() {
        AppUserEntity author = new AppUserEntity();
        author.setId(AUTHOR_ID);

        AppUserEntity assignee = new AppUserEntity();
        assignee.setId(ASSIGNEE_ID);
        assignee.setEmail("assignee@test.com");

        TaskEntity task = new TaskEntity();
        task.setId(TASK_ID);
        task.setAuthor(author);
        task.setAssignee(assignee);
        task.setStatus(TaskStatus.CREATED);
        return task;
    }

    @BeforeEach
    void setUp() {
        createTaskCommand = new CreateTaskCommand();
        createTaskCommand.setTitle("title");
        createTaskCommand.setDescription("description");
        createTaskCommand.setAuthorId(1L);
    }

    @Test
    void someNameIforTest() {
        //Arrange
        TaskEntity entity = new TaskEntity();
        Long idOfTask = 1L;
        entity.setId(idOfTask);
        entity.setTitle("title");
        entity.setDescription("description");
        entity.setStatus(TaskStatus.CREATED);
        entity.setAuthor(new AppUserEntity());
        entity.getAuthor().setId(1L);
        entity.getAuthor().setEmail("email");

        Mockito.when(taskRepository.findById(idOfTask)).thenReturn(Optional.of(entity));

        //Act
        TaskSummaryResponse response = taskService.getById(idOfTask);

        //Assert
        Assertions.assertNotNull(response);
        Assertions.assertEquals(response.getTitle(), entity.getTitle());
        Assertions.assertEquals(response.getDescription(), entity.getDescription());
        Assertions.assertEquals(response.getStatus(), entity.getStatus());
        Assertions.assertEquals(response.getAuthor().getId(), entity.getAuthor().getId());
        Assertions.assertEquals(response.getAuthor().getEmail(), entity.getAuthor().getEmail());
    }

    @Test
    void shouldNotGiveTask_whenGivenInValidId() {
        //Arrange
        Long idOfTask = 999L;
        Mockito.when(taskRepository.findById(idOfTask)).thenThrow(new TaskNotFoundException());

        //Act && Assert
        Assertions.assertThrows(TaskNotFoundException.class, () ->
                taskService.getById(idOfTask));
    }

    @Test
    void shouldCreateTask_whenGivenValidDataAndAuth() {
        //Given
        AppUserEntity user = new AppUserEntity();
        user.setId(1L);
        user.setEmail("email");
        user.setPassword("password");

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTitle(createTaskCommand.getTitle());
        taskEntity.setDescription(createTaskCommand.getDescription());
        taskEntity.setAuthor(user);

        Mockito.when(taskEntityMapper.toEntity(createTaskCommand)).thenReturn(taskEntity);
        Mockito.when(taskRepository.save(any(TaskEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //When
        TaskSummaryResponse respond = taskService.create(createTaskCommand);

        //Then
        Mockito.verify(taskRepository).save(taskCaptor.capture());
        TaskEntity capturedTask = taskCaptor.getValue();

        //Check
        assertEquals(createTaskCommand.getTitle(), capturedTask.getTitle());
        assertEquals(createTaskCommand.getDescription(), capturedTask.getDescription());

        assertNotNull(respond);
        assertEquals(createTaskCommand.getTitle(), respond.getTitle());
        assertEquals(createTaskCommand.getDescription(), respond.getDescription());
        assertEquals(TaskStatus.CREATED, respond.getStatus());
        assertEquals(user.getId(), respond.getAuthor().getId());
        assertEquals(user.getEmail(), respond.getAuthor().getEmail());
    }

    @Test
    void updateStatus_WhenUserIsAuthor_ShouldUpdateAndReturnResponse() {
        //Arrange
        TaskEntity task = createMockTask();
        TaskSummaryResponse mockResponse = new TaskSummaryResponse();

        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        Mockito.doReturn(mockResponse).when(taskSummaryMapper).toSummaryResponse(task);

        //Act
        TaskSummaryResponse actualResponse = taskService.updateStatus(TASK_ID, TaskStatus.IN_PROGRESS, AUTHOR_ID);

        //Assert
        Assertions.assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
        Assertions.assertSame(mockResponse, actualResponse);
        Mockito.verify(taskSummaryMapper, Mockito.times(1)).toSummaryResponse(task);
    }

    @Test
    void updateStatus_WhenUserIsAssignee_ShouldUpdateAndReturnResponse() {
        // Arrange
        TaskEntity task = createMockTask();
        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        Mockito.when(taskSummaryMapper.toSummaryResponse(task)).thenReturn(new TaskSummaryResponse());

        // Act
        taskService.updateStatus(TASK_ID, TaskStatus.COMPLETED, ASSIGNEE_ID);

        // Assert
        Assertions.assertEquals(TaskStatus.COMPLETED, task.getStatus());
    }

    @Test
    void updateStatus_WhenUserIsNeitherAuthorNorAssignee_ShouldThrowAccessDenied() {
        // Arrange
        TaskEntity task = createMockTask();
        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

        // Act & Assert
        AccessDenied exception = Assertions.assertThrows(AccessDenied.class,
                () -> taskService.updateStatus(TASK_ID, TaskStatus.IN_PROGRESS, HACKER_ID));

        Assertions.assertTrue(exception.getMessage().contains("is not author or assignee"));
        Assertions.assertEquals(TaskStatus.CREATED, task.getStatus()); // Статус не поменялся
        Mockito.verify(taskSummaryMapper, Mockito.never()).toSummaryResponse(Mockito.any());
    }

    @Test
    void updateStatus_WhenStatusIsSame_ShouldNotModifyButReturnResponse() {
        // Arrange
        TaskEntity task = createMockTask();
        TaskSummaryResponse mockResponse = new TaskSummaryResponse();
        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        Mockito.doReturn(mockResponse)
                .when(taskSummaryMapper)
                .toSummaryResponse(task);

        // Act
        taskService.updateStatus(TASK_ID, TaskStatus.CREATED, AUTHOR_ID);

        // Assert
        Assertions.assertEquals(TaskStatus.CREATED, task.getStatus());
        Mockito.verify(taskSummaryMapper, Mockito.times(1)).toSummaryResponse(task);
    }

    @Test
    void updateAssignee_WhenUserIsAuthor_ShouldUpdateAssignee() {
        // Arrange
        String newAssigneeEmail = "new_guy@test.com";
        TaskEntity task = createMockTask();

        AppUserEntity newAssignee = new AppUserEntity();
        newAssignee.setEmail(newAssigneeEmail);

        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findByEmail(newAssigneeEmail)).thenReturn(Optional.of(newAssignee));
        Mockito.when(taskSummaryMapper.toSummaryResponse(task)).thenReturn(new TaskSummaryResponse());

        // Act
        taskService.updateAssignee(TASK_ID, newAssigneeEmail, AUTHOR_ID);

        // Assert
        Assertions.assertEquals(newAssignee, task.getAssignee());
    }

    @Test
    void updateAssignee_WhenEmailIsNull_ShouldUnassignTask() {
        // Arrange
        TaskEntity task = createMockTask(); // Изначально исполнитель есть
        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        Mockito.when(taskSummaryMapper.toSummaryResponse(task)).thenReturn(new TaskSummaryResponse());

        // Act
        taskService.updateAssignee(TASK_ID, null, AUTHOR_ID);

        // Assert
        Assertions.assertNull(task.getAssignee()); // Исполнитель стерт
        Mockito.verify(userRepository, Mockito.never()).findByEmail(Mockito.any());
    }

    @Test
    void updateAssignee_WhenUserIsAssigneeButNotAuthor_ShouldThrowAccessDenied() {
        // Arrange
        TaskEntity task = createMockTask();
        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));

        // Act & Assert
        AccessDenied exception = Assertions.assertThrows(AccessDenied.class,
                () -> taskService.updateAssignee(TASK_ID, "some@email.com", ASSIGNEE_ID));

        Assertions.assertTrue(exception.getMessage().contains("is not author"));
    }

    @Test
    void updateAssignee_WhenNewAssigneeNotFound_ShouldThrowException() {
        // Arrange
        String fakeEmail = "ghost@test.com";
        TaskEntity task = createMockTask();
        Mockito.when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(task));
        Mockito.when(userRepository.findByEmail(fakeEmail)).thenReturn(Optional.empty());

        // Act & Assert
        UserWasNotFound exception = Assertions.assertThrows(UserWasNotFound.class,
                () -> taskService.updateAssignee(TASK_ID, fakeEmail, AUTHOR_ID));

        Assertions.assertTrue(exception.getMessage().contains(fakeEmail));
        Assertions.assertNotEquals(fakeEmail, task.getAssignee().getEmail()); // Убеждаемся, что старый не стерся
    }
}