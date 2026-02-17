package ee.zhan.task;

import ee.zhan.task.dto.CreateTaskCommand;
import ee.zhan.task.dto.CreateTaskRequest;
import ee.zhan.task.dto.TaskSummaryResponse;
import ee.zhan.task.mapper.TaskWebMapper;
import ee.zhan.user.AppUserAdapter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

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
        CreateTaskCommand command = taskWebMapper.toCommand(request, userAdapter);
        TaskSummaryResponse response = taskService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskSummaryResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<TaskSummaryResponse>> getTasks(
            @RequestParam(required = false) String email,
            @PageableDefault(sort = "id") Pageable pageable) {

        return ResponseEntity.ok(taskService.getByOptionalEmail(email, pageable));
    }
}

