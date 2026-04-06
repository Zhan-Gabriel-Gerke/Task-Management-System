package ee.zhan.task.controller;

import ee.zhan.task.service.TaskService;
import ee.zhan.task.dto.*;
import ee.zhan.task.mapper.TaskWebMapper;
import ee.zhan.common.security.AppUserAdapter;
import jakarta.servlet.ServletRequest;
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

    @PostMapping()//Endpoint for creating a task. Only authenticated users can create a task.
    public ResponseEntity<TaskSummaryResponse> createTask(
            @Valid @RequestBody CreateTaskRequest request,
            @AuthenticationPrincipal AppUserAdapter userAdapter
    ) {
        CreateTaskCommand command = taskWebMapper.toCommand(request, userAdapter);
        TaskSummaryResponse response = taskService.createTask(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")//Endpoint for getting task by id.
    // Only authenticated users can create a task.
    public ResponseEntity<TaskSummaryResponse> getTaskById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping//Endpoint for getting tasks.
    // Only authenticated users can create a task.
    // You can filter by email of author or / and assignee.
    public ResponseEntity<Page<TaskSummaryResponse>> getTasks(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String assignee,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(taskService.getTasks(author, assignee, pageable));
    }

    //Endpoint for updating task status. Only author and assignee can update the task.
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskSummaryResponse> updateTaskStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTaskStatus request,
            @AuthenticationPrincipal AppUserAdapter userAdapter) {

        TaskSummaryResponse response = taskService.updateTaskStatus(id, request.getStatus(), userAdapter.getId());
        return ResponseEntity.ok(response);
    }

    //Endpoint for updating task assignee. Only author can update the task.
    @PatchMapping("/{id}/assignee")
    public ResponseEntity<TaskSummaryResponse> updateTaskAssignee(
            @PathVariable Long id,
            @RequestBody UpdateTaskAssignee request,
            @AuthenticationPrincipal AppUserAdapter userAdapter) {

        TaskSummaryResponse response = taskService.updateTaskAssignee(id, request.getAssigneeEmail(), userAdapter.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("{id}/comments")
    public ResponseEntity<HttpStatus> createTaskComment(
            @PathVariable Long id,
            @Valid @RequestBody CreateTaskComment request,
            @AuthenticationPrincipal AppUserAdapter userAdapter) {

        taskService.createTaskComment(request.text(), id, userAdapter.getId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("{id}/comments")
    public ResponseEntity<Page<TaskCommentRespond>> getTaskComment(
            @PathVariable Long id,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        Page<TaskCommentRespond> respond = taskService.getTaskComments(id, pageable);
        return ResponseEntity.ok(respond);
    }
}

