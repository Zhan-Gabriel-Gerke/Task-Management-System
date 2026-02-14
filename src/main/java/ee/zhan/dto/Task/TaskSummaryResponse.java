package ee.zhan.dto.Task;

import ee.zhan.domain.TaskStatus;
import lombok.Data;

@Data
public class TaskSummaryResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private authorDto author;
}
