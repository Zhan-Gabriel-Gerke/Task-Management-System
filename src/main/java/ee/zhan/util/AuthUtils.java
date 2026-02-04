package ee.zhan.util;

import java.util.Locale;

public class AuthUtils {

    private AuthUtils() {}

    public static String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
