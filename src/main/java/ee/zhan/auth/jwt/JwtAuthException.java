package ee.zhan.auth.jwt;

import ee.zhan.common.exceptions.AppException;
import org.springframework.http.HttpStatus;

public class JwtAuthException extends AppException {
    public JwtAuthException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
