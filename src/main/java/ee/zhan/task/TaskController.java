package ee.zhan.task;

import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.CreateTaskRequest;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.mapper.TaskWebMapper;
import ee.zhan.user.AppUserAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/tasks")
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final TaskWebMapper taskWebMapper;

    @PostMapping()
    public ResponseEntity<TaskSummaryResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal AppUserAdapter userAdapter
    ) {
        // Convert request to command
        CreateTaskCommand command = taskWebMapper.toCommand(request, userAdapter);
        // Create task
        TaskSummaryResponse response = taskService.create(command);
        // Return response
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskSummaryResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }
}

