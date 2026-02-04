package ee.zhan.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExists extends AppException {
    public EmailAlreadyExists() {
        super("Email already exists", HttpStatus.BAD_REQUEST);
    }
}
