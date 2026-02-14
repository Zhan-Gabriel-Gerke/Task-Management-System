package ee.zhan.exception.Auth;

import ee.zhan.exception.AppException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExists extends AppException {
    public EmailAlreadyExists() {
        super("Email already exists", HttpStatus.BAD_REQUEST);
    }
}
