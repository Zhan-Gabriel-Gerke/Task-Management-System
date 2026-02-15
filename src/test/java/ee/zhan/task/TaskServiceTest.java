package ee.zhan.task;

import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.exception.TaskNotFoundException;
import ee.zhan.user.AppUserEntity;
import ee.zhan.task.mapper.TaskEntityMapper;
import ee.zhan.task.mapper.TaskSummaryMapper;
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

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    @Captor
    private ArgumentCaptor<TaskEntity> taskCaptor;

    private CreateTaskCommand createTaskCommand;

    @Spy
    private TaskSummaryMapper taskSummaryMapper = Mappers.getMapper(TaskSummaryMapper.class);

    @Mock
    private TaskEntityMapper taskEntityMapper;

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

        Mockito.when(repository.findById(idOfTask)).thenReturn(Optional.of(entity));

        //Act
        TaskSummaryResponse response = service.getById(idOfTask);

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
        Mockito.when(repository.findById(idOfTask)).thenThrow(new TaskNotFoundException());

        //Act && Assert
        Assertions.assertThrows(TaskNotFoundException.class, () ->
                service.getById(idOfTask));
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
        Mockito.when(repository.save(any(TaskEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        //When
        TaskSummaryResponse respond = service.create(createTaskCommand);

        //Then
        Mockito.verify(repository).save(taskCaptor.capture());
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
}