package ee.zhan.service;

import ee.zhan.dto.Task.CreateTaskCommand;
import ee.zhan.dto.Task.TaskSummaryResponse;
import ee.zhan.entity.TaskEntity;
import ee.zhan.mapper.Task.TaskEntityMapper;
import ee.zhan.mapper.Task.TaskSummaryMapper;
import ee.zhan.repository.TaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
