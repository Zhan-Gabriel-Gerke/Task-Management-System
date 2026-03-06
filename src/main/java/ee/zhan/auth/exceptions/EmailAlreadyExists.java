package ee.zhan.auth.exceptions;

import ee.zhan.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyExists extends AppException {
    public EmailAlreadyExists() {
        super("Email already exists", HttpStatus.BAD_REQUEST);
    }
}
