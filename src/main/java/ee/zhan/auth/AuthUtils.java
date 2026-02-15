package ee.zhan.auth;

import java.util.Locale;

public class AuthUtils {

    private AuthUtils() {}

    public static String normalizeEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
