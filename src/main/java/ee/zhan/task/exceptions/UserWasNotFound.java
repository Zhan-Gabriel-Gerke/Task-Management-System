package ee.zhan.task.exceptions;

import ee.zhan.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class UserWasNotFound extends AppException {
    public UserWasNotFound(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
