package ee.zhan.dto.Task;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;
}
