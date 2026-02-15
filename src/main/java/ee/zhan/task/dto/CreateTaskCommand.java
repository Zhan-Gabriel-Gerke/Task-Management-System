package ee.zhan.task.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateTaskCommand {
    private String title;
    private String description;
    private Long authorId;
}
