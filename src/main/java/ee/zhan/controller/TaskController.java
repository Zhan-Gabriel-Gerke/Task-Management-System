package ee.zhan.controller;

import ee.zhan.dto.Task.CreateTaskCommand;
import ee.zhan.dto.Task.CreateTaskRequest;
import ee.zhan.dto.Task.TaskSummaryResponse;
import ee.zhan.mapper.Task.TaskWebMapper;
import ee.zhan.security.AppUserAdapter;
import ee.zhan.service.TaskService;
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

    @GetMapping()
    public ResponseEntity<TaskSummaryResponse> getTasks(@RequestParam Long id) {
        return null;
    }
}
