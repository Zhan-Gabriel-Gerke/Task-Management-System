package ee.zhan.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthUtilsTest {

    @Test
    void testNormalizeEmail_whenGivenUnnormallEmail_shouldReturnLowerCaseEmail() {
        String dataToConvert = "ZhAN@gmaIl.com";
        String expected = "zhan@gmail.com";

        String result = AuthUtils.normalizeEmail(dataToConvert);

        assertEquals(expected, result);
    }

    @Test
    void testNormalizeEmail_whenGivenEmailWithSpaces_shouldReturnEmailWithoutSpaces() {
        String dataToConvert = "  zhan@gmail.com   ";
        String expected = "zhan@gmail.com";

        String result = AuthUtils.normalizeEmail(dataToConvert);

        assertEquals(expected, result);
    }

    @Test
    void testNormalizeEmail_whenGivenNull_shouldReturnError() {
        // Arrange
        String dataToConvert = null;

        // Act & Assert

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AuthUtils.normalizeEmail(dataToConvert);
        });
    }

    @Test
    void testNormalizeEmail_whenEmailIsBlank_shouldReturnError() {
        String dataToConvert = " ";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            AuthUtils.normalizeEmail(dataToConvert);
        });
    }
}