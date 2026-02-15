package ee.zhan.common.security.exception;

import java.time.LocalDateTime;

public record AppErrorResponse (
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) { }
