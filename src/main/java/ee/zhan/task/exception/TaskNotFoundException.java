package ee.zhan.task.exception;

import ee.zhan.common.security.exception.AppException;
import org.springframework.http.HttpStatus;

public class TaskNotFoundException extends AppException {
    public TaskNotFoundException() {
        super("Task not found", HttpStatus.NOT_FOUND);
    }
}
