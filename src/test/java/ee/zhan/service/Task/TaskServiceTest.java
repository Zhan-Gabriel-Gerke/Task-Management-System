package ee.zhan.service.Task;

import ee.zhan.domain.TaskStatus;
import ee.zhan.dto.Task.CreateTaskCommand;
import ee.zhan.dto.Task.TaskSummaryResponse;
import ee.zhan.entity.AppUserEntity;
import ee.zhan.entity.TaskEntity;
import ee.zhan.mapper.Task.TaskEntityMapper;
import ee.zhan.mapper.Task.TaskSummaryMapper;
import ee.zhan.repository.TaskRepository;
import ee.zhan.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

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