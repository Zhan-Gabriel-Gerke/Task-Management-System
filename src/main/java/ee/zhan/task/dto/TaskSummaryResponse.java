package ee.zhan.task.dto;

import ee.zhan.task.entity.TaskStatus;
import lombok.Data;

@Data
public class TaskSummaryResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private UserDto author;
    private UserDto assignee;
}
