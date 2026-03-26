package ee.zhan.task.dto;

import ee.zhan.task.entity.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskStatus {
    @NotNull(message = "Status cannot be null or missing")
    private TaskStatus status;
}
