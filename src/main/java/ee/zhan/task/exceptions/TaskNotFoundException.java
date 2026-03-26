package ee.zhan.task.exceptions;

import ee.zhan.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends AppException {
    public TaskNotFoundException() {
        super("Task not found", HttpStatus.NOT_FOUND);
    }
}
