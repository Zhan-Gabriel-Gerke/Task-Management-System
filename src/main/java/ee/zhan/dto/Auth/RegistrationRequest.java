package ee.zhan.dto.Auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequest {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Size(min = 12, max = 64, message = "The password has to be from 12 to 64 characters")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{12,}$",
            message = "The password must contain at least one number, uppercase and lowercase letters, a special character, and no spaces."
    )
    private String password;
}
