package ee.zhan.task;

import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.exception.TaskNotFoundException;
import ee.zhan.task.mapper.TaskEntityMapper;
import ee.zhan.task.mapper.TaskSummaryMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final TaskEntityMapper taskEntityMapper;
    private final TaskSummaryMapper taskSummaryMapper;

    @Transactional
    public TaskSummaryResponse create(CreateTaskCommand command) {
        TaskEntity entity = taskEntityMapper.toEntity(command);
        entity = repository.save(entity);
        return taskSummaryMapper.toSummaryResponse(entity);
    }

    public TaskSummaryResponse getById(Long id) {
        Optional<TaskEntity> entity = repository.findById(id);
        if (entity.isEmpty()) {
            throw new TaskNotFoundException();
        }
        return taskSummaryMapper.toSummaryResponse(entity.get());
    }
}
