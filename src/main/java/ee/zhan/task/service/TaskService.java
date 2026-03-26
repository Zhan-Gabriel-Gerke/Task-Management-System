package ee.zhan.task.service;

import ee.zhan.task.entity.TaskStatus;
import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.entity.TaskEntity;
import ee.zhan.task.exceptions.AccessDenied;
import ee.zhan.task.exceptions.TaskNotFoundException;
import ee.zhan.task.exceptions.UserWasNotFound;
import ee.zhan.task.mapper.TaskEntityMapper;
import ee.zhan.task.mapper.TaskSummaryMapper;
import ee.zhan.task.repository.TaskRepository;
import ee.zhan.user.entity.AppUserEntity;
import ee.zhan.user.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static ee.zhan.task.repository.TaskSpecifications.hasAssigneeEmail;
import static ee.zhan.task.repository.TaskSpecifications.hasAuthorEmail;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository repository;
    private final AppUserRepository userRepository;
    private final TaskEntityMapper taskEntityMapper;
    private final TaskSummaryMapper taskSummaryMapper;

    @Transactional
    public TaskSummaryResponse create(CreateTaskCommand command) {
        TaskEntity entity = taskEntityMapper.toEntity(command);

        if (command.getAssigneeEmail() != null) {
            AppUserEntity assignee = userRepository.findByEmail(command.getAssigneeEmail()).orElseThrow(() ->
                    new UserWasNotFound("Assignee with id " + command.getAssigneeEmail() + " was not found"));
            entity.setAssignee(assignee);
        }

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

    public Page<TaskSummaryResponse> getTasks(String author, String assignee,Pageable pageable) {
        Specification<TaskEntity> spec = Specification.allOf(
                hasAuthorEmail(author),
                hasAssigneeEmail(assignee)
        );
        Page<TaskEntity> tasksPage = repository.findAll(spec, pageable);
        return tasksPage.map(taskSummaryMapper::toSummaryResponse);
    }

    @Transactional
    public TaskSummaryResponse updateStatus(Long taskId, TaskStatus newStatus, Long userId) {
        //Get task from database
        TaskEntity task = repository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        //Check if user is author or assignee of the task
        boolean isAuthor = task.getAuthor().getId().equals(userId);
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getId().equals(userId);

        //If user is not author or assignee, throw exception
        if (!isAuthor && !isAssignee) {
            throw new AccessDenied
                    ("User with id " + userId + " is not author or assignee of the task and cannot change status");
        }

        //If task already has the same status, return it without updating
        if (task.getStatus() == newStatus) {
            return taskSummaryMapper.toSummaryResponse(task);
        }

        //Update task status and save to database
        task.setStatus(newStatus);
        return taskSummaryMapper.toSummaryResponse(task);
    }

    @Transactional
    public TaskSummaryResponse updateAssignee(Long taskId, String userEmail, Long userId) {
        //Get task from database
        TaskEntity task = repository.findById(taskId)
                .orElseThrow(TaskNotFoundException::new);

        //Check if user is author of the task
        boolean isAuthor = task.getAuthor().getId().equals(userId);

        //If user is not author, throw exception
        if (!isAuthor) {
            throw new AccessDenied
                    ("User with id " + userId + " is not author of the task and cannot change assignee");
        }

        if (userEmail == null) {
            //If assigneeId is null, unassign the task
            task.setAssignee(null);
            return taskSummaryMapper.toSummaryResponse(task);
        }

        //Get assignee from database
        AppUserEntity assignee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserWasNotFound("User with email" + userEmail + " was not found"));

        //If task already has the same assignee, return it without updating
        if (task.getAssignee() != null && assignee.getEmail().equals(task.getAssignee().getEmail())) {
            return taskSummaryMapper.toSummaryResponse(task);
        }

        //Update task assignee and save to database
        task.setAssignee(assignee);
        return taskSummaryMapper.toSummaryResponse(task);
    }
}
