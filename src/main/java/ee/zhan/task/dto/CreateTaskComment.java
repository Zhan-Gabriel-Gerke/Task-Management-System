package ee.zhan.task.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateTaskComment(@NotBlank String text) {
}
