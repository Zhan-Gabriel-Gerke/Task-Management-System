package ee.zhan.task.exceptions;

import ee.zhan.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class AccessDenied extends AppException {
    public AccessDenied(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}