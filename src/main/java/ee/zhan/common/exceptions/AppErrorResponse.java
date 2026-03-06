package ee.zhan.common.exceptions;

import java.time.LocalDateTime;

public record AppErrorResponse (
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path
) { }
