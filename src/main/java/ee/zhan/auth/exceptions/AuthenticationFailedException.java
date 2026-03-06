package ee.zhan.auth.exceptions;

import ee.zhan.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends AppException {
    public AuthenticationFailedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
